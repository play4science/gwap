/*
 * This file is part of gwap
 *
 * Copyright (C) 2013
 * Project play4science
 * Lehr- und Forschungseinheit für Programmier- und Modellierungssprachen
 * Ludwig-Maximilians-Universität München
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
