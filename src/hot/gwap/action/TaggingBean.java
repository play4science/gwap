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

package gwap.action;

import gwap.model.Tag;
import gwap.model.action.Tagging;
import gwap.model.action.TaggingCorrection;
import gwap.model.resource.ArtResource;
import gwap.model.resource.Resource;
import gwap.tools.TagSemantics;
import gwap.wrapper.MatchingTag;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;

@Name("taggingBean")
@Scope(ScopeType.PAGE)
public class TaggingBean extends AbstractTaggingBean<Tagging> {
	private static final long serialVersionUID = 1L;

	@In(create = true)
	protected Tagging tagging;

	@Observer(value = "checkForMatchingTags", create = false)
	public void checkForMatchingTags() {
		super.checkForMatchingTags();
	}

	public MatchingTag createTagging(Resource resource) {
		MatchingTag matchingTag = null;
		Tag findOrCreateTag = findOrCreateTag();
		if (findOrCreateTag != null) {
			matchingTag = new MatchingTag(recommendedTag.getName());
			recommendedTags.add(matchingTag);
			tagging.setCreated(new Date());
			tagging.setPerson(person);
			tagging.setTag(findOrCreateTag);
			tagging.setResource(resource);
			tagging.setGameRound(gameRound);
			entityManager.persist(tagging);
		} else {
			log.info("Tag '#0' was not added.", recommendedTag.getName());
		}
		return matchingTag;
	}

	public Tagging getTagging() {
		return tagging;
	}

	public MatchingTag correctTag(String originalTagName, String correctedTagName) {
		try {
			entityManager.flush();
			Query query = entityManager.createNamedQuery("tagging.taggingsByTagNameResourceAndGameround");
			query.setParameter("tagName", correctedTagName);
			query.setParameter("resource", resource);
			query.setParameter("gameRound", gameRound);
			Tagging correctedTagging = null;
			List<Tagging> resultList = query.getResultList();
			if (resultList.size() > 0) {
				log.info("Tag is already entered");
				correctedTagging = resultList.get(0);
			}
			
			query.setParameter("tagName", originalTagName);
			Tagging originalTagging = (Tagging) query.getSingleResult();
			
			MatchingTag matchingTag = getMatchingTag(originalTagName);

			TaggingCorrection taggingCorrection = new TaggingCorrection();
			taggingCorrection.setPerson(person);
			taggingCorrection.setCreated(new Date());
			taggingCorrection.setGameRound(gameRound);
			taggingCorrection.setOriginalTag(originalTagging.getTag());
			taggingCorrection.setAccepted(true);
			entityManager.persist(taggingCorrection);
			if (correctedTagging != null) {  // Corrected tag is already entered, delete second tagging
				taggingCorrection.setCorrectedTag(correctedTagging.getTag());
				recommendedTags.remove(matchingTag);
				gameRound.getActions().remove(originalTagging);
				entityManager.remove(originalTagging);
				return null;
			} else {
				Tag correctedTag = findOrCreateTag(correctedTagName);
				originalTagging.setTag(correctedTag);
				taggingCorrection.setCorrectedTag(correctedTag);
				matchingTag.setTag(correctedTagName);
				matchingTag.setAlternativeTags(null);
				matchingTag.setScore(0);
				matchingTag.setDirectMatch(false);
				matchingTag.setIndirectMatch(false);
				return matchingTag;
			}
		} catch (Exception e) {
			log.info("Could not correct tag: #1", e);
			return null;
		}
	}

	public MatchingTag getMatchingTag(String tagName) {
		for (MatchingTag tag : recommendedTags) {
			if (TagSemantics.equals(tag.getTag(), tagName))
				return tag;
		}
		return null;
	}
	
	public void skipResource() {
		if (resource instanceof ArtResource) {
			((ArtResource) resource).setSkip(true);
			log.info("Resource #0 set skip=true", resource);
			resource.setEnabled(false);
			entityManager.flush();
			Events.instance().raiseEvent("updateResource");
		}
	}
	
	public long getNumberOfTaggingsForResource() {
		Query query = entityManager.createNamedQuery("tagging.countByResourceAndLanguage");
		query.setParameter("resource", resource);
		query.setParameter("language", localeSelector.getLanguage());
		return ((Number)query.getSingleResult()).longValue();
	}

}
