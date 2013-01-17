/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.mit;

import gwap.model.action.Bet;
import gwap.wrapper.Score;

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

	private boolean isCorrect;
	
	@Override
	public void startGameSession() {
		startGameSession("mitPoker");
	}
	
	@Override
	public void startRound() {
		super.startRound();
		isCorrect = false;
	}
	
	@Override
	protected void loadNewResource() {
		statement = mitStatementBean.updateStatement();
		statement.getStatementStandardTokens().size();
	}
	
	public boolean assignLocation(Long locationId) {
		super.assignLocation(locationId);
		// different to Recognize, do scoring now
		Score score = mitPokerScoring.poker(locationAssignment);
		if (score != null && score.getScore() > 0) {
			addToScore(score.getScore());
			isCorrect = true;
			if (score.getScore() == PokerScoring.POKER_CORRECT_DIFFICULT) {
				facesMessages.addFromResourceBundle(Severity.INFO, "game.poker.correct.difficult");
			} else if (score.getScore() == PokerScoring.POKER_CORRECT) {
				facesMessages.addFromResourceBundle(Severity.INFO, "game.poker.correct.easy");
			}
		}
		entityManager.persist(locationAssignment);
		gameRound.getActions().add(locationAssignment);
		mitPokerScoring.updateScoreForPokerBets(locationAssignment.getResource());
		return true;
	}
	
	public void scoreAssignLocation() {
		// scoring already happened
	}
	
	public void createBet() {
		points = Bet.POKER_POINTS;
		super.createBet();
	}

	public boolean getIsCorrect() {
		return isCorrect;
	}

}
