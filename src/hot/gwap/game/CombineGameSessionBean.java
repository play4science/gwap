/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.game;

import gwap.ResourceBean;
import gwap.game.CombinedTagBean.MatchType;
import gwap.model.Tag;
import gwap.model.action.Combination;
import gwap.model.action.DisplayedTag;
import gwap.model.resource.ArtResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;

/**
 * @author Florian Störkle
 */

@Name("combineGameSessionBean")
@Scope(ScopeType.CONVERSATION)
public class CombineGameSessionBean extends AbstractGameSessionBean {

	private static final long serialVersionUID = 1L;
	
	public static final int INDIRECT_MATCH_SCORE = 5;
	
	public static final int DIRECT_MATCH_SCORE = 25;

	@In				    		private ArtResource resource;
	@In                         private ResourceBean resourceBean;
	@SuppressWarnings("unused")
	@In(create=true) @Out		private CombineOpponentBean combineOpponentBean;
	@Out						protected List<Tag> unselectedTags = new ArrayList<Tag>();
	
	private Map<Combination,MatchType> enteredCombinations = new HashMap<Combination,MatchType>();
	
	private boolean forceExpired = false;

	@Override
	public void startGameSession() {
		startGameSession("combino");
	}
	
	@Override
	public void startRound() {
		super.startRound();
		forceExpired = false;
	}
	
	@Override
	protected void loadNewResource() {
		resourceBean.updateResource();
	}
	
	@Override
	public void endRound() {
		log.info("endRound() currentRoundScore=#0, completedRoundScore=#1", currentRoundScore, completedRoundsScore);
		gameRound.getResources().add(resource);
		
		for (final Tag tag : unselectedTags) {
			final DisplayedTag displayedTag = new DisplayedTag(tag, resource, gameRound);
			log.info("Persisting new DisplayTag for Tag #0", tag);
			entityManager.persist(displayedTag);
		}
		unselectedTags = new ArrayList<Tag>();
		
		super.endRound();
	}

	public void forceRoundExpired() {
		forceExpired = true;
	}
	
	@Override
	public boolean roundExpired() {
		if (forceExpired) {
			return true;
		}
		
		return super.roundExpired();
	}
	
	public void removeSelectedTag(final Tag tag) {
		boolean onceMore = true;
		while (onceMore) {
			onceMore = unselectedTags.remove(tag);
		}
	}

	public void score(final Combination combination, final MatchType matchType) {
		if (currentRoundScore == null) {
			currentRoundScore = 0; 
		}
		
		final Integer currentScore = combination.getScore();
		
		if (enteredCombinations.containsKey(combination)) {
			currentRoundScore += matchType.getScore() - currentScore;
		} else {
			currentRoundScore += matchType.getScore();
		}
		
		combination.setScore(matchType.getScore());
		rememberCombination(combination, matchType);
		
		log.info("score() #0 match, scoring #1 points for #2", matchType, combination.getScore(), combination);
		log.info("score() currentRoundScore=#0", currentRoundScore);
	}
	
	private void rememberCombination(final Combination combination, final MatchType matchType) {
		enteredCombinations.put(combination, matchType);
		
		gameRound.getActions().add(combination);
	}
	
	public List<Combination> getCombinationsForGameRound(final Integer gameRoundNumber) {
		final List<Combination> combinations = new ArrayList<Combination>();
		
		for (final Combination combination : enteredCombinations.keySet()) {
			if (gameRoundNumber.equals(combination.getGameRound().getNumber())) {
				combinations.add(combination);
			}
		}
		
		return combinations;
	}
	
	public String getCssClass(final Combination combination) {
		return enteredCombinations.get(combination).getCssClass();
	}
}
