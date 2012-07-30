/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.wrapper;

/**
 * @author Mislav Boras
 */
public class BackstageAnswer {
	private String term;
	private Integer appearence;
	
	public BackstageAnswer(String term, Integer appearence) {
		this.term = term;
		this.appearence = appearence;
	}
	
	public BackstageAnswer(String term, Long appearence) {
		this.term = term;
		this.appearence = appearence.intValue();
	}
	
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public Integer getAppearence() {
		return appearence;
	}
	public void setAppearence(Integer appearence) {
		this.appearence = appearence;
	}
	
}
