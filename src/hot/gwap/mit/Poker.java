/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.mit;

import gwap.model.action.Bet;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessage.Severity;

/**
 * @author Fabian Kneißl
 */
@Name("mitPoker")
@Scope(ScopeType.CONVERSATION)
public class Poker extends Recognize {
	private static final long serialVersionUID = 1L;

	private boolean canCreateBet;
	
	@Override
	public void startGameSession() {
		startGameSession("mitPoker");
	}
	
	@Override
	public void startRound() {
		super.startRound();
		canCreateBet = false;
	}
	
	@Override
	protected void loadNewResource() {
		statement = mitStatementBean.updateStatement();
		statement.getStatementStandardTokens().size();
	}
	
	public boolean assignLocation(Long locationId) {
		super.assignLocation(locationId);
		// different to Recognize, do scoring now
		Integer score = mitPokerScoring.poker(locationAssignment);
		if (score != null) {
			addToScore(score);
			if (score > 0)
				facesMessages.addFromResourceBundle(Severity.INFO, "game.poker.correct");
		}
		if (score == null || score == 0) {
			facesMessages.addFromResourceBundle(Severity.INFO, "game.poker.wrong");
			canCreateBet = true;
		}
		entityManager.persist(locationAssignment);
		gameRound.getActions().add(locationAssignment);
		mitPokerScoring.updateScoreForBets(locationAssignment.getResource());
		return true;
	}
	
	public void scoreAssignLocation() {
		// scoring already happened
	}
	
	public void createBet() {
		points = Bet.POKER_POINTS;
		super.createBet();
	}

	public boolean getCanCreateBet() {
		return canCreateBet;
	}

}
