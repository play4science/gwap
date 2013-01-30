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
