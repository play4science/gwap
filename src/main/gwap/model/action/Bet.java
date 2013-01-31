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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Scope;

@NamedQueries({
	@NamedQuery(name="bet.byResource", 
			query="select b from Bet b where b.class=Bet and b.resource = :resource and b.revisedBet is null"), 
	@NamedQuery(name="bet.byPerson", 
			query="select b from Bet b where b.class=Bet and (b.person = :person or exists (select id from Person p where p.personConnected = :person and p.id = b.person.id)) and b.revisedBet is null order by b.resource.text,b.created desc"),
	@NamedQuery(name="bet.byPersonWithoutScore", 
			query="select b from Bet b where b.class=Bet and (b.person = :person or exists (select id from Person p where p.personConnected = :person and p.id = b.person.id)) and b.revisedBet is null " +
					"and b.score is null"), 
	@NamedQuery(name="bet.byResourceAndPerson", 
			query="select b from Bet b where b.class=Bet and b.resource = :resource and (b.person = :person or exists (select id from Person p where p.personConnected = :person and p.id = b.person.id)) and b.revisedBet is null"),
	@NamedQuery(name="bet.allWithPerson",
			query="select b from Bet b where b.class=Bet and b.person is not null"),
	@NamedQuery(name="bet.byScore",
			query="select b from Bet b where b.class=Bet and b.score != null and b.person != null order by b.score desc")
})

/**
 * A location is assigned to a statement. It can have a certain type if,
 * e.g., it is set by administrators
 * 
 * @author Fabian Kneißl
 */
@Entity
//@Name("bet")
@Scope(ScopeType.EVENT)
public class Bet extends LocationAssignment {
	
	private static final long serialVersionUID = 1L;

	protected Integer points;
	
	protected Integer currentMatch;
	
	@OneToOne private Bet revisedBet;
	
	public Integer getPoints() {
		return points;
	}
	public void setPoints(Integer points) {
		this.points = points;
	}	
	public Integer getCurrentMatch() {
		return currentMatch;
	}
	public void setCurrentMatch(Integer currentMatch) {
		this.currentMatch = currentMatch;
	}
	public Bet getRevisedBet() {
		return revisedBet;
	}
	public void setRevisedBet(Bet revisedBet) {
		this.revisedBet = revisedBet;
	}
	@Override
	public String toString() {
		return "Bet#"+getId();
	}
	
}
