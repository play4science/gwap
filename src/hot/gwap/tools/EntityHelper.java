/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.tools;

import gwap.game.RecommendedTag;
import gwap.model.CombinedTag;
import gwap.model.Tag;
import gwap.model.action.Combination;
import gwap.model.resource.ArtResource;
import gwap.wrapper.TagFrequency;

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
public class EntityHelper {
	
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
