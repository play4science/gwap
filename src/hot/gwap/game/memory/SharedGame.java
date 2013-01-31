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

package gwap.game.memory;

import gwap.action.ActionBean;
import gwap.action.TaggingBean;
import gwap.model.GameRound;
import gwap.model.Tag;
import gwap.model.action.Selection;
import gwap.model.action.Tagging;
import gwap.model.resource.ArtResource;
import gwap.tools.ArtResourceFrequency;
import gwap.tools.TagSemantics;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;


@Name("gwapGameMemorySharedGame")
@Scope(ScopeType.CONVERSATION)
public class SharedGame extends gwap.game.SharedGame<Player> {		
	@In 	private Player gwapGameMemoryPlayer;
	@Logger	                 private Log log;
	@In                      private FacesMessages facesMessages;	
	@In(create=true)	private ActionBean actionBean;
	@In(create=true) @Out(scope=ScopeType.CONVERSATION)	private TaggingBean taggingBean;
	
	//List of tags that is shown in view
	List<Tag> tags=new ArrayList<Tag>();
	
	//List of descriptions, including those generated from questions
	List<Tagging> descriptions=new ArrayList<Tagging>();

	private int wrongGuesses;
	List<ArtResourceFrequency> freq=new ArrayList<ArtResourceFrequency>();
	
	List<Question> questions=new ArrayList<Question>();
	List<Question> answers=new ArrayList<Question>();

	private boolean allowDescriptions=true;
	private boolean allowQuestions=true;
	
	private boolean clusteringMode=false; 
	
	private boolean alternatingMode=false;
	private int alternatingModeTurn=0;

	private ResourceGridBean resourceGridBean;
	
	private List<GameRound> gameRounds=new ArrayList<GameRound>();
	private GameRound gameRound;
	private ArtResource resource;
	
	public boolean isCompatible(gwap.game.SharedGame game)
	{	
		if (!game.getLanguage().equals(getLanguage()))
			return false;
			
		if (game instanceof SharedGame)
		{
			SharedGame g=(SharedGame)game;
			return	g.getAllowDescriptions() == allowDescriptions &&
					g.getAllowQuestions() == allowQuestions &&
					g.getAlternatingMode() == alternatingMode &&
					g.getRoundCount() == getRoundCount() &&
					g.getRoundLength() == getRoundLength() &&
					g.getMoveMode() == getMoveMode(); 
		}
		else
			return false;
	}

	public boolean getAllowDescriptions()
	{
		return allowDescriptions;
	}
		
	public boolean isClusteringMode() {
		return clusteringMode;
	}

	public void setClusteringMode(boolean clusteringMode) {
		this.clusteringMode = clusteringMode;
	}

	public void setAllowDescriptions(boolean allowDescriptions) {
		this.allowDescriptions = allowDescriptions;
	}

	public void setAllowQuestions(boolean allowQuestions) {
		this.allowQuestions = allowQuestions;
	}

	public boolean getAllowQuestions()
	{
		return allowQuestions;
	}
	
	public boolean getAlternatingMode()
	{
		return alternatingMode; 
	}
	
	public void setAlternatingMode(boolean alternatingMode)
	{
		this.alternatingMode=alternatingMode; 
	}

	public boolean getDisableEditQuestion()
	{		
		return resourceGridBean.getGoal()==null || (alternatingMode && alternatingModeTurn!=1);	
	}
	
	public boolean getDisableAnswerQuestion()
	{		
		return resourceGridBean.getGoal()==null || (alternatingMode && alternatingModeTurn!=2);	
	}
	
	public boolean getDisableEditDescription()
	{
		return resourceGridBean.getGoal()==null || (alternatingMode && alternatingModeTurn!=0);
	}

	public int getAlternatingModeTurn()
	{
		return alternatingModeTurn;	
	}
	
	public void doMove()
	{
		move();
		gwapGameMemoryPlayer.signalAll("moves");
	}
	
