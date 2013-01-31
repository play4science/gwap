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

package gwap.model.action;


import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * This is general declaration of PerceptionPairs that must not be used in
 * games.
 * 
 * @author Jonas Hölzler
 */

@NamedQueries({ @NamedQuery(name = "PerceptionPair.all", query = "select t from PerceptionPair t"),
	@NamedQuery(name = "PerceptionPair.averageRatingByResourceAndPairname", query = "select avg(p.value) from PerceptionPair p join p.perceptionRating r where p.pairname = :pairname and r.resource = :resource")})
@Entity
public class PerceptionPair extends Action {

	private static final long serialVersionUID = 1L;

	private Long value;
	private String pairname;

	@ManyToOne
	private PerceptionRating perceptionRating;

	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}

	public String toString() {
		return "PerceptionPair:" + pairname + "=" + value;
	}

	public String getPairname() {
		return pairname;
	}

	public void setPairname(String pairname) {
		this.pairname = pairname;
	}

	public void setPerceptionRating(PerceptionRating perceptionRating) {
		this.perceptionRating = perceptionRating;
	}
	
	public PerceptionRating getPerceptionRating() {
		return perceptionRating;
	}

}
