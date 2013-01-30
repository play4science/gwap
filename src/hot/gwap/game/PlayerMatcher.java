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

package gwap.game;

import gwap.game.memory.ForceBean;
import gwap.model.GameType;
import gwap.model.Person;
import gwap.tools.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.faces.Switcher;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.log.Log;


/*
 * Generic PlayerMatcher
 *
 * This class is used to pair two players (or a player and an Ai)
 * If no two human players are found after Player.WAIT_TIMEOUT and if Player.allowAi is true,
 * the player is paired with an Ai 
 * 
 * To create a custom game, subclasses of PlayerMatcher, SharedGame and Player need to be created
 * (See gwap.game.test for a minimalistic example)
 * 
 * Furthermore, some actions have to be added to pages.xml
 * 
 *  An action for the lobby page to ensure players are registered with the playerMatcher
 *  <page view-id="/memoryLobby.xhtml">
 *	<action execute="#{gwapGameMemoryPlayerMatcher.enqueue}" on-postback="false" />
 *	</page>
 * 	
 * A redirection rule for the game page and if applicable for the scoring page:
 * <rule if-outcome="memoryGame">
 * <redirect view-id="/memoryGame.xhtml"/>
 * </rule>
 * 
 */

public abstract class PlayerMatcher<G extends SharedGame<P>, P extends Player<P>> implements Serializable{	
	
	private static final long serialVersionUID = 1L;
		
	
	@Destroy public void destroy() { log.info("Destroying"); }
	
	@Logger	private Log log;
	
	@In		private EntityManager entityManager;
	@In		private Switcher switcher;
	@In		private LocaleSelector localeSelector;
	
	//private GameType gameType;
	@In(create=true)		 	private Person person;
//	@In(create=true)			private GameSessionBeanNew gameSessionBeanNew;

	//List of games that are currently queued (ie have only one player)
	private List<G> queue=new ArrayList<G>();
	
	//List of games that are currently in progress (ie have two players, at least one of which is human)
	private List<G> activeGames=new ArrayList<G>();

	/* Prefix for instantiation of classes used in a specific gametype:
	 * gameName + "Player"
	 * gameName + "SharedGame"
	 * gameName + "Ai"
	 * 
	 * Furthermore, GameType (roundDuration, etc) is read from database by using
	 * gameType.name == gameName  
	 * 
	 * Finally, gamename is used by Highscore.xhtml to extract the 
	 * full name of the game from messages.xml
	 * 
	 *  
	 * Example: gameName = "gwapGameMemory"
	 * => Player subclass MUST have @name "gwapGameMemoryPlayer"
	 * => SharedGame subclass MUST have @name "gwapGameMemorySharedGame"
	 * => GameType in database MUST have name "gwapGameMemory" 
	 */
	private String gameName;
	
	
	@Create public void init() {
		log.info("Creating");
	}
	
	/*public GameType getGameType()
	{
		return gameType;
	}*/
	
	public PlayerMatcher(String gameName)	
	{
		this.gameName=gameName;		
	}	

	public Pair<G, P> match()
	{
		return match((G)Component.forName(gameName+"SharedGame").newInstance(),
					(P)Component.forName(gameName+"Player").newInstance());		
	}
	
	
	public Pair<G, P> match(G game, P player)
	{
		return match(game, player, gameName);	
	}
	
	public Pair<G, P> match(G game, P player, String gameTypeName)
	{	
		log.info("Enqueueing");
		
		// get GameType
		Query query = entityManager.createNamedQuery("gameType.select");
		query.setParameter("name", gameTypeName);
		GameType gameType = (GameType) query.getSingleResult();
					
		game.setGameType(gameType);
		
		game.setLanguage(localeSelector.getLanguage());
		
		game.setRoundCount(gameType.getRounds());
		
		if (game.getMoveMode())
		{
			if (gameType.getRoundDuration()!=0)
				game.setMoves(gameType.getRoundDuration());
		}
		else			
			game.setRoundLength(gameType.getRoundDuration());									

		Pair<G, P> pair=null;
				
		synchronized(queue)
		{
		synchronized(activeGames)
		{			
			//Remove timed-out players and empty games
			cleanList(queue);
			cleanList(activeGames);
			
			Pair<G, P> queuedGame=isQueued(game);
			if (queuedGame==null)
			{				
				log.info("Creating player for person "+person.hashCode());
				
		       Conversation.instance().begin();

				player.setPerson(person);
				
				player.setSharedGame(game);
				player.setPlayerMatcher(this);
//				gameSessionBeanNew.startSession(gameName);
				
				player.setConversation(switcher.getConversationIdOrOutcome());
				
				game.addPlayer(player);
				
				pair=pair(game);
				//return pair;
			}			
			else
			{
				log.info("Person "+person.hashCode()+" is already playing");
								
				//Restore the previous conversation id
				String conv=switcher.getConversationIdOrOutcome();					
				if (conv==null || !conv.equals(queuedGame.b.getConversation()))
				{
					switcher.setConversationIdOrOutcome(queuedGame.b.getConversation());
					switcher.select();
				}
				
				if (queuedGame.b.isMatched())
					queuedGame.b.signal("justMatched");
				return queuedGame;
			}		
		}
		}
		//Players have been matched
		if (pair.b.isMatched())
		{
			//Start potentially time consuming process (newRound) outside locked region		
			pair.a.newRound();
			
			//Outject new components to avoid race conditions in view
			Contexts.getConversationContext().set(gameName+"SharedGame", pair.a);
			Contexts.getConversationContext().set(gameName+"Player", pair.b);						
			
			signal(pair.b);
		}
		
		return pair;		
	}