	public synchronized String getHeading(boolean isGuesser)
	{
		if (isGuesser)
		{			
			if (resourceGridBean.isNoGoal())
				return "game.memory.guesser_wait";
			else
			{
				if (alternatingMode)
				{
					if (alternatingModeTurn==0)
						return "game.memory.guesser_description_wait";
					else if (alternatingModeTurn==1)
						return "game.memory.guesser_description_ask";
					else
						return "game.memory.guesser_description_answer";
				}
				else
					return "game.memory.guesser_description";
			}
		}
		else
		{			
			if (resourceGridBean.isNoGoal())
				return "game.memory.describer_select";
			else
			{
				if (alternatingMode)
				{
					if (alternatingModeTurn==0)
						return "game.memory.describer_description";
					else if (alternatingModeTurn==1)
						return "game.memory.describer_wait";
					else
						return "game.memory.describer_answer";
				}
				else
					return "game.memory.describer_description";
			}
		}	
	}
	
	public void setAlternatingModeTurn(int turn)
	{
		alternatingModeTurn=turn;
		gwapGameMemoryPlayer.signalAll("edit");		
	}
	
	public void addQuestion(String question, int round)	
	{
		if (alternatingMode)
		{
			if (alternatingModeTurn!=1)
				return;
		}
			

		if (checkRound(round))
			return;
		
		question=question.toLowerCase();
		
		if (TagSemantics.wordCount(question)>3)
		{
			facesMessages.add("#{messages['taggingBean.tooManyWordsInTag']}");
			return;
		}
		
		for (Question q : answers)
		{
			if (q.getQuestion().getName().toLowerCase().equals(question))
			{
				facesMessages.add("#{messages['taggingBean.tagExistsAlready']}");
				return;
			}
		}
		
		if (alternatingMode)
		{
			setAlternatingModeTurn(2);
			gwapGameMemoryPlayer.signalAll("shakeheading");
		}


		Tag tag=taggingBean.findOrCreateTag(question);
		if (tag!=null)
		{
			Question q=new Question(tag);
			questions.add(q);
			answers.add(q);
		}
	}
	
	public void removeQuestion(Question question)
	{
		if (alternatingMode)
		{
			if (alternatingModeTurn!=2)
				return;
			
			setAlternatingModeTurn(0);
			gwapGameMemoryPlayer.signalAll("shakeheading");
		}
		
		doMove();
		questions.remove(question);
		
	}

	
	public List<Question> getQuestions()
	{
		return questions;
	}
	
	public List<Question> getAnswers()
	{
		return answers;
	}

	public void TESTsetFreq(List<ArtResourceFrequency> f)
	{
		freq=f;
	}
	
	public double TESTgetFreq(ArtResource res)
	{
		for (ArtResourceFrequency r : freq)
		{
			if (r.getResource()==res)
				return r.getCount();
		}
		return 0;
	}
	
	public ResourceGridBean getResourceGridBean() {
		return resourceGridBean;
	}

	public SharedGame()
	{
		resourceGridBean=(ResourceGridBean)Component.getInstance("resourceGridBean");
		
		setScore(0);
		
//		gameSessionBean=(GameSessionBean)Component.getInstance("gameSessionBean");
//		gameSessionBean.startGameSession("memory");
	}


	public List<Tag> getDescription() {
		return tags;
	}
	
	public List<Tagging> getDescriptions() {
		return descriptions;
	}


	public void sendTag(Tag tag, int round) {		
		if (checkRound(round))
			return;
		
		tags.add(tag);
	}

	public void sendDescription(Tagging description, int round) {		
		if (checkRound(round))
			return;

		if (alternatingMode)
		{
			if (alternatingModeTurn!=0)
				return;
			
			setAlternatingModeTurn(1);
			gwapGameMemoryPlayer.signalAll("shakeheading");
		}

		doMove();
		
		
		descriptions.add(description);		
		resource.getTaggings().add(description);
		tags.add(description.getTag());
	}

	public void addDescription(Tagging t)
	{		
		resource.getTaggings().add(t);
		descriptions.add(t);
	}

	private ArtResource cloneResource(ArtResource resource)
	{
		ArtResource result=new ArtResource();		
		result.setArtist(resource.getArtist());	
		result.setTitles(resource.getTitles());
		result.setDateCreated(resource.getDateCreated());
		result.setUrl(resource.getUrl());
		return result;
	}
	
