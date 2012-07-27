/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.mit;


/**
 * @author kneissl
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