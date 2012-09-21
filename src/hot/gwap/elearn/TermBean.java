/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.elearn;

import gwap.model.GameConfiguration;
import gwap.model.GameSession;
import gwap.model.Tag;
import gwap.model.resource.Term;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.log.Log;

@Name("elearnTermBean")
@Scope(ScopeType.CONVERSATION)
public class TermBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Create  public void init()    { log.info("Creating");   }
	@Destroy public void destroy() { log.info("Destroying"); }
	
	@Logger                  private Log log;
	@In                      private FacesMessages facesMessages;
	@In                      private EntityManager entityManager;
	@In(required=false)      private GameConfiguration gameConfiguration;
	@Out(required=false)     private Term term;
	@In						 private LocaleSelector localeSelector;
	@In(required=false)      private GameSession gameSession;
	
	@Factory("term")
	public Term updateTerm() {
		updateSensibleTerm();
		if (term == null)
			updateRandomTerm();
		log.info("Updated term: #0", term);
		return term;
	}
	
	public Term updateTerm(GameConfiguration gameConfiguration) {
		this.gameConfiguration = gameConfiguration;
		return updateSensibleTerm();
	}
	
	private Term updateRandomTerm() {
		log.info("Updating Random Term");
		
		try {
			Query query = null;
			if (gameConfiguration != null && gameConfiguration.getTopic() != null) {
				query = entityManager.createNamedQuery("term.randomByTopic");
				query.setParameter("topic", gameConfiguration.getTopic());
			} else {
				query = entityManager.createNamedQuery("term.randomByLevel");
				if (gameConfiguration != null && gameConfiguration.getLevel() != null)
					query.setParameter("level", gameConfiguration.getLevel());
				else
					query.setParameter("level", 1);
			}
			query.setParameter("language", localeSelector.getLanguage());
			query.setMaxResults(1);
			term = (Term) query.getSingleResult();
			return term;
		} catch(Exception e) {
			log.info("Could not find a random term");
			return null;
		}
	}
	
	private Term updateSensibleTerm() {
		log.info("Updating Sensible Term");
		
		try {
			Query query = null;
			if (gameConfiguration != null) {
				if (gameConfiguration.getTopic() != null) {
					query = entityManager.createNamedQuery("term.sensibleRandomForGameWithTopic");
					query.setParameter("topic", gameConfiguration.getTopic());
				} else {
					query = entityManager.createNamedQuery("term.sensibleRandomForGame");
				}
				query.setParameter("level", gameConfiguration.getLevel());
				query.setParameter("minConfirmedTags", gameConfiguration.getBid().longValue());
			} else {
				query = entityManager.createNamedQuery("term.sensibleRandomForGameWithoutConfig");
			}
			query.setParameter("gameSession", gameSession);
			query.setParameter("language", localeSelector.getLanguage());
			query.setMaxResults(1);
			term = (Term) query.getSingleResult();
			return term;
		} catch(Exception e) {
			log.info("Could not find a sensible term");
			term = null;
			return null;
		}
	}

	public Term updateSensibleTermForFreeTagging(Integer level) {
		log.info("Updating Sensible Term For Free Tagging");
		
		try {
			Query query = null;
			query = entityManager.createNamedQuery("term.randomByLevelNotInGameSession");
			query.setParameter("level", level);
			query.setParameter("gameSession", gameSession);
			query.setParameter("language", localeSelector.getLanguage());
			query.setMaxResults(1);
			term = (Term) query.getSingleResult();
			return term;
		} catch(Exception e) {
			log.info("Could not find a sensible term for free tagging");
			term = null;
			return null;
		}
	}
	
	public List<Tag> updateRandomTagsNotRelated(Term relatedTerm, int maxNrResults) {
		log.info("Updating Random Tags Not Related");
		
		List<Tag> terms;
		try {
			Query query = entityManager.createNamedQuery("term.randomTagsNotRelated");
			query.setParameter("language", localeSelector.getLanguage());
			query.setParameter("term", relatedTerm);
			query.setMaxResults(maxNrResults);
			terms = (List<Tag>) query.getResultList();
			log.info("Found #0 random terms #0", terms.size());
			return terms;
		} catch(Exception e) {
			facesMessages.add("#{messages['general.noResource']}");
			log.info("Could not update random tags not related");
			return null;
		}
	}
	
}
