/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.mit;

import gwap.model.GameSession;
import gwap.model.Person;
import gwap.model.resource.AudioResource;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

/**
 * @author Fabian Kneißl
 */
@Name("mitAudioResourceBean")
@Scope(ScopeType.CONVERSATION)
public class AudioResourceBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Logger				private Log log;
	@In					private EntityManager entityManager;
	@In(create=true)    private Person person;
	@In(required=false) private GameSession gameSession;
	@Out(required=false)private AudioResource audioResource;
	@In                 private FacesMessages facesMessages;

	@Factory("audioResource")
	public AudioResource getAudioResource() {
		try {
//			AudioResourceCandidatesCacheBean audioResourceCandidatesCacheBean = 
//				(AudioResourceCandidatesCacheBean) Component.getInstance("mitAudioResourceCandidatesCacheBean");
			
//			Long randomResourceId = audioResourceCandidatesCacheBean.randomResourceId();
//			if (randomResourceId == null) {
//				log.info("No more audio resources left for the current user");
//				return null;
//			}
//			audioResource = entityManager.find(AudioResource.class, randomResourceId);
			try {
				if (gameSession == null)
					throw new NoResultException();
				log.info("Get audio resource with unique location");
				Query query = entityManager.createNamedQuery("audioResource.randomNotAssignedUniqueLocationInGamesession");
				query.setParameter("person", person);
				query.setParameter("gamesession", gameSession);
				query.setMaxResults(1);
				audioResource = (AudioResource) query.getSingleResult();
			} catch (NoResultException e) {
				log.info("Get random audio resource");
				Query query = entityManager.createNamedQuery("audioResource.randomNotAssigned");
				query.setParameter("person", person);
				query.setMaxResults(1);
				audioResource = (AudioResource) query.getSingleResult();
			}
		} catch (NoResultException e) {
			log.info("No more audio resources available to load");
			facesMessages.add("#{messages['general.noAudioResource']}");
			audioResource = null;
		} catch(Exception e) {
			log.warn("Could not get an audioResource", e);
		}
		log.info("Chose audioResource #0", audioResource);
		return audioResource;
	}

}
