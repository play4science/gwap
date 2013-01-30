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

package gwap.mit;

import gwap.model.action.Bet;
import gwap.model.action.PokerBet;
import gwap.model.resource.Location;

import java.util.Date;

import org.jboss.seam.annotations.Name;

/**
 * @author kneissl
 */
@Name("mitNewPokerStatement")
public class NewPokerStatement extends NewStatement {

	public boolean assignLocation() {
		log.info("Trying to assign locationId #0 to statement #1", locationId, statement);
		if (locationId == null || locationId <= 0)
			return false;
		Location location = entityManager.find(Location.class, locationId);
		if (location == null)
			return false;
		Bet bet = new PokerBet();
		bet.setCreated(new Date());
		bet.setLocation(location);
		bet.setResource(statement);
		bet.setPerson(person);
		entityManager.persist(bet);
		log.info("Assigned location #0 to statement #1", location, statement);
		return true;
	}

	@Override
	public String createStatement() {
		if (super.createStatement() != null)
			return "/pokerNewStatementCreated.xhtml";
		else 
			return null;
	}
}
