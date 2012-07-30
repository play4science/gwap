/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.wrapper;

/**
 * @author Fabian Kneißl, Mislav Boras
 */
public class BackstageResult {
	private Integer roundnumber;
	private Double averageScore;
	private Integer score;
	
	public BackstageResult(Integer number, Double averageScore) {
		this.roundnumber = number;
		if (averageScore != null) {
			this.averageScore = (double) Math.round(averageScore * 100) / 100;
		} else {
			this.averageScore = averageScore;
		}
	}
	
	public BackstageResult(Integer number, 
			Integer score){
		this.roundnumber = number;
		this.score = score;
	}
	
	public BackstageResult(Double averageScore){
		this.averageScore = averageScore;
	}
	
	public Integer getNumber() {
		return roundnumber;
	}
	public void setNumber(Integer number) {
		this.roundnumber = number;
	}
	
	
	public Double getAverageScore() {
		return averageScore;
	}
	public void setAverageScore(Double averageScore) {
		this.averageScore = averageScore;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	
}
