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

import gwap.model.resource.Resource;
import gwap.wrapper.MatchingTag;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.log.Log;

@Name("gameRoundScoringBean")
@Scope(ScopeType.STATELESS)
public class GameRoundScoringBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@In                      private EntityManager entityManager;
	@In                      private LocaleSelector localeSelector;
	@In(create=true)         private SpellCorrection spellCorrection;
	@Logger                  private Log log;
	
	private int scoreDirectMatch = 25; 
	private int scoreIndirectMatch = 5; 
	
	public void updateScore(Resource resource, MatchingTag matchingTag) {
		int score = 0;
		
		if (matchingTag.isDirectMatch()) {
			score += scoreDirectMatch;
		} else {
			if (!matchingTag.isIndirectMatch()) {
				// How often the resource is tagged with the given tag
				Query query = entityManager.createNamedQuery("tag.tagFrequencyOfResourceByNameAndLanguage");
				query.setParameter("language", localeSelector.getLanguage());
				query.setParameter("resource", resource);
				query.setParameter("tagName", matchingTag.getTag());
				Long resourceTaggingsByTagNameSize = ((Long) query.getSingleResult());
				
				log.debug("resourceTaggingsByTagNameSize: #0 (#1, #2, #3)", resourceTaggingsByTagNameSize, localeSelector.getLanguage(), resource, matchingTag.getTag());
				
				if (resourceTaggingsByTagNameSize > 1)  // One tag is the currently given tag
					matchingTag.setIndirectMatch(true);
				// Deactivate spell correction temporarily (does it fix ConcurrentRequestTimeout exceptions?)
//				else
//					spellCorrection.findSpellCorrectedTags(resource, matchingTag);
			} 
			
			if (matchingTag.isIndirectMatch())
				score += scoreIndirectMatch;

			// tag "relevance"
			//int resourceTaggingsSize = ((ArtResource)resource).getTaggings().size();
			//float tagRelevance = (float) resourceTaggingsByTagNameSize / (float) resourceTaggingsSize; 
			//score += tagRelevance * scoreFactorWithoutMatch;
		}
		matchingTag.setScore(score);
		
		log.info("Score for tag #0 (matching (direct/indirect): #1/#2): #3.", 
				matchingTag.getTag(), matchingTag.isDirectMatch(), matchingTag.isIndirectMatch(), score);
	}
	
	/*public Integer getScore(GameRound gameRound) {
		log.info("Calculating score of round #0", gameRound.getNumber());
		long millis = System.currentTimeMillis();
		List<Action> actions = gameRound.getActions();
		List<Tag> playerTags = new ArrayList<Tag>();
		
		for (Action action : actions) {
			if (action instanceof Tagging)
				playerTags.add(((Tagging)action).getTag());
		}
		List<Tag> oppponentTags = gameRound.getOpponentTags();
		
		int score = 0;
		for (Tag tag : playerTags) {
			int scoreForCurrentTag = getScore(gameRound.getResources().get(0), tag.getName(), TagSemantics.containsNotNormalized(oppponentTags, tag.getName()));
			score += scoreForCurrentTag;
		}
		log.info("End calculating score of round #0. It took #1 msec", gameRound.getNumber(), (System.currentTimeMillis()-millis));
		
		return score;
	}*/
}
