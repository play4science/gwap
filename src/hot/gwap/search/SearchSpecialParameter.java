/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
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