/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.wrapper;

/**
 * @author Fabian Kneissl
 */
public class Score {
	
	private Integer score;
	
	private Percentage percentage;

	public Score() { }
	
	public Score(Integer score, Percentage percentage) {
		this.score = score;
		this.percentage = percentage;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public Percentage getPercentage() {
		return percentage;
	}

	public void setPercentage(Percentage percentage) {
		this.percentage = percentage;
	}
	
}
