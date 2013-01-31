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

package gwap.tools;

import gwap.game.RecommendedTag;
import gwap.model.CombinedTag;
import gwap.model.Tag;
import gwap.model.action.Combination;
import gwap.model.resource.ArtResource;
import gwap.wrapper.TagFrequency;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.log.Log;

/**
 * @author Florian Störkle
 */
@Name("entityHelper")
@Scope(ScopeType.CONVERSATION)
public class EntityHelper implements Serializable {
	
	@Logger private Log log;
	@In 	private EntityManager entityManager;
	@In		private ArtResource resource;
	@In 	private LocaleSelector localeSelector;
	
	private String getLanguage() {
		return localeSelector.getLanguage();
	}
	
	@SuppressWarnings("unchecked")
	private <T> T wrapSingleResult(final Class<T> clazz, final Query query) {
		try {
			return (T) query.setMaxResults(1).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public CombinedTag findCombinedTag(final CombinedTag combinedTag) {
		return wrapSingleResult(CombinedTag.class, entityManager
				.createNamedQuery("combination.combinedTagsSimpleByLanguage")
				.setParameter("firstTag", combinedTag.getFirstTag())
				.setParameter("secondTag", combinedTag.getSecondTag())
				.setParameter("language", getLanguage()));
	}
	
	public Combination findCombination(final CombinedTag combinedTag) {
		return wrapSingleResult(Combination.class, entityManager
				.createNamedQuery("combination.combinationByResourceAndLanguageAndCombinedTag")
				.setParameter("language", getLanguage())
				.setParameter("resource", resource)
				.setParameter("combinedTag", combinedTag));
	}
	
	public Tag findTag(final RecommendedTag recommendedTag) {
		return wrapSingleResult(Tag.class, entityManager
				.createNamedQuery("tag.tagByNameAndLanguage")
				.setParameter("name", recommendedTag.getName())
				.setParameter("language", getLanguage()));
	}

	@SuppressWarnings("unchecked")
	public List<Tag> fetchTagsForTagFrequencies(final List<TagFrequency> tagFrequencies) {
		if (tagFrequencies.size() == 0) {
			log.warn("fetchTagsForTagFrequencies(): no TagFrequencies given");
			return new ArrayList<Tag>();
		}
		
		final List<Long> tagIds = new ArrayList<Long>(tagFrequencies.size());
		
		for (final TagFrequency tag : tagFrequencies) {
			tagIds.add(tag.getTagId());
		}
		
		return entityManager.createNamedQuery("tag.byIds").setParameter("ids", tagIds).getResultList();
	}
	
}
