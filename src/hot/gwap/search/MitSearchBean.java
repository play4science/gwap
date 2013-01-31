/*
 * This file is part of gwap, an open platform for games with a purpose
 *
 * Copyright (C) 2013
 * Project play4science
 * Lehr- und Forschungseinheit für Programmier- und Modellierungssprachen
 * Ludwig-Maximilians-Universität München
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
 * @author Fabian Kneißl
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
