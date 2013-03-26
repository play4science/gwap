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

	private Object score;
	private Object timestamp;
	private Object coveredDistance;
	
	public UserStatistics(Object score, Object timestamp, Object coveredDistance) {
		System.out.println("STOP");
		this.score = score;
		this.timestamp = timestamp;
		this.coveredDistance = coveredDistance;
	}

	public Object getScore() {
		return score;
	}

	public void setScore(Object score) {
		this.score = score;
	}

	public Object getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Object timestamp) {
		this.timestamp = timestamp;
	}

	public Object getCoveredDistance() {
		return coveredDistance;
	}

	public void setCoveredDistance(Object coveredDistance) {
		this.coveredDistance = coveredDistance;
	}
	
}
