/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.action;

import gwap.game.OpponentBean;
import gwap.game.RecommendedTag;
import gwap.model.GameRound;
import gwap.model.Person;
import gwap.model.Tag;
import gwap.model.resource.Resource;
import gwap.tools.TagSemantics;
import gwap.wrapper.MatchingTag;
import gwap.wrapper.TagFrequency;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.log.Log;

@Scope(ScopeType.PAGE)
public abstract class AbstractTaggingBean<GenericTagging> implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Create  public void init()    { log.info("Creating"); }
	@Destroy public void destroy() { log.info("Destroying"); }
	
	@Logger                  protected Log log;
	@In                      protected FacesMessages facesMessages;
	@In                      protected EntityManager entityManager;
	@In                      protected LocaleSelector localeSelector;
	@In(required=false)      protected Resource resource;
	@In(create=true)         protected RecommendedTag recommendedTag;
	@In(create=true)         protected Person person;
	@In(create=true) @Out    protected Tag tag;
	@In(required=false)      protected GameRound gameRound;
	@In(required=false)      protected OpponentBean opponentBean;
	@In(required=false)      protected List<TagFrequency> tabooTags;
	@DataModel               protected List<MatchingTag> recommendedTags = new ArrayList<MatchingTag>();

	
	public GenericTagging recommendTag(GameRound gameRound, Resource resource) {
		this.gameRound=gameRound;		
		return recommendTag(resource, true);
	}
	
	/* SharedGame needs to set the resource before calling recommendTag
	 * However, the outjection  cycle is not completed between these calls
	 * therefore, it is not possible to set the resource in recommendedTag
	 * To solve this, overloaded methods taking parameters were added 
	 */
	public GenericTagging recommendTag() {
		return recommendTag(resource, false);		
	}
	
	public GenericTagging recommendTagForTagging() {
		return recommendTag(resource, true);		
	}
	
	public GenericTagging recommendTag(Resource resource, boolean returnNull) {
		if (recommendedTag != null)
			recommendedTag.setName(TagSemantics.normalize(recommendedTag.getName()));
		
		if (recommendedTag == null || recommendedTag.getName() == null || recommendedTag.getName().length() == 0) {
			if (returnNull)
				return null;
			else
				return getTagging();
		}
		
		boolean recommendedTagInTabooTags = false;
		if (tabooTags != null) {
			// Tag already in tabooTags
			recommendedTagInTabooTags = TagSemantics.containsNotNormalized2(tabooTags, recommendedTag.getName()) != null;
		}
		
		// Tag already in recommended tags?
		boolean recommendedTagInRecommendedTags = false;
		for (MatchingTag matchingTag : recommendedTags) {
			recommendedTagInRecommendedTags |= TagSemantics.equals(recommendedTag.getName(), matchingTag.getTag());
		}
		
		// Valid tagging or not?
		if (recommendedTagInRecommendedTags) {
			facesMessages.add("#{messages['taggingBean.tagExistsAlready']}");
			if (returnNull)
				return null;
		} else if (recommendedTagInTabooTags) {
			facesMessages.add("#{messages['game.tabooImageLabeler.tabooTagMatch']}");
				if (returnNull)
					return null;
		} else {
			MatchingTag matchingTag = createTagging(resource);
//			Events.instance().raiseEvent("checkForMatchingTags");
			// the clause 'opponentBean != null' can be removed when GameSessionBeanNew and GameSessionBean have been merged
			if (!matchingTag.isDirectMatch() && opponentBean != null)
				Events.instance().raiseEvent("updateScore", matchingTag);
		}
		return getTagging();
	}
	
	protected abstract MatchingTag createTagging(Resource resource);
	
	public void checkForMatchingTags() {
		if (opponentBean != null) {
			List<Tag> opponentTags = opponentBean.getOpponentTags();
			for (Tag opponentTag : opponentTags) {
				for (MatchingTag recommendedTag : recommendedTags) {
					if (!recommendedTag.isDirectMatch()
							&& TagSemantics.equals(opponentTag.getName(), recommendedTag.getTag())) {
						recommendedTag.setDirectMatch(true);
						Events.instance().raiseEvent("updateScore", recommendedTag);
					}
				}
			}
		}
	}
	
	public Tag findOrCreateTag() {
		return findOrCreateTag(recommendedTag.getName());
	}
	
	/**
	 * Important: Normalize the tag first, e.g. with TagSemantics.normalize()
	 *  
	 * @param recommendedTagName
	 * @return
	 */
	public Tag findOrCreateTag(String recommendedTagName) {
		String language = localeSelector.getLanguage();

        if (recommendedTagName.length() > 0) {			
			log.info("Added '#0' to recommended tags.", recommendedTagName);
	
			Query query = entityManager.createNamedQuery("tag.tagByNameAndLanguage");
			query.setParameter("language", language);
			query.setParameter("name", recommendedTagName);
			try {
				tag = (Tag) query.getSingleResult();
			} catch (NonUniqueResultException e) {
				log.error("The tag #0 (#1) is not unique", recommendedTagName, language);
				@SuppressWarnings("unchecked")
				List<Tag> tagList = query.getResultList();
				tag = tagList.get(0);
			} catch(NoResultException e) {
				log.info("The tag #0 (#1) is new", recommendedTagName, language);
				tag.setName(recommendedTagName);
				tag.setLanguage(language);
				entityManager.persist(tag);
			}
			return tag;
        } else
        	return null;		
	}
	
	public void cancelResource() {
		recommendedTags = new ArrayList<MatchingTag>();
		log.info("Resource cancelled.");
	}
	
	public int getCurrentScore() {
		int score = 0;
		for (MatchingTag t : recommendedTags) {
			score += t.getScore();
		}
		return score;
	}
	
	public List<MatchingTag> getRecommendedTags() {
		return recommendedTags;
	}
	
	public abstract GenericTagging getTagging();	
}
