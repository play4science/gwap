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

import gwap.NotEnoughDataException;
import gwap.game.AbstractGameSessionBean;
import gwap.model.GameRound;
import gwap.model.action.Action;
import gwap.model.action.Bet;
import gwap.model.action.Characterization;
import gwap.model.action.LocationAssignment;
import gwap.model.action.StatementAnnotation;
import gwap.model.resource.Location;
import gwap.model.resource.Statement;
import gwap.model.resource.StatementToken;
import gwap.tools.CharacterizationBean;
import gwap.wrapper.LocationPercentage;
import gwap.wrapper.Score;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessage.Severity;

/**
 * @author Fabian Kneißl
 */
@Name("mitRecognize")
@Scope(ScopeType.CONVERSATION)
public class Recognize extends AbstractGameSessionBean {
	private static final long serialVersionUID = 1L;

	@In(create=true)
	@Out					protected Statement statement;
	@In						protected StatementBean mitStatementBean;
	@In(required=false)@Out(required=false) protected Long locationId;
	@In						protected PokerScoring mitPokerScoring;
	@In                     protected CharacterizationBean characterizationBean;
	
	@Out
	protected List<Location> breadcrumbLocations = new ArrayList<Location>();
	protected Map<StatementToken, Boolean> selectedTokens = new HashMap<StatementToken, Boolean>();

	protected boolean skipCharacterizationResult;
	protected int roundNrUsingAtLeastAssignedStatement = 0;

	protected Characterization[] characterizations;
	protected StatementAnnotation statementAnnotation;
	protected LocationAssignment locationAssignment;
	protected Integer points;

	
	@Override
	public void startGameSession() {
		startGameSession("mitRecognize");
		if (gameType != null && gameType.getRounds() != null && gameType.getRounds() > 1)
			roundNrUsingAtLeastAssignedStatement = new Random().nextInt(gameType.getRounds()-1)+2; // not in first round
	}
	
	@Override
	public void startRound() {
		super.startRound();
		gameRound.getResources().add(statement);
		locationId = null;
		breadcrumbLocations.clear();
		characterizations = CharacterizationBean.createAndInitializeCharacterizations(statement);
		statementAnnotation = null;
		locationAssignment = null;
		points = null;
		selectedTokens.clear();
		skipCharacterizationResult = false;
	}
	
	@Override
	protected void loadNewResource() {
		if (roundNr > 1) {
			if (roundNr == roundNrUsingAtLeastAssignedStatement)
				statement = mitStatementBean.updateAtLeastAssignedStatement();
			else
				statement = mitStatementBean.updateStatement();
		}
		statement.getStatementStandardTokens().size();
	}
	
	public boolean assignLocation() {
		return assignLocation(locationId);
	}

	public boolean assignLocation(Long locationId) {
		if (locationId == null || locationAssignment != null)
			return false;
		this.locationId = locationId;
		Location location = entityManager.find(Location.class, locationId);
		if (location == null)
			return false;
		locationAssignment = new LocationAssignment();
		initializeAction(locationAssignment);
		locationAssignment.setLocation(location);
		locationAssignment.setResource(statement);
		log.info("Assigned location #0 to statement #1", location, statement);
		// wait with scoring and persisting until after bet has been created!
		return true;
	}
	
	public void scoreAssignLocation() {
		Score score = mitPokerScoring.locationAssignment(locationAssignment);
		if (score != null) {
			addToScore(score.getScore());
			if (score.getScore() != 0)
				facesMessages.addFromResourceBundle(Severity.INFO,"game.recognize.locationAssignment.high", score.getPercentage(), score.getScore());
		}
		if (score == null || score.getScore() == 0) {
			facesMessages.addFromResourceBundle(Severity.INFO, "game.recognize.gainedNoPoints");
			List<LocationPercentage> percentages = mitPokerScoring.getStatementPercentages(statement);
			for (LocationPercentage percentage : percentages) {
				if (percentage.getSum() == 1)
					facesMessages.addFromResourceBundle("bets.list.othersText.singularAll", 
							percentage.getSum(), percentage.getTotal(), percentage.getPercentage(), percentage.getLocation().getName());
				else
					facesMessages.addFromResourceBundle("bets.list.othersText.pluralAll", 
							percentage.getSum(), percentage.getTotal(), percentage.getPercentage(), percentage.getLocation().getName());
			}
		}
		entityManager.persist(locationAssignment);
		gameRound.getActions().add(locationAssignment);
		mitPokerScoring.updateScoreForBets(locationAssignment.getResource());
	}
	
	public List<Action> getAssignedLocations() {
		return gameRound.getActions();
	}
	
	public List<Location> addToBreadcrumbLocation(Long locationId) {
		Location l = entityManager.find(Location.class, locationId);
		if (l != null)
			breadcrumbLocations.add(l);
		return breadcrumbLocations;
	}
	
	public List<Location> navigateToBreadcrumbLocation(Long locationId) {
		for (int i = 0; i < breadcrumbLocations.size(); i++) {
			if (breadcrumbLocations.get(i).getId().equals(locationId)) {
				for (int j = i+1; j < breadcrumbLocations.size(); j++) {
					breadcrumbLocations.remove(j);
				}
				break;
			}
		}
		return breadcrumbLocations;
	}
	
