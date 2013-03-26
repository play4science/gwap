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

package gwap.wrapper;


/**
 * @author maders, wieser
 */
public class UserStatistics {

	private Long score;
	private Long secondsPlayed;
	private Double coveredDistance;
	
	public UserStatistics(Long score, Number secondsPlayed, Double coveredDistance) {
		this.score = score;
		if (secondsPlayed != null)
			this.secondsPlayed = secondsPlayed.longValue();
		this.coveredDistance = coveredDistance;
	}

	public Long getScore() {
		return score;
	}

	public void setScore(Long score) {
		this.score = score;
	}

	public Long getSecondsPlayed() {
		return secondsPlayed;
	}

	public void setSecondsPlayed(Long secondsPlayed) {
		this.secondsPlayed = secondsPlayed;
	}

	public Double getCoveredDistance() {
		return coveredDistance;
	}

	public void setCoveredDistance(Double coveredDistance) {
		this.coveredDistance = coveredDistance;
	}
	
}
