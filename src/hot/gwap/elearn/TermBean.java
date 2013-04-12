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

import gwap.model.GameConfiguration;
import gwap.model.GameSession;
import gwap.model.Tag;
import gwap.model.resource.Term;
import gwap.tools.CustomSourceBean;

import java.io.Serializable;
import java.util.List;

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
	@In(required=false)      private GameConfiguration gameConfiguration;
	@Out(required=false)     private Term term;
	@In						 private LocaleSelector localeSelector;
	@In(required=false)      private GameSession gameSession;
	@In                      private CustomSourceBean customSourceBean;
	
	@Factory("term")
	public Term updateTerm() {
		updateSensibleTerm();
		if (term == null)
			updateRandomTerm();
		return term;
	}
	
	public Term updateTerm(GameConfiguration gameConfiguration) {
		this.gameConfiguration = gameConfiguration;
		return updateSensibleTerm();
	}
	
	private Term updateRandomTerm() {
		log.info("Updating random term");
		
		try {
			Query query = null;
			if (gameConfiguration != null && gameConfiguration.getTopic() != null) {
				query = customSourceBean.query("term.randomByTopic");
				query.setParameter("topic", gameConfiguration.getTopic());
			} else {
				query = customSourceBean.query("term.randomByLevel");
				if (gameConfiguration != null && gameConfiguration.getLevel() != null)
					query.setParameter("level", gameConfiguration.getLevel());
				else
					query.setParameter("level", 1);
			}
			query.setParameter("language", localeSelector.getLanguage());
			query.setMaxResults(1);
			term = (Term) query.getSingleResult();
			log.info("Updated random term: #0", term);
			return term;
		} catch(Exception e) {
			log.info("Could not find a random term");
			return null;
		}
	}
	
	private Term updateSensibleTerm() {
		log.info("Updating sensible term");
		
		try {
			Query query = null;
			if (gameConfiguration != null) {
				if (gameConfiguration.getTopic() != null) {
					log.info("term.sensibleRandomForGameWithTopic level=#0, minConfirmedTags=#1, topic=#2", gameConfiguration.getLevel(), gameConfiguration.getBid().longValue(), gameConfiguration.getTopic());
					query = customSourceBean.query("term.sensibleRandomForGameWithTopic");
					query.setParameter("topic", gameConfiguration.getTopic());
				} else {
					log.info("term.sensibleRandomForGame level=#0, minConfirmedTags=#1", gameConfiguration.getLevel(), gameConfiguration.getBid().longValue());
					query = customSourceBean.query("term.sensibleRandomForGame");
				}
				query.setParameter("level", gameConfiguration.getLevel());
				query.setParameter("minConfirmedTags", gameConfiguration.getBid().longValue());
			} else {
				log.info("term.sensibleRandomForGameWithoutConfig");
				query = customSourceBean.query("term.sensibleRandomForGameWithoutConfig");
			}
			query.setParameter("gameSession", gameSession);
			query.setParameter("language", localeSelector.getLanguage());
			query.setMaxResults(1);
			term = (Term) query.getSingleResult();
			
			log.info("Updated sensible term: #0", term);
			return term;
		} catch(Exception e) {
			log.info("Could not find a sensible term");
			term = null;
			return null;
		}
	}

	public Term updateSensibleTermForFreeTagging(Integer level) {
		log.info("Updated sensible term for free tagging");
		
		try {
			Query query = null;
			query = customSourceBean.query("term.randomByLevelNotInGameSession");
			query.setParameter("level", level);
			query.setParameter("gameSession", gameSession);
			query.setParameter("language", localeSelector.getLanguage());
			query.setMaxResults(1);
			term = (Term) query.getSingleResult();

			log.info("Updated sensible term for free tagging: #0", term);
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
			Query query = customSourceBean.query("term.randomTagsNotRelated");
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