	public void highlightWords() {
		boolean empty = true;
		for (StatementToken token : selectedTokens.keySet()) {
			if (selectedTokens.get(token)){
				empty = false; 
				break;
			} 
		}
		if(empty){
			log.info("Highlighting words NOT saved (empty)");
		}else{
			log.info("Highlighting words");
			if (statementAnnotation != null)
				return;
			statementAnnotation = new StatementAnnotation();
			initializeAction(statementAnnotation);
			statementAnnotation.setStatement(statement);
			if (locationId != null)
				statementAnnotation.setText(StatementAnnotation.LOCATED);
			else
				statementAnnotation.setText(StatementAnnotation.OTHER);
			entityManager.persist(statementAnnotation);
			gameRound.getActions().add(statementAnnotation);
			List<StatementToken> sat = statementAnnotation.getStatementTokens();
			for (StatementToken token : selectedTokens.keySet()) {
				if (selectedTokens.get(token))
					sat.add(token);
			}
			entityManager.flush();
			try {
				Integer score = mitPokerScoring.highlighting(statementAnnotation);
				addToScore(score);
				if (score > 0)
					facesMessages.addFromResourceBundle(Severity.INFO, "game.recognize.gainedPoints", score);
				else
					facesMessages.addFromResourceBundle(Severity.INFO, "game.recognize.gainedNoPoints");
				facesMessages.addFromResourceBundle(Severity.INFO, "game.recognize.highlighting.result", mitPokerScoring.getHighlightingPercentage(statementAnnotation));
			} catch (NotEnoughDataException e) {
				skipCharacterizationResult = true;
				facesMessages.addFromResourceBundle(Severity.INFO, "game.recognize.highlighting.notEnoughData");
			}
		}
	}
	
	public String characterize() {
		boolean noAnswerGiven = true;
		log.info("Saving characterization");
		for (Characterization c : characterizations) {
			if (CharacterizationBean.isValueSet(c)) {
				log.info("#0 characterized with #1=#2", statement, c.getName(), c.getValue());
				noAnswerGiven = false;
				initializeAction(c);
				c.setResource(statement);
				entityManager.persist(c);
				gameRound.getActions().add(c);
			}
		}
		entityManager.flush();
		characterizationBean.clearCache();
		try {
			Integer score = mitPokerScoring.characterization(statement, characterizations);
			addToScore(score);
			if (score > 0)
				facesMessages.addFromResourceBundle(Severity.INFO, "game.recognize.gainedPoints", score);
//			else
//				facesMessages.addFromResourceBundle(Severity.INFO, "game.recognize.gainedNoPoints");
//			facesMessages.addFromResourceBundle(Severity.INFO, "game.recognize.characterizationResult.seeAbove");
		} catch (NotEnoughDataException e) {
//			facesMessages.addFromResourceBundle(Severity.INFO, "game.recognize.characterizationResult.notEnoughData");
		}
		if (noAnswerGiven) {
			log.info("Characterization NOT saved (empty)");
			skipCharacterizationResult = true;
		}
		return "next";
	}
	
	public void createBet() {
		if (locationAssignment == null)
			return;
		Location location = locationAssignment.getLocation();
		log.info("Creating bet on #0% for statement #1 and location #2", points, statement, location);
		Bet bet = new Bet();
		initializeAction(bet);
		bet.setLocation(location);
		bet.setResource(statement);
		bet.setPoints(points);
		scoreAssignLocation();
		
		entityManager.persist(bet);
		gameRound.getActions().add(bet);
		bet.setNotEvaluated(true);
		entityManager.flush();
		mitPokerScoring.updateScoreForBets(bet.getResource());
	}
	
	public void skipCreateBet() {
		scoreAssignLocation();
	}
	
	public Map<StatementToken, Boolean> getSelectedTokens() {
		return selectedTokens;
	}
	
	public void setSelectedTokens(Map<StatementToken, Boolean> selectedTokens) {
		this.selectedTokens = selectedTokens;
	}

	public Characterization[] getCharacterizations() {
		return characterizations;
	}

	public void setCharacterizations(
			Characterization[] characterizations) {
		this.characterizations = characterizations;
	}
	
	public Integer getPoints() {
		return points;
	}

	public void setPoints(Integer points) {
		this.points = points;
	}

	public void addToScore(Integer score) {
		if (score != null) {
			currentRoundScore += score;
		}
	}

	public boolean getSkipCharacterizationResult() {
		return skipCharacterizationResult;
	}
	
	public Integer getCharacterizationScore(GameRound round) {
		int score = 0;
		for (Characterization c : getCharacterizationActions(round))
			if (c.getScore() != null)
				score += c.getScore();
		return score;
	}

	/**
	 * Retrieves all default characterizations for this game and especially 
	 * the characterizations entered by the player.
	 * 
	 * @param round
	 * @return array of all Characterizations
	 */
	public Characterization[] getCharacterizationActions(GameRound round) {
		Characterization[] chars = CharacterizationBean.createCharacterizations(round.getResources().get(0));
		for (Action a : round.getActions())
			if (a instanceof Characterization) {
				Characterization c = (Characterization)a;
				for (int i = 0; i < chars.length; i++) {
					if (chars[i].getName().equals(c.getName())) {
						chars[i] = c;
					}
				}
			}
		return chars;
	}
	
	public StatementAnnotation getAnnotationAction(GameRound round) {
		return getAction(round, StatementAnnotation.class);
	}
	
	public LocationAssignment getLocationAssignmentAction(GameRound round) {
		return getAction(round, LocationAssignment.class);
	}
	
	public Bet getBetAction(GameRound round) {
		return getAction(round, Bet.class);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getAction(GameRound round, Class<T> clazz) {
		if (round != null)
			for (Action a : round.getActions())
				if (clazz.isInstance(a))
					return (T)a;
		return null;
	}
	
	public boolean enableHighlighting(){
		List <StatementToken> statementTokens = statement.getStatementTokens();
		int aux = 0;
		for(int i= 0; i<statementTokens.size(); i++){
			if(!statementTokens.get(i).getToken().isPunktuation())
				aux++;
		}
		if(aux == 1)
			return false;
		else 
			return true;
	}

		
}
