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

import gwap.action.TaggingBean;
import gwap.game.RecommendedTag;
import gwap.model.action.Tagging;
import gwap.model.resource.ArtResource;
import gwap.tools.TagSemantics;
import gwap.wrapper.TagFrequency;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

@Scope(ScopeType.CONVERSATION)
@Name("gwapGameMemoryPlayer")
public class Player extends gwap.game.Player<Player> {
	@In		private SharedGame gwapGameMemorySharedGame;
	
	@Logger	                 private Log log;
	
	@In(create=true) @Out(scope=ScopeType.CONVERSATION)	private TaggingBean taggingBean;
	@In                      private FacesMessages facesMessages;
	@In(create=true)        private RecommendedTag recommendedTag;
	
	private String enteredTag;
	
	public Player()
	{
		setAllowAi(true);
	}
	
	public boolean isGuesser()
	{
		return getId()!=gwapGameMemorySharedGame.getCurrentRound()%2;		
	}

	public void tagClicked(TagFrequency tag, int round)
	{
		if (gwapGameMemorySharedGame.checkRound(round))
			return;
		
		gwapGameMemorySharedGame.getResourceGridBean().removeGoalTag(tag);
		if (enteredTag==null || enteredTag=="")
		{	
			enteredTag=tag.getName();
		}
		else
		{
			enteredTag+=" "+tag.getName();
			sendTag(round);
		}
	}
	
	public void sendTag(int round)
	{	
		if (gwapGameMemorySharedGame.checkRound(round))
			return;
		
		if (enteredTag==null || enteredTag.equals(""))
			return;
		
		ArtResource goal=gwapGameMemorySharedGame.getResourceGridBean().getGoal();
		if (goal==null)
			return;
		
		log.info("Tagging #0 with #1", goal.getId(), enteredTag);
		
		Tagging t=null;
		
		enteredTag=TagSemantics.removePunctuation(enteredTag);
		if (TagSemantics.wordCount(enteredTag)>3) {
			facesMessages.add("#{messages['taggingBean.tooManyWordsInTag']}");			
		} else {	
			if (isInQuestionList(enteredTag, gwapGameMemorySharedGame.getAnswers())) {
				facesMessages.add("#{messages['taggingBean.tagExistsAlready']}");
			} else {
				recommendedTag.setName(enteredTag);
				t=taggingBean.recommendTag(getGameRound(), goal);
			}
		}
		
		if (t!=null)
		{
			log.info("Sending description: #0", t.getTag());
			gwapGameMemorySharedGame.sendDescription(t, round);
			signalPartner("tags");
		}
		else
			signal("edit");
		
		enteredTag="";		
	}

	public void sendQuestion(int round)
	{
		if (gwapGameMemorySharedGame.checkRound(round))
			return;		
			
		//List<String> questions=DescriptionBean.FilterTag(enteredTag);
		/*for (String question : questions)
		{
			gwapGameMemorySharedGame.addQuestion(question, round);
			
			//Only process the first tag if alternating mode is used
			if (gwapGameMemorySharedGame.getAlternatingMode())
				break;
			
		}*/
		String question=TagSemantics.removePunctuation(enteredTag);			
		question=TagSemantics.normalize(question);
		
		if (isInTagList(question, gwapGameMemorySharedGame.getDescriptions())) {
			signal("edit");
			facesMessages.add("#{messages['taggingBean.tagExistsAlready']}");
		} else {
			gwapGameMemorySharedGame.addQuestion(question, round);
			
			enteredTag="";
		
			//signal("edit");
			signalPartner("questions");
		}
	}
	
	public void answerQuestion(Question question, int answer, int round)
	{
		if (gwapGameMemorySharedGame.checkRound(round))
			return;

		question.setAnswer(answer);
		
		//Question is answered with yes => Add a tagging
		if (answer==1)
		{
			recommendedTag.setName(question.getQuestion().getName());

			Tagging t=taggingBean.recommendTag(getGameRound(), gwapGameMemorySharedGame.getResourceGridBean().getGoal());
			
			gwapGameMemorySharedGame.addDescription(t);

		}

		//Don't display answered questions to describer
		gwapGameMemorySharedGame.removeQuestion(question);

		signalPartner("questions");	
	}

	public synchronized String notified()
	{			
		if (isNotified("justMatched"))
		{
			return "memoryGame";			
		}
		
		if (isNotified("newRound"))
		{
			startRound();
			List<ArtResource> r=gwapGameMemorySharedGame.getResourceGridBean().getResources();
			log.info("Adding resources to game round:");
			for (ArtResource res : r)
			{
				log.info("#0", res.getId());			
			}
			getGameRound().getResources().addAll(r);			
		}
		
		if (isNotified("clearResource"))	
			taggingBean.cancelResource();
			//descriptionBean.cancelResource();		
		
//		if (isNotified("newResource"))		
//			gameRound.getResourcesPlayed().add(sharedGameMemory.getResourceGridBean().getGoal());						
				
    	if (isNotified("endGame"))
    	{
    		endRound();
    		//gameSessionBeanNew.endSession();
    		return "memoryScoring";
    	}

		
		return null;
	}
	
	public String getEnteredTag() {
		return enteredTag;
	}

	public void setEnteredTag(String enteredTag) {
		this.enteredTag = enteredTag;
	}
	
	private boolean isInQuestionList(String enteredTag, List<Question> answers) {
		for (Question q : answers)
			if (TagSemantics.equals(enteredTag, q.getQuestion().getName()))
				return true;
		return false;
	}

	private boolean isInTagList(String enteredTag, List<Tagging> taggings) {
		for (Tagging t : taggings)
			if (TagSemantics.equals(enteredTag, t.getTag().getName()))
				return true;
		return false;
	}

}