	private G findPlayer(Person person)
	{
		ListIterator<G> g=queue.listIterator();
		
		while (g.hasNext())		
		{
			G game=g.next();			
			P lplayer=game.getPlayerByIndex(0);
			if (lplayer.getPerson()==person)
			{				
				return game;
			}
		}	
		return null;
	}
	
	private Pair<G,P> updatePlayer(List<G> list, G cGame)
	{
		ListIterator<G> g=list.listIterator();
		
		while (g.hasNext())		
		{
			G game=g.next();
			P player=game.updatePlayer(person);
			if (player!=null)
			{				
				if (game.isCompatible(cGame))
					return new Pair<G, P>(game, player);
				else	//Player is already playing or enqueued, but has request a new game 
				{
					
					//Remove player
					player.forceTimeout();
					
					//Remove the old if it is not currently matched
					if (list==queue)					
						g.remove();
				}
			}
		}
		return null;		
	}

	//Finds out whether the current Person is already playing a game or is queued.
	private Pair<G, P> isQueued(G game)
	{
		Pair<G,P> queued=updatePlayer(queue, game);
		if (queued!=null)
			return queued;
		else
			return updatePlayer(activeGames, game);
	}
	
	private void cleanList(List<G> list)
	{
		Iterator<G> g=list.iterator();
		
		while (g.hasNext())
		{
			G game=g.next();
			
			if (game.cleanPlayers()
				|| game.isTerminated())			
				g.remove();
		}
	}
	
	public void link(P match1, P match2, G game)
	{		
		Random r=new Random();
		
		boolean ids=false;
		if (match2.isAi())
			ids=r.nextDouble()>0.8;	//Make human player the describer with high probability
		else	//Randomly assign human players
			ids=r.nextBoolean();
		
		ForceBean forceBean=(ForceBean)Component.getInstance("gwapGameMemoryForceBean");

		//Always start as Describer in forced Id matches
		if (forceBean!=null && forceBean.getForcedId()!=0)
			ids=false;
		
		//ids=true;
		
		if (ids)	//Player 1 is Guesser
		{
			match1.setId(0);
			match2.setId(1);
		}
		else	//Player 1 is Describer
		{
			match1.setId(1);
			match2.setId(0);
		}
		
		match1.setPartner(match2);
		match2.setPartner(match1);

		match1.setSharedGame(game);
		match2.setSharedGame(game);
		
		//match1.signal("justMatched");
		//match2.signal("justMatched");
	}
	
	public void signal(P player)
	{
		player.signalAll("justMatched");
	}

	public Pair<G, P> pair(G game)
	{
		G match=null;
		
		Iterator<G> g=queue.iterator();
		while (g.hasNext() && match==null)
		{
			G cgame=g.next(); 
			if (cgame.isCompatible(game))
			{
				match=cgame;
				g.remove();				
			}			
		}		
		
		if (match!=null)
		{	
			P match1=match.getPlayerByIndex(0);
			P match2=game.getPlayerByIndex(0);		
			
			match.addPlayer(match2);
					
			activeGames.add(match);
											
			link(match1, match2, match);
			//match.newRound();
			
							
			return new Pair<G, P>(match, match2);
		}
		else
		{
			queue.add(game);
						
			return new Pair<G,P>(game, game.getPlayerByIndex(0));
		}
	}
	
	public void MatchTimeOut(Person p)
	{
		log.info("Matching person #0 with AI", p);
		G game;
		P player;
		P ai;
		synchronized(queue)
		{
		synchronized(activeGames)
		{
		game=findPlayer(p);
		if (game==null)
			return;

		queue.remove(game);
		
		player=game.getPlayerByIndex(0);
		ai=(P)Component.forName(gameName+"Ai").newInstance();
		
		game.addPlayer(ai);
		activeGames.add(game);		
		link(player, ai, game);
		}
		}
		
		game.newRound();
		player.signalAll("justMatched");
		ai.poll(0);
	}
}
