/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.elearn;

import gwap.model.GameConfiguration;
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
	
	@Factory("term")
	public Term updateTerm() {
		return updateRandomTerm();
	}
	
	public Term updateRandomTerm() {
		log.info("Updating Random Term");
		
		try {
			Query query = entityManager.createNamedQuery("term.randomByLevel");
			query.setParameter("language", localeSelector.getLanguage());
			if (gameConfiguration != null)
				query.setParameter("level", gameConfiguration.getLevel());
			else
				query.setParameter("level", 1);
			query.setMaxResults(1);
			term = (Term) query.getSingleResult();
			log.info("Found random term #0", term);
			return term;
		} catch(Exception e) {
			facesMessages.add("#{messages['general.noResource']}");
			log.info("Could not find a random term");
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
			log.info("Could not find a random term");
			return null;
		}
	}
	public Term updateRandomTermMinConfirmedTags(int maxNrResults) {
		log.info("Updating Random Term");
		
		try {
			Query query = entityManager.createNamedQuery("term.randomByLevelMinConfirmedTags");
			query.setParameter("language", localeSelector.getLanguage());
			if (gameConfiguration != null)
				query.setParameter("level", gameConfiguration.getLevel());
			else
				query.setParameter("level", 1);
			query.setParameter("minConfirmedTags", maxNrResults);
			query.setMaxResults(1);
			term = (Term) query.getSingleResult();
			log.info("Found random term #0", term);
			return term;
		} catch(Exception e) {
			facesMessages.add("#{messages['general.noResource']}");
			log.info("Could not find a random term");
			return null;
		}
	}
	
}
