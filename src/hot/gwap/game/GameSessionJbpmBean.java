/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

//package gwap.game;
//
//import gwap.PersonBean;
//import gwap.model.GamePlayer;
//import gwap.model.GameRound;
//import gwap.model.GameSession;
//import gwap.model.GameType;
//import gwap.model.Person;
//import gwap.model.Resource;
//
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import javax.persistence.EntityManager;
//import javax.persistence.Query;
//
//import org.jboss.seam.Component;
//import org.jboss.seam.ScopeType;
//import org.jboss.seam.annotations.Create;
//import org.jboss.seam.annotations.Destroy;
//import org.jboss.seam.annotations.In;
//import org.jboss.seam.annotations.Logger;
//import org.jboss.seam.annotations.Name;
//import org.jboss.seam.annotations.Out;
//import org.jboss.seam.annotations.Scope;
//import org.jboss.seam.annotations.bpm.EndTask;
//import org.jboss.seam.annotations.bpm.StartTask;
//import org.jboss.seam.bpm.BusinessProcess;
//import org.jboss.seam.log.Log;
//import org.jbpm.taskmgmt.exe.TaskInstance;
//
///**
// * This is the backing bean for one game session. It handles all actions that
// * can be executed during a game session. The game session itself is organized
// * in a business process.
// * 
// * @author Christoph Wieser
// */
//
//@Name("gameSessionJbpmBean")
//@Scope(ScopeType.BUSINESS_PROCESS)
//public class GameSessionJbpmBean implements Serializable {
//
//	private static final long serialVersionUID = 1L;
//	
//	@Create  public void init()    { log.info("Creating"); if (person == null) { personBean.createAnonymousPerson(); } }
//	@Destroy public void destroy() { log.info("Destroying"); }
//	@Logger                  private Log log;
//	@In                      private EntityManager entityManager;
//	@In                      private BusinessProcess businessProcess;
//	@In(required=false)      private Person person;
//	@In(create=true)         private PersonBean personBean;
//	@In(create = true) @Out  private GameSession gameSession;
//	@In(required = false)    private GameType gameType;
//	@In(required=false)      private TaskInstance taskInstance;
//
//
//	// ////////////////////////////////////////////////////////////////////////////////
//	// game management
//
//	/**
//	 * Start a game session
//	 */
//	public String startGameSession() {
//		// GameType
//		Query query = entityManager.createNamedQuery("gameType.select");
//		query.setParameter("name", "imageLabeler");
//		gameType = (GameType) query.getSingleResult();
//		gameSession.setGameType(gameType);
//
//		initGameSession();
//		return startBusinessProcess("gameImageLabeler");
//	}
//
//	public void initGameSession() {
//		log.info("Initializing game session");
////		gameSession.setGamePlayers(gamePlayers);
//		initRounds();
//	}
//
//	/**
//	 * Init Rounds
//	 */
//	public void initRounds() {
//		List<GameRound> gameRounds = new ArrayList<GameRound>();
//		GameRound previousGameRound = null;
//
//		List<Resource> gameObjects = selectGameObjects();
//
//		for (int i = 0; i < gameSession.getGameType().getRounds(); i++) {
//			GameRound gameRound = new GameRound();
//			gameRound.setNumber(i + 1);
//			gameRound.setGameSession(gameSession);
//			gameRound.setGameObject(gameObjects.get(i));
//			gameRounds.add(gameRound);
//
//			// connect the gameRounds with the following Round
//			if (previousGameRound != null) {
//				previousGameRound.setNextGameRound(gameRound);
//			}
//			previousGameRound = gameRound;
//		}
//		gameSession.setGameRounds(gameRounds);
//	}
//
//	@SuppressWarnings("unchecked")
//	public List<Resource> selectGameObjects() {
//		Query query = entityManager.createNamedQuery("resource.all");
//		query.setMaxResults(gameSession.getGameType().getRounds());
//		return query.getResultList();
//	}
//
//	/**
//	 * Init a round
//	 */
//	public void startRound() {
//		// Set current gameRound
//		GameRound gameRound = gameSession.getCurrentGameRound();
//		if (gameRound == null) {
//			gameRound = gameSession.getGameRounds().get(0);
//		} else {
//			gameRound = gameSession.getCurrentGameRound().getNextGameRound();
//		}
//		gameSession.setCurrentGameRound(gameRound);
//
//		// Populate gameRound
//		gameRound.setGameSession(gameSession);
//		gameRound.setStartDate(new Date());
//
//		// Write log message
//		List<GamePlayer> gamePlayers = gameSession.getGamePlayers();
//		log.info("Starting round #0 for #1 and #2", gameRound.getNumber(),
//				gamePlayers.get(0).getPerson().getUsername(), gamePlayers
//						.get(1).getPerson().getUsername());
//
//	}
//
//	/**
//	 * End current round
//	 */
//	public void endRound() {
//		getGameContext();
//		log.info("ending round #0", gameSession.getCurrentGameRound()
//				.getNumber());
//		GameRound gameRound = gameSession.getCurrentGameRound();
//		gameRound.setEndDate(new Date());
//		// TODO: Calcualte Score
//	}
//
//	// ////////////////////////////////////////////////////////////////////////////////
//	// jBPM management
//
//	/**
//	 * Starting the business process, that manages a game session. 
//	 * In other words: start the session.
//	 * 
//	 * Usually @CreateProcess would be used here to start a business process.
//	 * Instead we used businessProcess.createProcess() because the
//	 * startImageLabelerProcess() is invoked from the same class.
//	 * Hence, the @CreateProcess annotation would not be evaluated.
//	 */
//	public String startBusinessProcess(String string) {
//		businessProcess.createProcess(string);
//		log.info("Business process #0 started!", string);
//		return "gameSession";
//	}
//
//	/**
//	 * General task starter with redirection to a task-specific page configured
//	 * in faces-config.xml
//	 */
//	@StartTask
//	public String startTask() {
//		log.info("Task #0 started for #1.", getTaskInstance().getName(), person
//				.getUsername());
//		return getTaskInstance().getName();
//	}
//
//	@EndTask
//	public String endTask() {
//		log.info("Task #0 ended for #1.", getTaskInstance().getName(), person
//				.getUsername());
//		return "gameSession";
//
//	}
//
//	public void taskTimeout() {
//		log.info("Task timeout!");
//	}
//
//	public void logTransition(String string) {
//		log.info("---> #0", string);
//	}
//
//	// ////////////////////////////////////////////////////////////////////////////////
//	// Getter / Setter
//
//	/**
//	 * Get the player id for the jBPM process definition as String
//	 * 
//	 * @param index
//	 *            of the player starting with 0
//	 * @return String id of the player
//	 */
//	public String getPlayerId(int index) {
//		return String.valueOf(gameSession.getGamePlayers().get(index)
//				.getPerson().getId());
//	}
//
//	public TaskInstance getTaskInstance() {
//		return taskInstance;
//	}
//
//	public boolean hasNextRound() {
//		return gameSession.getCurrentGameRound().getNextGameRound() != null;
//	}
//
//	public String getGameObjectURL() {
//		return gameSession.getCurrentGameRound().getGameObject().getUrl();
//	}
//	
//	public Resource getGameObject() {
//		return gameSession.getCurrentGameRound().getGameObject();
//	}
//
//	/**
//	 * Workaround to load all injected variables, if @In does not work. This
//	 * happens, if the Seam life-cycle is not involved.
//	 */
//	private void getGameContext() {
//		gameSession = (GameSession) Component.getInstance("gameSession");
//		entityManager = (EntityManager) Component.getInstance("entityManager");
//	}
//}
