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

package gwap.model.action;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Scope;

@NamedQueries({
	@NamedQuery(name="pokerBet.byPerson", 
			query="select b from PokerBet b where (b.person = :person or exists (select id from Person p where p.personConnected = :person and p.id = b.person.id)) and b.revisedBet is null order by b.resource.text,b.created desc"),
	@NamedQuery(name="pokerBet.byResource", 
			query="select b from PokerBet b where b.resource = :resource and b.revisedBet is null"), 
	@NamedQuery(name="pokerBet.byResourceAndLocation",
			query="select b from PokerBet b where b.resource = :resource and b.revisedBet is null and b.location = :location")
})

/**
 * A bet designated for the poker game
 * 
 * @author Fabian Kneissl
 */
@Entity
@Scope(ScopeType.EVENT)
public class PokerBet extends Bet {
	
	private static final long serialVersionUID = 1L;
	
	public PokerBet() {
		points = 1;
	}
	
	@Override
	public void setPoints(Integer points) {
		throw new RuntimeException("Points of a PokerBet are not changeable");
	}
}
