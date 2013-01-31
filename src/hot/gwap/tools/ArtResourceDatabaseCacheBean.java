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

import gwap.model.resource.ArtResource;
import gwap.model.resource.ArtResourceCache;

import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.log.Log;

/**
 * Service Bean for the ArtResourceLeastTagged entity
 * 
 * @author Christoph Wieser
 */

@Name("artResourceDatabaseCacheBean")
public class ArtResourceDatabaseCacheBean  implements ArtResourceCacheBean {

	@In	              EntityManager entityManager;
	@In (create=true) CustomSourceBean customSourceBean;
	@In               private LocaleSelector localeSelector;
	@Logger           Log log;
	
	private final int POOLSIZE = 100;
	private final double RELOADTRIGGER = 0.1;

	private final long MINTAGGINGS = 10;
	
	@SuppressWarnings("unchecked")
	private void updateCandidates(String name) {
		// Check number of remaining candidate ArtResources having few tags.
		
		Query pool = customSourceBean.query("artResourceCache.countByLanguageName");
		pool.setParameter("name", name);
		pool.setParameter("language", localeSelector.getLanguage());
		Number poolSize = (Number) pool.getSingleResult();
		
		// Reload Pool if needed
		if(poolSize.intValue() < POOLSIZE * RELOADTRIGGER) {

			ArrayList<Long> candidateArtResourceIdList = null;
			if (name.equals("least")) {
				log.info("Updating candidate pool of ArtResources with least taggings.");
				
				// Check for untagged resources
				Query notTaggedArtResources = customSourceBean.query("artResource.notTaggedResourceId");
				notTaggedArtResources.setMaxResults((int) (POOLSIZE * (1-RELOADTRIGGER)));
				candidateArtResourceIdList = (ArrayList<Long>) notTaggedArtResources.getResultList();
				
				// Check for resources, if no untagged resources exist  
				if (candidateArtResourceIdList.size()==0) {
					Query leastTaggedArtResources = customSourceBean.query("artResource.leastTaggedResourceId");
					leastTaggedArtResources.setParameter("language", localeSelector.getLanguage());
					leastTaggedArtResources.setMaxResults((int) (POOLSIZE * (1-RELOADTRIGGER)));
					candidateArtResourceIdList = (ArrayList<Long>) leastTaggedArtResources.getResultList();
				}
			} else if (name.equals("atLeast")) {
				log.info("Updating candidate pool of ArtResources with at least #0 taggings.", MINTAGGINGS);
				
				// Check for resources  
				Query query = customSourceBean.query("artResource.atLeastTaggedResourceByLanguageId");
				query.setMaxResults((int) (POOLSIZE * (1-RELOADTRIGGER)));
				query.setParameter("minTaggings", MINTAGGINGS);
				query.setParameter("language", localeSelector.getLanguage());
				candidateArtResourceIdList = (ArrayList<Long>) query.getResultList();
			} else if (name.equals("leastWithTeaser")) {
				log.info("Updating candidate pool of ArtResources with least taggings with teaser.");
				
				// Check for untagged resources
				Query notTaggedArtResources = customSourceBean.query("artResource.notTaggedResourceIdWithTeaser");
				notTaggedArtResources.setMaxResults((int) (POOLSIZE * (1-RELOADTRIGGER)));
				candidateArtResourceIdList = (ArrayList<Long>) notTaggedArtResources.getResultList();
				
				// Check for resources, if no untagged resources exist  
				if (candidateArtResourceIdList.size()==0) {
					Query leastTaggedArtResources = customSourceBean.query("artResource.leastTaggedResourceIdWithTeaser");
					leastTaggedArtResources.setParameter("language", localeSelector.getLanguage());
					leastTaggedArtResources.setMaxResults((int) (POOLSIZE * (1-RELOADTRIGGER)));
					candidateArtResourceIdList = (ArrayList<Long>) leastTaggedArtResources.getResultList();
				}
			} else if (name.equals("atLeastForCombino")) {
				final long minTaggings = 220; // equal to circa 23 confirmed taggings
				
				log.info("Updating candidate pool of ArtResources with at least #0 taggings for combination.", minTaggings);
				
				// Check for resources  
				Query query = customSourceBean.query("artResource.atLeastTaggedResourceByLanguageId");
				query.setMaxResults((int) (POOLSIZE * (1-RELOADTRIGGER)));
				query.setParameter("minTaggings", minTaggings);
				query.setParameter("language", localeSelector.getLanguage());
				candidateArtResourceIdList = (ArrayList<Long>) query.getResultList();
			}
			
			// Persist new candidates
			Query artResourceQuery = entityManager.createNamedQuery("artResource.byIdNotInCache");
			int nrAddedResources = 0;
			for (Long artResourceId : candidateArtResourceIdList) {
				try {
					artResourceQuery.setParameter("id", artResourceId);
					artResourceQuery.setParameter("name", name);
					artResourceQuery.setParameter("language", localeSelector.getLanguage());
					ArtResource artResource = (ArtResource) artResourceQuery.getSingleResult();
					
					ArtResourceCache artResourceCache = new ArtResourceCache();
					
					
					artResourceCache.setArtResource(artResource);
					artResourceCache.setName(name);
					artResourceCache.setSource(artResource.getSource());
					artResourceCache.setLanguage(localeSelector.getLanguage());
					
					entityManager.persist(artResourceCache);
					
					nrAddedResources++;
				} catch (NoResultException e) { /* ArtResource is already in cache */ }
			}
			entityManager.flush();
			log.info("Added #0 resources to candidate pool, #1 were skipped because they already were in candidate pool", 
					nrAddedResources, candidateArtResourceIdList.size()-nrAddedResources);
		}
	}
	
	/* (non-Javadoc)
	 * @see gwap.tools.ArtResouceCacheBean#getArtResource(java.lang.String)
	 */
	@Override
	public ArtResource getArtResource(String name) {
		// still enough candidates?
		updateCandidates(name);
		
		// extract ArtResource
		Query query = customSourceBean.query("artResourceCache.allByLanguageRandomName");
		query.setParameter("name", name);
		query.setParameter("language", localeSelector.getLanguage());
		query.setMaxResults(1);
		
		ArtResource artResource = null;
		try {
			ArtResourceCache artResourceCache = (ArtResourceCache) query.getSingleResult();
			artResource = artResourceCache.getArtResource();

			// delete ArtResource from pool
			entityManager.remove(artResourceCache);
			entityManager.flush();
		} catch (Exception e) {
			log.info("No #0 artresource available", name);
		}
		
		return artResource;
	}
}
