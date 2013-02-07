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

package gwap.mit;

import gwap.ResourceAcquisitionType;
import gwap.model.action.Bet;
import gwap.model.action.PokerBet;
import gwap.model.resource.Location;
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
		if (mitStatementBean.getAcquisitionType() != ResourceAcquisitionType.SENSIBLE_FOR_POKER)
			throw new NoSuchResourceException();
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
		if (locationAssignment == null)
			return;
		Location location = locationAssignment.getLocation();
		log.info("Creating poker bet for statement #1 and location #2", statement, location);
		Bet bet = new PokerBet();
		initializeAction(bet);
		bet.setLocation(location);
		bet.setResource(statement);
		scoreAssignLocation();
		
		entityManager.persist(bet);
		gameRound.getActions().add(bet);
		bet.setNotEvaluated(true);
		entityManager.flush();
		mitPokerScoring.updateScoreForBets(bet.getResource());
	}

	public boolean getIsCorrect() {
		return isCorrect;
	}
	
	public void noStatementAvailable() {
		facesMessages.addFromResourceBundle(Severity.INFO, "game.poker.noResourceLeft");
	}

}
