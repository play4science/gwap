/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap;

import gwap.tools.CustomSourceBean;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

/**
 * Caches a list of resources which are can be shown to the user
 * during one session
 * 
 * @author Fabian Kneißl
 */
@Name("resourceCandidatesCacheBean")
@Scope(ScopeType.SESSION)
@Deprecated
public class ResourceCandidatesCacheBean implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Create                  public void init()    { log.info("Creating");  loadAtLeastTaggedResources(); }
	@Logger                  private Log log;
	@In(create=true)         private CustomSourceBean customSourceBean;
	
	
	private List<Long> atLeastTaggedResourcesCandidates;
	
	private Long minTaggings = 10L; // select resources for the game having at least 20 taggings;
	
	private int maxCachedResources = 100;
	
	/**
	 * Pre-compute resource candidates. Computations between game rounds to determine
	 * a new resource would last too long. 
	 */
	@SuppressWarnings("unchecked")
	public void loadAtLeastTaggedResources() {
		log.info("Loading atLeastTaggedResources");
		Query query = customSourceBean.query("artResource.atLeastTaggedResourceId");
		query.setMaxResults(maxCachedResources);
		query.setParameter("minTaggings", minTaggings);
		atLeastTaggedResourcesCandidates = (List<Long>) query.getResultList();
	}
	
	public Long randomResourceId() {
		// Choose pre-computed candidate id randomly.
		Random random = new Random();
		if (atLeastTaggedResourcesCandidates.isEmpty())
			loadAtLeastTaggedResources();
		
		int candidateListSize = atLeastTaggedResourcesCandidates.size();
		int chosenIndex = random.nextInt(candidateListSize);
		
		log.info("Choosing random resource #0 of #1", chosenIndex, candidateListSize);
		
		//FIXME: what happens if there are no resources left? 
		
		// remove consumed id from candidate list
		Long resourceId = atLeastTaggedResourcesCandidates.remove(chosenIndex);
		return resourceId;
	}
}
