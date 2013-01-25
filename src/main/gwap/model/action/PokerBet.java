/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
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
