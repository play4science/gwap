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

package gwap.game.memory;

import gwap.model.resource.ArtResource;
import gwap.tools.ArtResourceFrequency;
import gwap.tools.DBLimiter;
import gwap.tools.Timer;
import gwap.widget.TagCloudBean;
import gwap.wrapper.TagFrequency;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Synchronized;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.log.Log;

@Name("resourceGridBean")
@Scope(ScopeType.PAGE)
@Synchronized(timeout=10000)
public class ResourceGridBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private static final int numImages = 3;
	private static final long MIN_TAGGINGS=5;
	
	@Create	 public void init()    { log.info("Creating"); }
	@Destroy public void destroy() { log.info("Destroying"); }
	
	@Logger	                 private Log log;
	@In	                     private EntityManager entityManager;
	@In(create=true)		 private DBLimiter dbLimiter;

	@In(create=true)		 private ForceBean gwapGameMemoryForceBean;
	@In                      private FacesMessages facesMessages;
	
	@In						 private Player 		gwapGameMemoryPlayer;
	
	@In						 private LocaleSelector localeSelector;

	@In(create=true)		 private ReplayBean gwapGameMemoryReplayBean;
	@In(create=true)		 private TagCloudBean tagCloudBean;
	//@Out(required=false) 	 private ArtResource resource; 

	
	private List<ArtResource> resources0=new ArrayList<ArtResource>();
	private List<ArtResource> resources1;
	
	private List<TagFrequency> goalTags;
	
	private ArtResource goal;

	private ArtResource selected;

	private int resourceCount;
	
	public int getResourceCount() {
		return resourceCount;
	}
	
	public void setGoal(ArtResource goal) {
		this.goal = goal;
		
		if (goal!=null)
		{
			goalTags=tagCloudBean.getTagCloud(goal, 0L, 30);
			log.info("Goal set: #0, #1 tags", goal, goalTags.size());
		}
		else
			goalTags=null;
	}
	public ArtResource getGoal() {
		return goal;
	}
		
	public List<TagFrequency> getGoalTags() {
		return goalTags;
	}
	
	public void removeGoalTag(TagFrequency tag)
	{
		goalTags.remove(tag);		
	}
	
	/*public ArtResource getResource()
	{
		resource=goal;
		
		return resource;	
	}*/


	public ArtResource getSelected() {
		return selected;
	}
	public void setSelected(ArtResource selected) {
		this.selected = selected;
	}
	
	private void updateResourcesFromPrevious()
	{
		log.info("Updating Resource Grid with previous round");
		resources0=gwapGameMemoryReplayBean.updatePreviousRound(numImages*numImages, gwapGameMemoryForceBean.getForcedId());
		
		if (resources0==null)
		{
			resources0=new ArrayList<ArtResource>();
			return;
		}		

		List<ArtResource> resourcesPlayed=gwapGameMemoryReplayBean.getResourcesPlayed();
				
		if (resourcesPlayed!=null)		
			setGoal(resourcesPlayed.remove(0));
		else
			setGoal(resources0.get(new Random().nextInt(resources0.size())));

		if (resources0.size()>numImages*numImages)
		{
			log.info("Round id has too many played resources: #0", gwapGameMemoryReplayBean.getReplayRoundId());
			resources0=resources0.subList(0, numImages*numImages);		
		}
		
		log.info("Added #0 resources", resources0.size());
	}
	
	@SuppressWarnings("unchecked")
	public void updateResourceGridBean(boolean replay, boolean requireTagged) {
		setGoal(null);
		resources0=new ArrayList<ArtResource>();
		
		if (replay || gwapGameMemoryForceBean.getForcedId()!=0)
		{
			updateResourcesFromPrevious();				
		}
		
		if (goal==null)
		{
			log.info("Updating random base resource");
			
			Timer t=new Timer();
			
			Query query;
			
			//Select base resource by number of tags
			if (requireTagged)
			{
				log.info("Selecting with more than #0 taggings", MIN_TAGGINGS);
				query = entityManager.createNamedQuery("artResource.atLeastTaggedResourceIdLimit");
				query.setParameter("minTaggings", MIN_TAGGINGS);
			}
			else
			{
				log.info("Selecting least tagged resource", MIN_TAGGINGS);
				query = entityManager.createNamedQuery("artResource.leastTaggedResourceIdLimit");				
			}
				
			query.setParameter("language", localeSelector.getLanguage());
			
			dbLimiter.PrepareLimitedQuery(query);
			query.setMaxResults(100);
			List<Long> resGoal=query.getResultList();
			
			log.info("Found #0 resources.", resGoal.size());
			log.info("artResource.(at)leastTaggedResourceId: Query time: #0", t.timePassed());

			if (resGoal.size()>0)
			{
				//Randomly select from bottom 20%
				/*int limit=Math.min(Math.max(resGoal.size()/5, 20), resGoal.size());

				resGoal=resGoal.subList(0, limit);
				Random rand=new Random();
				setGoal(entityManager.find(ArtResource.class, resGoal.get(rand.nextInt(limit))));*/
				
				setGoal(entityManager.find(ArtResource.class, resGoal.get(new Random().nextInt(resGoal.size()))));				
			}
			else	//If no suitable resource is found (ie there is no tagged resource), pick a random one
			{
				log.info("Selecting random resource");
				t=new Timer();
				
				Query query2 = entityManager.createNamedQuery("artResource.random");				
				query2.setMaxResults(1);				
				setGoal((ArtResource)query2.getSingleResult());
				
				log.info("artResource.random: Query time: #0", t.timePassed());
			}
			resources0.add(goal);
	
			log.info("Selected goal "+goal.getId());
		}
		
		if (resources0.size()<numImages*numImages)
		{
			log.info("Adding similar resources to resource grid");
					
			// Get all resources 
			try {			
				Timer t=new Timer();
				
				Query query;
				
				if (requireTagged)
				{
					query = entityManager.createNamedQuery("artResource.bySimilarityIdAndNotIdListAtLeastTaggedLimit");
					query.setParameter("minTaggings", MIN_TAGGINGS);
				}
				else
					query = entityManager.createNamedQuery("artResource.bySimilarityIdAndNotIdListLimit");
					
				query.setParameter("id", goal.getId());
				query.setParameter("others", resources0); 
				query.setParameter("lang", localeSelector.getLanguage());
				query.setMaxResults(numImages*numImages*4);				
				dbLimiter.PrepareLimitedQuery(query);			

				List<ArtResourceFrequency> resF=query.getResultList();
				
				log.info("artResource.bySimilarityId Query time: #0", t.timePassed());
				
				log.info("Found #0 similar resources", resF.size());
				
				//Collections.sort(resF);
				//artResourceFrequencyBean.normalizeAll(resF);

				Collections.shuffle(resF);
				
				List<ArtResource> res=new ArrayList<ArtResource>();				
				
				int i=0;
				//int limit=Math.min(Math.max(resF.size()/10, numImages*numImages-resources0.size()), resF.size());
				
				//Randomly select from all images				
				int limit=Math.min(numImages*numImages-resources0.size(), resF.size());
				for (ArtResourceFrequency r : resF)
				{						
					res.add(r.getResource());
					if (++i>=limit)
						break;
				}
				Collections.shuffle(res);
				resources0.addAll(res);				

				//Take random images if not enough matching ones were found
				if (resources0.size()<numImages*numImages)
				{
					log.info("Adding random resources");
										
					t=new Timer();
					
					Query query2 = entityManager.createNamedQuery("artResource.byNotIdListRandom");
					query2.setParameter("others", resources0); 
					query2.setMaxResults(numImages*numImages-resources0.size());
					
					log.info("artResource.byNotIdListRandom Query time: #0", t.timePassed());
	
					res=query2.getResultList();
					
					if (res.size()<numImages*numImages-resources0.size())
					{
						facesMessages.add("#{messages['general.noResource']}");
						return;
					}
					resources0.addAll(res);
				}
			} catch(Exception e) {
				log.info("Failed: #0", e.getMessage());
				facesMessages.add("#{messages['general.noResource']}");
			}
		}
		
		log.info("Done");
		Collections.shuffle(resources0);
		resources1=new ArrayList<ArtResource>(resources0);
		Collections.shuffle(resources1);
		
		resourceCount=resources0.size();
		if (resourceCount!=numImages*numImages)		
			log.info("Wrong resourceCount: #0, expected #1. This shouldn't happen!", resourceCount, numImages*numImages);		
		else
		{
			log.info("Found #0 resources:", resourceCount);
			for (ArtResource res : resources0)
			{
				log.info("#0", res.getId());			
			}
		}
	}
	
	private void removeResource(List<ArtResource> l, ArtResource r)
	{
		int index=l.indexOf(r);
		l.remove(index);
		l.add(index, null);
	}
	
	public void removeGoalResource()
	{
		if (resourceCount<=1)
		{
			log.info("resourceCount<=1 shouldn't happen");
		}
		
		if (goal!=null)
		{
			//Remove current resource from list, replace with null value
			removeResource(resources0, goal);
			removeResource(resources1, goal);
						
			setGoal(null);
			
			resourceCount--;
		}
	}
	
	public List<ArtResource> getResources() {
		//Randomize order for different players
		if (gwapGameMemoryPlayer.getId()==0)
			return resources0;
		else
			return resources1;			
	}

	public List<ArtResource> getValidResources() {
		List<ArtResource> res=new ArrayList<ArtResource>();
		
		for (ArtResource r : resources0)
		{
			if (r!=null)
				res.add(r);		
		}
		return res;
	}

	public boolean isGoal(ArtResource resource)
	{
		return goal!=null && resource==this.goal;	
	}
	
	public boolean isNoGoal()
	{
		return goal==null;
	}
	
	public boolean isSelected(ArtResource resource)
	{
		return resource!=null && resource==this.selected;	
	}

}
