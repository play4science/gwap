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
