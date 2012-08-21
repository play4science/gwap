/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.game;

import gwap.model.GameRound;
import gwap.model.GameSession;
import gwap.model.GameType;
import gwap.model.Person;
import gwap.model.action.Action;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

/**
 * This is the backing bean for one game session. It handles all actions that
 * can be executed during a game session. The game session itself is organized
 * in a business process.
 * 
 * @author Christoph Wieser
 */
public abstract class AbstractGameSessionBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Create                  public void init() { log.info("Creating"); }
	@Destroy                 public void destroy() { log.info("Destroying"); }
	
	@Logger                  protected Log log;
	@In                      protected FacesMessages facesMessages;
	@In                      protected EntityManager entityManager;
	@In(create=true)	     protected Person person;
	@In(create=true) @Out	 protected GameSession gameSession;
	@In(required=false) @Out protected GameRound gameRound;
	@In(required=false) @Out protected GameType gameType;
	
	protected Integer completedRoundsScore = 0;
	protected Integer currentRoundScore;
	
	protected Integer roundsLeft;
	protected Integer roundNr = 1;
	protected int maxClientDelay = 3000; // milliseconds
	protected Date startConsideringClientDelay;
	
	public void startGameSession() {	
		startGameSession("imageLabeler");
	}

	public void startGameSession(String gameName) {
		log.info("Starting game session");
		Query query = entityManager.createNamedQuery("gameType.select");
		query.setParameter("name", gameName);
		gameType = (GameType) query.getSingleResult();
		gameSession.setGameType(gameType);
		entityManager.persist(gameSession);
		roundsLeft = gameType.getRounds();
		completedRoundsScore = 0;
		startRound();
	}

	public void endGameSession() {
		log.info("Ending game session");
		
		entityManager.merge(gameSession);
	}

	public void startRound() {
		log.info("Starting game round #0 (#1 left)", roundNr, roundsLeft);
		currentRoundScore = 0;
		gameRound = new GameRound();
		gameRound.setStartDate(new Date());
		startConsideringClientDelay = null;
		gameRound.setPerson(person);
		gameRound.setNumber(roundNr);
		gameRound.setGameSession(gameSession);
		gameSession.getGameRounds().add(gameRound);
		entityManager.persist(gameRound);
		loadNewResource();
	}

	/**
	 * Needs to load a new resource before each round. It is called
	 * in the startRound() method.
	 */
	protected abstract void loadNewResource();
	
	public void endRound() {
		log.info("Ending round");
		if (roundsLeft != null)
			roundsLeft--;
		roundNr++;
		gameRound.setEndDate(new Date());
		gameRound.setScore(currentRoundScore);
		if (currentRoundScore != null)
			completedRoundsScore += currentRoundScore;
		currentRoundScore = null;
		entityManager.merge(gameRound);
		entityManager.flush();
	}
	
	public boolean roundExpired() {
		Date now = new Date();
		Date roundStart = gameRound.getStartDate();
		Integer roundDuration = gameType.getRoundDuration();
		
		// Calculate round expiration for the first call of this method by the client
		if (startConsideringClientDelay == null) {
			Date clientStartDate = new Date();
		
			// clientDelay: delay between roundStart(server) and rendering complete(client)  
			Calendar roundStartCalendar = new GregorianCalendar();
			Calendar clientStartCalendar = new GregorianCalendar();
			roundStartCalendar.setTime(roundStart);
			clientStartCalendar.setTime(clientStartDate);
			Integer clientDelay = (int) (clientStartCalendar.getTimeInMillis()-roundStartCalendar.getTimeInMillis());
			
			// latest start of round considering client delay
			Calendar maxStartConsideringClientDelayCalendar = new GregorianCalendar();
			maxStartConsideringClientDelayCalendar.setTime(roundStart);
			maxStartConsideringClientDelayCalendar.add(Calendar.MILLISECOND, maxClientDelay);
			Date maxStartConsideringClientDelay = maxStartConsideringClientDelayCalendar.getTime();
	
			// limiting client delay
			startConsideringClientDelay = (clientDelay > maxClientDelay) ? maxStartConsideringClientDelay : clientStartDate;
		}
	
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(startConsideringClientDelay);
		calendar.add(Calendar.SECOND, roundDuration);
		Date expectedExpiration = calendar.getTime();
		
		return expectedExpiration.before(now);
	}
	
	public Integer getRoundsLeft() {
		return roundsLeft;
	}
	
	@Out("gameSessionScore")
	public Integer getScore() {
		if (currentRoundScore == null)
			return completedRoundsScore;
		else if (completedRoundsScore == null)
			return currentRoundScore;
		else
			return completedRoundsScore + currentRoundScore;
	}
	
	public GameType getGameType() {
		return gameType;
	}

	/**
	 * Assigns created, person and gameround to the current values
	 */
	public void initializeAction(Action action) {
		action.setCreated(new Date());
		action.setPerson(person);
		action.setGameRound(gameRound);
	}
}
