package gwap.game;

import gwap.model.GameRound;
import gwap.model.GameSession;
import gwap.model.GameType;
import gwap.model.Person;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

@Name("gameSessionBeanNew")
@Scope(ScopeType.CONVERSATION)
public class GameSessionBeanNew implements Serializable {
	@Logger				private Log log;
	@In					private EntityManager entityManager;
		
	@Out				private GameSession gameSession;
	
	//private boolean isFirstRound=true;
	//private int round=1;
	
	
	public GameSession getGameSession() {
		return gameSession;
	}

	public void setGameSession(GameSession gameSession) {
		this.gameSession = gameSession;
	}


	public synchronized void addGameRound(GameRound gameRound, Person person) {
		log.info("Adding game round #0", gameRound.hashCode());
		log.info("GameRounds in gameSession:");
		for (GameRound g : gameSession.getGameRounds())
			log.info("#0", g.getId());
		
		//gameSession=entityManager.merge(gameSession);
		
		gameRound.setStartDate(new Date());		
		gameRound.setPerson(person);		
		gameRound.setGameSession(gameSession);
		
		entityManager.persist(gameRound);		
		gameSession.getGameRounds().add(gameRound);
		log.info("Added game round #0", gameRound.getId());
	}
	
	public synchronized void endGameRound(GameRound gameRound)
	{
		log.info("Ending game round #0", gameRound.getId());
		gameRound.setEndDate(new Date());
		//gameRound=entityManager.merge(gameRound);
	}

	/* Initiate a GameSession for the supplied gameName  
	 * gameName is used to extracted gameType from database
	 * and to acquire SharedGame after session is completed
	 * See PlayerMatcher for further reference
	 */
	public void startSession(GameType gameType)
	{
		log.info("Starting game session");
		
/*		this.gameName=gameName;
		// GameType
		Query query = entityManager.createNamedQuery("gameType.select");
		query.setParameter("name", gameName);*/
		
		//GameType gameType = (GameType) query.getSingleResult();
		gameSession=new GameSession();
		gameSession.setGameType(gameType);
		entityManager.persist(gameSession);
		log.info("Persisted gameSession id=#0, code=#1", gameSession.getId(), gameSession.hashCode());
	}
		
	public synchronized void endSession()
	{
	//	endRound();
		log.info("Ending game session");
		//gameSession=entityManager.merge(gameSession);
	}
	
	/*public GameRound startRound()
	{
		if (gameSession.getId()==null)
			entityManager.merge(gameSession);
		
		if (!isFirstRound)
			endRound();	
		isFirstRound=false;
		
		gameRound=new GameRound();
		
		log.info("Starting game round #"+round+" "+gameRound.hashCode());		
		log.info("gameSession id=#0, code=#1", gameSession.getId(), gameSession.hashCode());
		
		gameRound.setStartDate(new Date());		
		gameRound.setPerson(person);
		gameRound.setNumber(round);
		gameRound.setGameSession(gameSession);
		
		entityManager.persist(gameRound);
		gameSession.getGameRounds().add(gameRound);
		
		round++;
		return gameRound;
	}
	
	public void endRound()
	{
		SharedGame game=(SharedGame)Component.getInstance(gameName+"SharedGame");
		
		
		if (gameSession.getId()==null)
			entityManager.merge(gameSession);
				
		int score=game.getLastScore().intValue();
		gameRound.setScore(score);
		gameRound.setEndDate(new Date());
		log.info("Ending game round "+gameRound.hashCode());
				
		entityManager.merge(gameRound);
	}*/
}
