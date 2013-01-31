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

package gwap.game;

import gwap.model.CombinedTag;
import gwap.model.Tag;
import gwap.model.action.Combination;
import gwap.model.resource.ArtResource;
import gwap.tools.EntityHelper;
import gwap.widget.TagCloudBean;
import gwap.wrapper.TagFrequency;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.log.Log;

/**
 * @author Florian Störkle
 */

@Name("combinedTagBean")
@Scope(ScopeType.PAGE)
public class CombinedTagBean implements Serializable {
	
	enum MatchType {
		DIRECT (25, "directMatch"),
		INDIRECT (5, "indirectMatch"),
		NONE (0, "");
		
		private String cssClass;
		private int score;
		
		MatchType(final int score, final String cssClass) {
			this.score = score;
			this.cssClass = cssClass;
		}
		
		String getCssClass() {
			return this.cssClass;
		}
		
		int getScore() {
			return this.score;
		}
		
		@Override
		public String toString() {
			return this.name().toLowerCase();
		}
	}

	private static final long serialVersionUID = 1;
	public static final int TAG_LIST_SIZE = 15;
	
	private final Tag EMPTY_TAG = new Tag();
	
	@Logger                 private Log log;
	@In                     protected EntityManager entityManager;
	@In(create=true)		private EntityHelper entityHelper;
	@In                     private LocaleSelector localeSelector;
	@In(create=true)		private TagCloudBean tagCloudBean;
	@In						private CombineOpponentBean combineOpponentBean;
	@In						private CombineGameSessionBean combineGameSessionBean;
	
	@In(create=true) @Out	private ArtResource resource;
	@In(create=true)		private RecommendedTag recommendedTag;
	
	@In						private List<Tag> unselectedTags;
	
	@Out					protected List<Tag> displayedTags;
	@Out					protected List<Combination> combinations = new ArrayList<Combination>();
	
	private List<Combination> combinationsToTest = new ArrayList<Combination>();
	private Map<CombinedTag,MatchType> matches = new HashMap<CombinedTag,MatchType>();
	private CombinedTag combinedTag;
	
	private List<TagFrequency> currentTagFrequencies;
	
	private boolean hasNewTags = true;
	
	@Create
	public void init() {
		log.info("Creating");
		
		EMPTY_TAG.setName("…");
		
		combineOpponentBean.initAllOpponentTags();
	}
	
	@Destroy
	public void destroy() { log.info("Destroying"); }
	
	@Observer(value = "checkForMatchingCombinedTags", create = false)
	public boolean checkForMatchingCombinedTags() {
		for (final CombinedTag tag : combineOpponentBean.getOpponentTags()) {
			
			final Iterator<Combination> combinations = combinationsToTest.iterator();
			while (combinations.hasNext()) {
				final Combination combination = combinations.next();
				
				log.info("Comaring #0 and #1", combination.getCombinedTag(), tag);
				
				if (combination.getCombinedTag().equals(tag)) {
					score(combination, MatchType.DIRECT);
					
					combinations.remove();
					
					return true;
				}
			}
		}
		
		return false;
	}
	
	@Factory("displayedTags")
	public void updateDisplayedTags() {
		if (currentTagFrequencies == null) {
			currentTagFrequencies = tagCloudBean.getTagCloud(resource, 2L, TAG_LIST_SIZE * 10);
			
			log.info("updateDisplayedTags(): fetched #0 tags", currentTagFrequencies.size());
			
			Collections.shuffle(currentTagFrequencies);
		}
		
		if (displayedTags == null) {
			displayedTags = new ArrayList<Tag>();
		}
		
		final int size = Math.min(currentTagFrequencies.size(), TAG_LIST_SIZE);
		log.info("updateDisplayedTags(): adding #0 more tags", size);
		if (size < TAG_LIST_SIZE) {
			hasNewTags = false;
			return;
		}
		
		final List<TagFrequency> frequencies = new ArrayList<TagFrequency>(currentTagFrequencies.subList(0, size));
		currentTagFrequencies.removeAll(frequencies);
		
		hasNewTags = displayedTags.addAll(entityHelper.fetchTagsForTagFrequencies(frequencies));
		
		if (hasNewTags && frequencies.size() < TAG_LIST_SIZE) {
			hasNewTags = false;
		}
	}
	
	public void resetDisplayedTags() {
		displayedTags = null;
	}

	public String selectTag() {
		if (combineGameSessionBean.roundExpired()) {
			log.info("Game round expired");
			return "next";
		}
		
		if (recommendedTag == null) {
			log.warn("recommendedTag was null");
			return null;
		}
		
		final Tag tag = entityHelper.findTag(recommendedTag);
		
		combineGameSessionBean.removeSelectedTag(tag);
		
		if (combinedTag == null) {
			combinedTag = createCombinedTag(tag);
			
			addCombination();
		} else {
			if (combinedTag.getFirstTag().getId().equals(tag.getId())) {
				log.info("Two same tags cannot be combined");
				return null;
			}
			combinedTag.setSecondTag(tag);
			
			combineTags();
			
			recommendedTag = null;
			combinedTag = null;
		}
		
		return null;
	}
	
	
	public String getCssClass(final Combination combination) {
		final MatchType matchType = matches.get(combination.getCombinedTag());
		return matchType != null ? matchType.getCssClass() : "";
	}
	
	private void addCombination() {
		final Combination newCombination = createCombination(combinedTag); 
		combinations.add(0, newCombination);
		matches.put(newCombination.getCombinedTag(), MatchType.NONE);
	}
	
	private void combineTags() {
		final CombinedTag existingCombinedTag = entityHelper.findCombinedTag(combinedTag);
	
		if (existingCombinedTag != null) {
			combinedTag = existingCombinedTag;
			log.info("Found existing CombinedTag: #0", existingCombinedTag);
		} else {
			log.info("CombinedTag not found, persisting new one: #0", combinedTag);
			entityManager.persist(combinedTag);
		}
		
		final Combination existingCombination = entityHelper.findCombination(combinedTag);
		final Combination currentCombination = combinations.get(0);
		currentCombination.setCombinedTag(combinedTag);
		log.info("Persisting combination: #0", currentCombination);
		entityManager.persist(currentCombination);
		
		final MatchType matchType;
		
		if (existingCombination != null) {
			if (combinations.contains(existingCombination)) {
				// duplicate entry
				log.info("duplicate: #0", existingCombination.getCombinedTag());
				combinations.remove(0);
				return;
			}
			
			combinationsToTest.add(currentCombination);
			
			if (checkForMatchingCombinedTags()) {
				// direct match: scoring already happened, quit
				return;
			}

			// indirect match: combination was entered before, but not in the current session
			matchType = MatchType.INDIRECT;
		} else {
			matchType = MatchType.NONE;
		}
		
		score(currentCombination, matchType);
	}
	
	private void score(Combination combination, final MatchType type) {
		matches.put(combination.getCombinedTag(), type);
		combineGameSessionBean.score(combination, type);
	}
	
	private CombinedTag createCombinedTag(final Tag tag) {
		final CombinedTag combinedTag = new CombinedTag(tag, EMPTY_TAG);
		return combinedTag;
	}
	
	private Combination createCombination(final CombinedTag combinedTag) {
		final Combination combination = new Combination(combinedTag);
		combineGameSessionBean.initializeAction(combination);
		combination.setResource(resource);
		combination.setLanguage(localeSelector.getLanguage());
		return combination;
	}
	
	@BypassInterceptors
	public boolean hasNewTags() {
		return hasNewTags;
	}
}
