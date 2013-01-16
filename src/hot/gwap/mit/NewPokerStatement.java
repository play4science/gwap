/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.mit;

import gwap.model.action.Bet;

import org.jboss.seam.annotations.Name;

/**
 * @author kneissl
 */
@Name("mitNewPokerStatement")
public class NewPokerStatement extends NewStatement {

	public NewPokerStatement() {
		super();
		points = Bet.POKER_POINTS;
	}
	
	@Override
	public String createStatement() {
		if (super.createStatement() != null)
			return "/pokerNewStatementCreated.xhtml";
		else 
			return null;
	}
}
