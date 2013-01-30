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


/**
 * This class represents a user-entered search term for which a 
 * corresponding term or sequence of terms for use in Solr is defined.
 * 
 * It consists of the search term itself (word), the replacement for that 
 * word, and optionally a Solr boost function.
 * 
 * @author Fabian Kneißl
 */
public class SearchSpecialParameter implements Comparable<SearchSpecialParameter> {
	private String word;
	private String queryReplacement;
	private String boostFunction;

	public SearchSpecialParameter(String word, String queryReplacement) {
		this.word = word;
		this.queryReplacement = queryReplacement;
	}
	public SearchSpecialParameter(String word, String queryReplacement, String boostFunction) {
		this.word = word;
		this.queryReplacement = queryReplacement;
		this.boostFunction = boostFunction;
	}
	/**
	 * Compare is solely based on the search term (word)
	 */
	public int compareTo(SearchSpecialParameter other) {
		return this.word.compareTo(other.word);
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public String getQueryReplacement() {
		return queryReplacement;
	}
	public void setQueryReplacement(String queryReplacement) {
		this.queryReplacement = queryReplacement;
	}
	public String getBoostFunction() {
		return boostFunction;
	}
	public void setBoostFunction(String boostFunction) {
		this.boostFunction = boostFunction;
	}
}
