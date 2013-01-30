/*
 * This file is part of gwap
 *
 * Copyright (C) 2013
 * Project play4science
 * Lehr- und Forschungseinheit für Programmier- und Modellierungssprachen
 * Ludwig-Maximilians-Universität München
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package gwap.game.memory;

import gwap.model.GameRound;
import gwap.model.Tag;
import gwap.model.action.Tagging;
import gwap.model.resource.ArtResource;
import gwap.model.resource.Resource;
import gwap.tools.DBLimiter;
import gwap.tools.Timer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.log.Log;

@Name("gwapGameMemoryReplayBean")
@Scope(ScopeType.CONVERSATION)
public class ReplayBean implements Serializable {

	@Logger Log log;
	@In		EntityManager entityManager;
	@In		Player 			gwapGameMemoryPlayer;
	@In		PlayerMatcher	gwapGameMemoryPlayerMatcher;
	@In		SharedGame		gwapGameMemorySharedGame;
	@In 	LocaleSelector localeSelector;
	@In(create=true)	private DBLimiter dbLimiter;
	
	//Rounds already selected for a forced replay
	private List<Long> forceRounds=new ArrayList<Long>();
	private long forcePerson=0;
	
	private Long replayRoundId;

	private Date replayRoundStart;
	private Date roundStart;
	
	private List<ArtResource> resourcesPlayed;
	private List<Tagging> descriptions;
	
	public Long getReplayRoundId()
	{
		return replayRoundId;
	}
	
	public List<ArtResource> updatePreviousRound(long count, long forceId)
	{
		//Get a random round
		try {
			
			if (forceId!=0)
			{	
				
				Query query;
				if (forceRounds.isEmpty())
				{
					query = entityManager.createNamedQuery("gameRound.FromSession");
					query.setParameter("sessionid", forceId);					
				}
				else
				{	
					query = entityManager.createNamedQuery("gameRound.FromSessionNotIdList");
					query.setParameter("sessionid", forceId);
					query.setParameter("limitlist", forceRounds);
					query.setParameter("personid", forcePerson);
				}
				List<GameRound> rounds=query.getResultList();
				if (rounds.isEmpty())
					replayRoundId=0L;
				else
				{		
					GameRound round=rounds.get(0);
					replayRoundId=round.getId();
					
					forcePerson=round.getPerson().getId();
					forceRounds.add(replayRoundId);					
				}				
			}
			
			if (forceId==0 || replayRoundId==0)
			{			
				Query lquery = entityManager.createNamedQuery("gameRound.randomIdByGameTypeIdAndNotPersonId");
				lquery.setParameter("personid", gwapGameMemoryPlayer.getPerson().getId());			
				lquery.setParameter("gametypeid", gwapGameMemorySharedGame.getGameType().getId());
				
				Query query = entityManager.createNamedQuery("gameRound.AtLeastTaggingsLimit");
				query.setParameter("limit", 5L);
				query.setParameter("lang", localeSelector.getLanguage());
				query.setMaxResults(100);
				
				dbLimiter.PrepareLimitedQuery(query, lquery);
				
				Timer t=new Timer();
				
				List<Long> res=query.getResultList();
				if (res.size()==0)
				{
					log.info("No previous rounds found");
					return null;
				}
				log.info("gameRound.AtLeastTaggingsLimit query time: #0", t.timePassed());
	
				replayRoundId=res.get(new Random().nextInt(res.size()));
			}

			
			Query query = entityManager.createNamedQuery("artResource.resourcesByGameRoundId");
			query.setParameter("id", replayRoundId);
						
			List<ArtResource> result=query.getResultList();
			if (result.size()!=count)
			{
				log.info("GameRound #0 has invalid number of Resources: #1", replayRoundId, result.size());
				replayRoundId=0L;
				return null;
			}

			resourcesPlayed=getPlayedResources();

			return result;
		} catch(Exception e) {
			log.info("No previous rounds found: "+e.getMessage());
			return null;
		}
		
		/*
		try {
			Query query = entityManager.createNamedQuery("gameRound.randomGameRoundByResourceCountAndLimit");
			query.setParameter("count", count);
			query.setParameter("limit", 2L);
			query.setParameter("personid", gwapGameMemoryPlayer.getPerson().getId());
			query.setParameter("gametypeid", gwapGameMemoryPlayerMatcher.getGameType().getId());
			query.setParameter("lang", localeSelector.getLanguage());
			query.setMaxResults(100);
			
			List<Long> res=query.getResultList();
			if (res.size()==0)
			{
				log.info("No previous rounds found");
				return null;
			}
					
			//Randomly replay one of the last 100 gamerounds
			replayRoundId=res.get(new Random().nextInt(res.size()));

			resourcesPlayed=getPlayedResources();
			
			query = entityManager.createNamedQuery("artResource.resourcesByGameRoundId");
			query.setParameter("id", replayRoundId);
						
			return query.getResultList();
		} catch(Exception e) {
			log.info("No previous rounds found: "+e.getMessage());
			return null;
		}*/	
	}
	
	
	public List<ArtResource> getResourcesPlayed() {
		return resourcesPlayed;
	}

	private List<ArtResource> getPlayedResources()
	{
		Query query = entityManager.createNamedQuery("artResource.playedResourceIdsByGameRoundId");			
		query.setParameter("id", replayRoundId);
		List<Long> results=query.getResultList();
		
		if (results.size()>0)
		{
			ArrayList<ArtResource> resourcesPlayed=new ArrayList<ArtResource>();
				
			//This a bit of a hack, because the previous query cannot return actual objects
			//(see https://forum.hibernate.org/viewtopic.php?p=2273602 )
			for(Long result : results)									
			{
				//query.setParameter("id", (Long)result);
				//resourcesPlayed.add((ArtResource)query.getSingleResult());
				resourcesPlayed.add(entityManager.find(ArtResource.class, result));
			}
			
			return resourcesPlayed;
		}
		else
			return null;
	}
	
	public ArtResource getNextResource()
	{
		if (resourcesPlayed!=null && resourcesPlayed.size()>0)
			return resourcesPlayed.remove(0);
		else
			return null;	
	}
	
	public Set<Tag> allTagsForResource(Resource r)
	{
		return allTagsForResource(r, 0);
		
	}
	
	public Set<Tag> allTagsForResource(Resource r, int limit)
	{
		Set<Tag> resultSet=new HashSet<Tag>();
		
		Query query = entityManager.createNamedQuery("tagging.randomTagByResourceAndLanguage");
		query.setParameter("resource", r);
		query.setParameter("language", localeSelector.getLanguage());
		if (limit>0)
			query.setMaxResults(limit);
		
		List<Tag> res=query.getResultList();
		resultSet.addAll(res);
		
		return resultSet;
	}	
	
	public void updateDescriptions(Resource r)
	{	
		log.info("Updating descriptions for resource #0", r.getId());
		try
		{
			Query query = entityManager.createNamedQuery("tagging.taggingsByGameAndResourceId");
			query.setParameter("resid", r.getId());
			query.setParameter("gameid", replayRoundId);
			
			
			descriptions=query.getResultList();
		}
		catch (Exception e)
		{
			descriptions=new ArrayList<Tagging>();
		}		
		
		if (descriptions.size()>5)
		{
			replayRoundStart=descriptions.get(0).getCreated();
			log.info("updateDescriptions() for resource "+r.getId()+" found: "+descriptions.size());
		}
		else
		{
			log.info("updateDescriptions() for resource failed, not enough descriptions found (only "+descriptions.size()+"). Adding random taggings");
		
			
			
			
			/*Query query = entityManager.createNamedQuery("tagging.randomTagByResourceAndLanguage");
			query.setParameter("resource", r);
			query.setParameter("language", localeSelector.getLanguage());
			query.setMaxResults(15);*/
						
			Random random = new Random();
			
			Set<Tag> tags=allTagsForResource(r, 15);
			
			//Make sure no tag appears twice
			Set<Tag> descTags=new HashSet<Tag>();
			for (Tagging t : descriptions)
			{
				descTags.add(t.getTag());
				log.info("Already existing: #0 id: #1", t.getTag().getName(), t.getTag().getId());
			}			
			
			tags.removeAll(descTags);
			
			List<Tag> tags2=new ArrayList<Tag>();
			tags2.addAll(tags);
			Collections.shuffle(tags2);
			
			
			Date date;
			if (descriptions.size()>0)
			{
				replayRoundStart=descriptions.get(0).getCreated();
				//Get last item's creation date and add new tags after it
				date=descriptions.get(descriptions.size()-1).getCreated();
			}
			else
			{
				replayRoundStart=new Date();
				date=replayRoundStart;
			}
			
			boolean firstTag=true;
			for (Tag t : tags2)
			{
				date=new Date(date.getTime());
				
				if (firstTag)	//Send the first tag faster
					date.setTime(date.getTime()+500+t.getName().length()*(100+random.nextInt(150))+random.nextInt(1000));
				else
					date.setTime(date.getTime()+1000+t.getName().length()*(200+random.nextInt(300))+random.nextInt(2000));
				firstTag=false;
				
				Tagging tg=new Tagging();
				tg.setTag(t);
				tg.setResource(r);
				tg.setCreated(date);
				
				
				
				descriptions.add(tg);
			}		
			log.info("Added "+descriptions.size()+" random tags.");
			
		}		
		roundStart=new Date();
		log.info("Added descriptions:");
		for (Tagging t:descriptions)
		{
			log.info("#0", t.getTag());
			
		}
		
	}
	
	public List<Tagging> getDescriptions()
	{
		return getDescriptions(0);		
	}
	
	public List<Tagging> getDescriptions(int limit)
	{	
		if (descriptions!=null)
		{
			List<Tagging> res=new ArrayList<Tagging>();
			if (descriptions.size()>0)
			{
				Tagging d=descriptions.get(0);
				int i=0;

				while (d.getCreated().getTime()-replayRoundStart.getTime() <= new Date().getTime()-roundStart.getTime() &&
						(limit==0 || i<limit))
				{
					res.add(d);
					descriptions.remove(0);
					if (descriptions.size()>0)
						d=descriptions.get(0);
					else
						break;
					i++;
				}				
			}
			
			log.info("getDescriptions() found: "+res.size()+" remaining: "+descriptions.size());
			return res;
		}
		else
			return new ArrayList<Tagging>();	
	}	
}
