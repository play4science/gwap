/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.search;

import gwap.model.action.Bet;
import gwap.model.resource.Statement;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.solr.client.solrj.SolrQuery;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * @author Fabian Kneissl
 */
@Name("mitSearchBean")
@Scope(ScopeType.PAGE)
public class MitSearchBean extends SolrSearchBean {

	private static final long serialVersionUID = -6337457799491522338L;

	protected static final List<SearchSpecialParameter> specialWords;
	protected Statement selectedStatement;
	protected Bet selectedBet;
	@In
	protected EntityManager entityManager;

	
	static {
		specialWords = new ArrayList<SearchSpecialParameter>();
		specialWords.add(new SearchSpecialParameter("femminile", 	"gender:1"));
		specialWords.add(new SearchSpecialParameter("maschile",		"gender:2"));
		specialWords.add(new SearchSpecialParameter("giovane",		"maturity:1"));
		specialWords.add(new SearchSpecialParameter("anziano",		"maturity:2"));
		specialWords.add(new SearchSpecialParameter("poco-istruito","cultivation:1"));
		specialWords.add(new SearchSpecialParameter("istruito",		"cultivation:2"));
	}
	
	public MitSearchBean() {
		RESULTS_PER_PAGE = 10;
	}
	
	@Override
	public SolrQuery generateQuery() {
		if (isQueryEmpty())
			return null;
		// 1. Parse queryString and look for locations and special characterization words (variable "specialWords")
		String[] queryArray = queryBean.getQueryString().split("\\s+");
		String solrQueryString = "";
		String solrBoostString = null;
		for (int i = 0; i < queryArray.length; i++) {
			boolean found = false;
			for (SearchSpecialParameter special : specialWords) {
				if (special.getWord().equals(queryArray[i])) {
					solrQueryString += special.getQueryReplacement() + " ";
					solrBoostString = special.getBoostFunction();
					found = true;
				}
			}
			if (!found)
				solrQueryString += queryArray[i] + " ";
		}
		//TODO for locations
		
		SolrQuery solrQuery = new SolrQuery(solrQueryString);
		solrQuery.setParam("defType", "edismax");
		solrQuery.setParam("qf", "statement standard highlighted location_name");
		if (solrBoostString != null)
			solrQuery.setParam("bf", solrBoostString);
		return solrQuery;
	}
	
	
	public void showDetail(Long selectedStatementId) {
		selectedStatement = entityManager.find(Statement.class, selectedStatementId);
		
		Query q = entityManager.createNamedQuery("bet.byResourceAndPerson")
				.setParameter("person", person)
				.setParameter("resource", selectedStatement);
		List<Bet> betList  = q.getResultList();
		if (betList.size() > 0)
			selectedBet = betList.get(0);
		else
			selectedBet = null;
		
		log.info("Selected Statement in showDetail(): " + selectedStatementId);
	}
	
	public Statement getSelectedStatement(){
		return selectedStatement;
	}
	
	public Bet getSelectedBet(){
		return selectedBet;
	}
	
}
