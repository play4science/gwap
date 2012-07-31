/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.mit;

import gwap.NotEnoughDataException;
import gwap.game.AbstractGameSessionBean;
import gwap.model.GameRound;
import gwap.model.action.Action;
import gwap.model.action.Bet;
import gwap.model.action.Familiarity;
import gwap.model.action.LocationAssignment;
import gwap.model.action.StatementAnnotation;
import gwap.model.action.StatementCharacterization;
import gwap.model.resource.Location;
import gwap.model.resource.Statement;
import gwap.model.resource.StatementToken;
import gwap.wrapper.LocationPercentage;
import gwap.wrapper.Percentage;
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
	@Out					private Statement statement;
	@In						private StatementBean mitStatementBean;
	@In(required=false)@Out(required=false) private Long locationId;
	@In						private PokerScoring mitPokerScoring;
	
	@Out
	private List<Location> breadcrumbLocations = new ArrayList<Location>();
	private Map<StatementToken, Boolean> selectedTokens = new HashMap<StatementToken, Boolean>();

	private boolean skipCharacterizationResult;
	private int roundNrUsingAtLeastAssignedStatement = 0;

	private StatementCharacterization statementCharacterization;
	private Familiarity familiarity;
	private StatementAnnotation statementAnnotation;
	private LocationAssignment locationAssignment;
	private Integer points;
	
	@Override
	public void startGameSession() {
		startGameSession("mitRecognize");
		if (gameType != null && gameType.getRounds() != null && gameType.getRounds() > 1)
			roundNrUsingAtLeastAssignedStatement = new Random().nextInt(gameType.getRounds()-1)+2; // not in first round
	}
	
	@Override
	public void startRound() {
		super.startRound();
		if (roundNr > 1) {
			if (roundNr == roundNrUsingAtLeastAssignedStatement)
				statement = mitStatementBean.updateAtLeastAssignedStatement();
			else
				statement = mitStatementBean.updateStatement();
		}
		statement.getStatementStandardTokens().size();
		gameRound.getResources().add(statement);
		locationId = null;
		breadcrumbLocations.clear();
		statementCharacterization = new StatementCharacterization();
		statementCharacterization.setGender(0);
		statementCharacterization.setMaturity(0);
		statementCharacterization.setCultivation(0);
		statementAnnotation = null;
		locationAssignment = null;
		familiarity = null;
		points = null;
		selectedTokens.clear();
		skipCharacterizationResult = false;
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
	
	public void familiarAndAssignLocation(Long locationId) {
		familiarity(true);
		assignLocation(locationId);
	}
	
	public void familiarity(boolean familiar) {
		if (familiarity != null)
			return;
		log.info("#0 rated as familiar=#1 by #2", statement, familiar, person);
		familiarity = new Familiarity();
		initializeAction(familiarity);
		familiarity.setFamiliar(familiar);
		entityManager.persist(familiarity);
		gameRound.getActions().add(familiarity);
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
			addToScore(mitPokerScoring.highlighting(statementAnnotation, facesMessages));
		}
	}
	
	public void characterize() {
		if(!statementCharacterization.isEmpty()){
			log.info("Saving characterization");
			if (statementCharacterization.getId() != null)
				return;
			initializeAction(statementCharacterization);
			statementCharacterization.setStatement(statement);
			entityManager.persist(statementCharacterization);
			gameRound.getActions().add(statementCharacterization);
			entityManager.flush();
			try {
				addToScore(mitPokerScoring.characterization(statementCharacterization));
				if (statementCharacterization.getScore() > 0)
					facesMessages.addFromResourceBundle(Severity.INFO, "game.recognize.gainedPoints", statementCharacterization.getScore());
				facesMessages.addFromResourceBundle(Severity.INFO, "game.recognize.characterizationResult.seeAbove");
			} catch (NotEnoughDataException e) {
				facesMessages.addFromResourceBundle(Severity.INFO, "game.recognize.characterizationResult.notEnoughData");
			}
		} else {
			log.info("Characterization NOT saved (empty)");
			skipCharacterizationResult = true;
		}
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

	public StatementCharacterization getStatementCharacterization() {
		return statementCharacterization;
	}

	public void setStatementCharacterization(
			StatementCharacterization statementCharacterization) {
		this.statementCharacterization = statementCharacterization;
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

	/**
	 * Calculates the average value of the defined characterization type
	 * 
	 * @param type one of 'gender', 'maturity' or 'cultivation'
	 * @return a value normalized to the range [0,100] instead of [-100,100]
	 */
	public int getCharacterizationAsPercentage(String type) {
		if (statementCharacterization.getId() != null) {
			Percentage result = mitPokerScoring.getCharacterizationResult(statement, type);
			if (result != null && result.getTotal() >= PokerScoring.MIN_NR_FOR_STATISTICS) {
				return (result.getFraction().intValue()+100)/2;
			}
		}
		return 0;
	}

	public StatementCharacterization getCharacterizationAction(GameRound round) {
		return getAction(round, StatementCharacterization.class);
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