	public synchronized void startNewRound()
	{
		try
		{
			//Start new round (replay previous round if partner is Ai and Player is guesser)
			resourceGridBean.updateResourceGridBean(gwapGameMemoryPlayer.getPartner().isAi() && gwapGameMemoryPlayer.isGuesser(),
					isClusteringMode());
		}
		catch (Exception e)
		{
			log.info("Error starting new round: #0", e.getMessage());
			return;
		}
				
		int number=1;
		if (gameRound!=null)
		{			
			number=gameRound.getNumber()+1;
		}
			
		
		gameRound=new GameRound();		
		gameRounds.add(gameRound);		
		
		resource=cloneResource(resourceGridBean.getGoal());
		gameRound.getResources().add(resource);
		gameRound.setNumber(number);
		
		newGoal();
				
		gwapGameMemoryPlayer.signalAll("images");
		gwapGameMemoryPlayer.addNotifierAll("tags");
		
		gwapGameMemoryPlayer.addNotifierAll("edit");
		
		if (allowQuestions)
			gwapGameMemoryPlayer.addNotifierAll("questions");
		wrongGuesses=0;
		
		log.info("Players signalled");
	}	
	
	private void newGoal()
	{	
		descriptions.clear();
		tags.clear();
		questions.clear();
		answers.clear();
		
		alternatingModeTurn=0;
	}
	
	public List<GameRound> getGameRounds() {
		return gameRounds;
	}

	public void resourceClicked(ArtResource res, int round)
	{
		resourceClicked(res, round, gwapGameMemoryPlayer);			
	}
		
	public synchronized void resourceClicked(ArtResource res, int round, Player player)
	{	
		if (checkRound(round))
			return;
		
		/*if (isRoundCompleted())	
		{
			newRound();
			return;
		}*/
		
		if (res==null)
			return;		
		
		//Guesser
		if (player.isGuesser())
		{		
			if (resourceGridBean.getGoal()!=null)
			{
				int scoreDelta=0;
				if (resourceGridBean.isSelected(res))
				//Player has ventured a guess
				{
					Selection selection=(Selection)Component.getInstance("selection");
					selection.setResource(res);					
									
					if (alternatingMode)
					{
						if (alternatingModeTurn==1)						
							setAlternatingModeTurn(0);
						
						gwapGameMemoryPlayer.signalAll("shakeheading");
						doMove();
					}

					if (resourceGridBean.isGoal(res))
					{	
						selection.setCorrect(true);
												
						resourceGridBean.removeGoalResource();
						int rCnt=resourceGridBean.getResourceCount();
						scoreDelta=(9-rCnt)*10;
						
						//Factor between 1.0 and 0.0
						// 1.0 = No wrong guesses
						// 0.0 = 1/3 rCnt wrong guesses
						
						if (rCnt>0)	//Should always be the case
						{
							double weight=Math.max(0.3-(double)wrongGuesses/rCnt, 0.0)/0.3;
	
							if (!player.isAi())
								actionBean.updateRatings(descriptions, (0.5+0.05*rCnt)*weight);
						}

						newGoal();
						
						player.addNotifierAll("clearResource");
						player.addNotifierAll("edit");
						player.addNotifierAll("shakeheading");
						
						wrongGuesses=0;
						
						if (resourceGridBean.getResourceCount()==1)
							newRound();	
						
					}
					else
					{							
						int rCnt=resourceGridBean.getResourceCount();
						selection.setCorrect(false);
						//actionBean.updateDescriptions(descriptions, -1.0/rCnt);
						
						wrongGuesses++;
						
						scoreDelta=-(10-rCnt)*11;
						player.addNotifier("shakeheading");
					}
					
					actionBean.performAction(selection, player.getGameRound());
					
					setScore(getScore()+scoreDelta);
					
					gameRound.setScore((int)getRoundScore());
										
					resourceGridBean.setSelected(null);				
					
					player.signalPartner("images");
					player.signalAll("tags");
					player.signalAll("score");
				}
				else
					resourceGridBean.setSelected(res);
			}
		}
		
		//Describer
		else
		{
			if (resourceGridBean.getGoal()==null)
			{
				resourceGridBean.setGoal(res);				
				
				resource=cloneResource(resourceGridBean.getGoal());
				gameRound.getResources().add(resource);
				
				player.addNotifierAll("newResource");
				player.signalPartner("edit");
				player.signalAll("shakeheading");				
			}
			else
				player.signal("shakeheading");
		
		}
	}
	
	
	
}
