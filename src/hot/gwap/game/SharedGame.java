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

import gwap.model.GameType;
import gwap.model.Person;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.log.Log;


/* Class for all shared contents in a game
 * PlayerMatcher ensures that all parties in a game
 * share the same SharedGame object
 * 
 * Warning: Any properties that are injected into the
 * SharedGame are NOT shared 
 */
public abstract class SharedGame<P extends Player<P>> implements Serializable {
	
	@Logger	                 private Log log;
	
	private int currentRound=0;
	
	protected Date roundStarted;

	private long score=0;
	private long lastRoundStartScore=0;
	private long lastRoundEndScore;
	
	private boolean moveMode=false;
	
	private int movesLeft;
	private int moves=15;
	
	private String language;
	
	//Overall number of rounds
	private int roundCount=0;
	
	private int roundLength=0;
		
	private GameSessionBeanNew gameSessionBeanNew;
	
	private List<P> players=new ArrayList<P>();
	
	private GameType gameType;

	/*This method is used by the PlayerMatcher to decide, whether:
	 * Two players can be matched
	 * A player is returned to their queue or created anew
	 * 
	 * This is mostly important for games that have customizable parameters
	 */
	public abstract boolean isCompatible(SharedGame game);
	
	public abstract void startNewRound();
	
	
	public SharedGame()
	{
		gameSessionBeanNew=(GameSessionBeanNew)Component.getInstance("gameSessionBeanNew");
	
	}
	
	
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public GameSessionBeanNew getGameSessionBeanNew() {
		return gameSessionBeanNew;
	}
	
	public GameType getGameType() {
		return gameType;
	}

	public void setGameType(GameType gameType) {
		this.gameType = gameType;
	}

	public long getScore() {
		return score;
	}
	
	public long getRoundScore() {
		return score-lastRoundEndScore;	
	}

	public void setScore(long score) {
		this.score = score;
	}

	public void addPlayer(P player) {
		players.add(player);
	}

	public P getPlayerByIndex(int index) {
		return players.get(index);
	}
	
	public P updatePlayer(Person person)
	{	
		ListIterator<P> lp=players.listIterator();
		
		while (lp.hasNext())		
		{
			P lplayer=lp.next();
			
			if (lplayer.getPerson()==person)			
				return lplayer;
		}
		
		return null;	
	}
	
	public boolean cleanPlayers()
	{
		ListIterator<P> lp=players.listIterator();
		
		while (lp.hasNext())		
		{
			P lplayer=lp.next();
			
			try
			{
				if (lplayer.isTimedOut())
				{				
					return true;
				}
			}
			catch (Exception e)
			{
				String m=e.getMessage();
			}
		}
		
		return false;
	}
	
	public void move()
	{
		if (moveMode)
		{
			if (movesLeft>0)
				movesLeft--;
		}
				
	}
	
	public boolean isRunning()
	{
		return players.size()==2;		
	}
	
	public int getCurrentRound() {
		return currentRound;
	}

	public void setCurrentRound(int currentRound) {
		this.currentRound = currentRound;
	}

	public int getRoundCount() {
		return roundCount;
	}	
	
	public int getRoundLength() {
		return roundLength;
	}

	public void setRoundLength(int roundLength) {
		this.roundLength = roundLength;
	}

	public void setRoundCount(int roundCount) {
		this.roundCount = roundCount;
	}

	public boolean isTerminated()
	{
		return currentRound>roundCount;	
	}
	
	public int getRoundRemaining() {
		return (int)(roundStarted.getTime()-new Date().getTime())/1000+roundLength;
	}
	
	public boolean getMoveMode()
	{
		return moveMode;
	}
	
	public void setMoveMode(boolean moveMode)
	{
		this.moveMode=moveMode;		
	}
	
	public void setMoves(int moves)
	{
		this.moves=moves;	
	}
	
	public int getMoves()
	{
		return moves;
	}
	
	public int getMovesLeft()
	{
		return movesLeft;	
	}
	
	public boolean isRoundCompleted() {
		if (moveMode)
			return movesLeft<=0;
		else
			return getRoundRemaining()<=0;
	}
	
	public synchronized boolean checkRound(int round)
	{
		if (isRoundCompleted())
			newRound();
		
		if (isTerminated())
		{
			for (Player p : players) {
				p.signal("endGame");		
			}
		}

			
		return (round!=0 && round!=currentRound) || isTerminated();
	}
	
	public Long getLastScore()
	{
		return lastRoundEndScore-lastRoundStartScore;
	}
	
	public synchronized void newRound()
	{				
		if (isTerminated())
		{
			for (Player p : players) {
				p.signal("endGame");		
			}
			return;
		}
			
		log.info("newRound()");
		
		roundStarted=new Date();
		currentRound++;
		
		if (isFirstRound())
			gameSessionBeanNew.startSession(gameType);
		
		movesLeft=moves;
		lastRoundStartScore=lastRoundEndScore;
		lastRoundEndScore=score;

		

		if (!isTerminated())
		{
			startNewRound();
			
			for (Player p : players) {
				p.signal("newRound");		
			}
		}	
		else
		{
			for (Player p : players) {
				p.signal("endGame");		
			}			
		}
	}
	
	public boolean isFirstRound()
	{
		return currentRound<=1;	
	}
}
