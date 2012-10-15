/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.elearn;

import gwap.action.TaggingBean;
import gwap.game.AbstractGameSessionBean;
import gwap.model.GameRound;
import gwap.model.action.Tagging;
import gwap.model.resource.Term;
import gwap.wrapper.MatchingTag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;

/**
 * This is the backing bean for one game session. It handles all actions that
 * can be executed during a game session. The game session itself is organized
 * in a business process.
 * 
 * @author Christoph Wieser
 */

@Name("elearnFreeTaggingGame")
@Scope(ScopeType.CONVERSATION)
public class FreeTaggingGame extends AbstractGameSessionBean {

	private static final long serialVersionUID = 1L;

	@In(create=true)        				private TaggingBean taggingBean;
	@In(create=true) @Out(required=false)	private Term term;
	@In(create=true)						private TermBean elearnTermBean;
	
	private Map<Integer, List<MatchingTag>> recommendedTags = new HashMap<Integer, List<MatchingTag>>();

	private Integer level = 1;
	
	@Override
	public void startGameSession() {
		startGameSession("elearnFreeTaggingGame");
	}
	
	@Override
	protected void loadNewResource() {
		term = elearnTermBean.updateTerm();
	}
	
	public String recommendTag() {
		Tagging tagging = taggingBean.recommendTag(term, false);
		if (tagging.getTag() == null) {
			log.info("Could not add tag to gameround as it is invalid.");
		} else {
			gameRound.getActions().add(tagging);
			if (TerminaMatching.isAssociationInConfirmedTags(tagging.getTag().getName(), term))
				tagging.setScore(1);
			log.info("Added #0 to game round", tagging.getTag());
		}
		recommendedTags.put(gameRound.getNumber(), taggingBean.getRecommendedTags());
		return null;
	}
	
	@Override
	public void endRound() {
		gameRound.getResources().add(term);
		super.endRound();
		entityManager.flush();
		term = elearnTermBean.updateSensibleTermForFreeTagging(level);
		if (term == null) {
			level++;
		}
	}
	
	@Override
	public Integer getRoundsLeft() {
		if (elearnTermBean.updateSensibleTermForFreeTagging(level) != null)
			return 1;
		else
			return 0;
	}
	
	public List<MatchingTag> getTagsForRound(GameRound gameRound) {
		Integer roundNr = gameRound.getNumber();
		if (roundNr != null)
			return recommendedTags.get(roundNr);
		else
			return null;
	}
}
