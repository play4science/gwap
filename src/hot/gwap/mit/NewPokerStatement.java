/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
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
