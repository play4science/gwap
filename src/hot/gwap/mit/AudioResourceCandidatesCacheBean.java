/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.mit;

import gwap.model.Person;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

/**
 * Caches a list of audio resources which are can be shown to the user
 * during one session
 * 
 * @author Fabian Kneißl
 */
@Name("mitAudioResourceCandidatesCacheBean")
@Scope(ScopeType.SESSION)
public class AudioResourceCandidatesCacheBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Create                  public void init()    { log.info("Creating");  loadAtLeastTaggedResources(); }
	@Logger				private Log log;
	@In					private EntityManager entityManager;
	@In(create=true)    private Person person;
	
	private List<Long> atLeastTaggedResourcesCandidates;
	
	/**
	 * Pre-compute resource candidates. Computations between game rounds to determine
	 * a new resource would last too long. 
	 */
	@SuppressWarnings("unchecked")
	public void loadAtLeastTaggedResources() {
		log.info("Loading atLeastTaggedResources");
		//FIXME: improve
		Query query = entityManager.createNamedQuery("audioResource.randomEnabledId");
		query.setParameter("person", person);
		atLeastTaggedResourcesCandidates = (List<Long>) query.getResultList();
	}
	
	public Long randomResourceId() {
//		if (atLeastTaggedResourcesCandidates.isEmpty())
//			loadAtLeastTaggedResources();
		
		int candidateListSize = atLeastTaggedResourcesCandidates.size();
		if (candidateListSize == 0)
			return null;
				
		log.info("Choosing random resource out of #0", candidateListSize);
		Long resourceId = atLeastTaggedResourcesCandidates.remove(0);
		return resourceId;
	}

}
