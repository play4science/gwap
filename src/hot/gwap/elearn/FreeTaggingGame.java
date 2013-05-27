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
			if (TerminaMatching.isAssociationInList(tagging.getTag().getName(), term.getConfirmedTags()))
				tagging.setScore(1);
			if (TerminaMatching.isAssociationInList(tagging.getTag().getName(), term.getRejectedTags()))
				tagging.setScore(-1);
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
		term = elearnTermBean.updateSensibleTermForFreeTagging();
	}
	
	@Override
	public Integer getRoundsLeft() {
		if (elearnTermBean.updateSensibleTermForFreeTagging() != null)
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
