/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.search;

import static com.google.common.base.Strings.isNullOrEmpty;

import org.apache.solr.client.solrj.SolrQuery;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * Provides search through the letters of the alphabet.
 * 
 * @author Fabian Kneißl
 */
@Name("mitAlphabeticSearchBean")
@Scope(ScopeType.PAGE)
public class MitAlphabeticSearchBean extends MitSearchBean {

	private static final long serialVersionUID = 3705006325134252092L;

	private static final String[] LETTERS = new String[] {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

	@Override
	public SolrQuery generateQuery() {
		if (isQueryEmpty()) {
			queryBean.setLetter("A");
		}
		String letter = queryBean.getLetter();
		letter = letter.toUpperCase();
		queryBean.setLetter(letter);
		if (!letter.matches("[a-zA-Z]")) {
			log.warn("Query by letter #0 not possible", letter);
			return null;
		}
		SolrQuery solrQuery = new SolrQuery("statement_untokenized:"+letter+"*");
		solrQuery.setParam("defType", "edismax");
		solrQuery.setParam("sort", "statement_untokenized asc");
		return solrQuery;
	}
	
	@Override
	protected boolean isQueryEmpty() {
		return isNullOrEmpty(queryBean.getLetter());
	}
	
	public String[] getLetters() {
		return LETTERS;
	}
}
