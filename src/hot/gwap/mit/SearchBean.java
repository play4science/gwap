/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.mit;

import gwap.model.action.Bet;
import gwap.model.resource.Statement;
import gwap.widget.SolrSearchBean;

import java.io.Serializable;
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
public class SearchBean extends SolrSearchBean implements Serializable {

	private static final long serialVersionUID = -6337457799491522338L;
	protected static final List<SearchSpecialParameter> specialWords;
	private Statement selectedStatement;
	private Bet selectedBet;
	@In
	EntityManager entityManager;

	
	
	static {
		specialWords = new ArrayList<SearchSpecialParameter>();
		specialWords.add(new SearchSpecialParameter("femminile", 	"gender:[* TO -1]",		"product(scale(gender_ratingcount,1,10),linear(abs(gender),0.05,1))"));
		specialWords.add(new SearchSpecialParameter("maschile",		"gender:[1 TO *]",		"product(scale(gender_ratingcount,1,10),linear(abs(gender),0.05,1))"));
		specialWords.add(new SearchSpecialParameter("giovane",		"maturity:[* TO -1]",	"product(scale(maturity_ratingcount,1,10),linear(abs(maturity),0.05,1))"));
		specialWords.add(new SearchSpecialParameter("anziano",		"maturity:[1 TO *]",	"product(scale(maturity_ratingcount,1,10),linear(abs(maturity),0.05,1))"));
		specialWords.add(new SearchSpecialParameter("poco-istruito","cultivation:[* TO -1]","product(scale(cultivation_ratingcount,1,10),linear(abs(cultivation),0.05,1))"));
		specialWords.add(new SearchSpecialParameter("istruito",		"cultivation:[1 TO *]",	"product(scale(cultivation_ratingcount,1,10),linear(abs(cultivation),0.05,1))"));
	}
	
	@Override
	protected SolrQuery generateQuery() {
		// 1. Parse queryString and look for locations and special characterization words (variable "specialWords")
		String[] queryArray = queryString.split("\\s+");
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
		
		log.info("Ausgewählte id in showDetail(): " + selectedStatementId);
	}
	
	public Statement getSelectedStatement(){
		return selectedStatement;
	}
	
	public Bet getSelectedBet(){
		return selectedBet;
	}
	
}
