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

package gwap.action;

import gwap.model.GameRound;
import gwap.model.Person;
import gwap.model.TagRating;
import gwap.model.action.Action;
import gwap.model.action.Tagging;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

@Name("actionBean")
@Scope(ScopeType.PAGE)
public class ActionBean implements Serializable {
	//@In private GameSessionBeanNew gameSessionBeanNew;
	@In private EntityManager entityManager;
	@In private Person person;
	
	@Logger Log log;
	
//	private final double TAG_THRESHOLD=4.0;

	
	public void performAction(Action action, GameRound gameRound)
	{	
		gameRound.getActions().add(action);
		action.setGameRound(gameRound);
		
		
		action.setCreated(new Date());
		action.setPerson(person);

		log.info("Adding action #0 to GameRound #1", action, gameRound);

		entityManager.persist(action);
	}
	
	private TagRating findOrCreateTagRating(Tagging t)
	{
		Query query = entityManager.createNamedQuery("tagRating.byResourceAndTag");
		query.setParameter("resid", t.getResource().getId());
		query.setParameter("tag", t.getTag());

		TagRating tagRating=null;
		try {
			tagRating = (TagRating) query.getSingleResult();
		} catch (NonUniqueResultException e) {
			log.error("The tagRating #0, #1 is not unique", t.getResource(), t.getTag());								
			tagRating = (TagRating)query.getResultList().get(0);
		} catch(NoResultException e) {			
		} catch(IndexOutOfBoundsException e) {
		} finally {
		}
		
		if (tagRating==null)
		{
			tagRating=new TagRating();
			tagRating.setResource(t.getResource());
			tagRating.setTag(t.getTag());
			entityManager.persist(tagRating);			
		}		
		return tagRating;
		
	}
	
	public void updateRatings(List<Tagging> taggings, double delta)
	{
		//EntityTransaction tx=entityManager.getTransaction();
		
		Query query=entityManager.createNamedQuery("tagRating.addRating");
		
		int i=0;
		double s=taggings.size();
		s=(s+1)*s/2.0;
		
		for (Tagging t : taggings)
		{
			TagRating r=findOrCreateTagRating(t);
			
			if (r!=null)
			{
				//1+2+3+4+5+6.+i=(i+1)*i/2 = s (see above)
				//Weight for tags: Newer tags are more important
				double weight=0.5+(double)(i+1)/s*0.5;	//Weight between 0.5 and 1.0
				query.setParameter("id", r.getId());
				query.setParameter("rating", delta*weight);						
				try
				{
					query.executeUpdate();
				}
				catch (Exception e)
				{
				}
			}
			
/*			boolean error=false;
			//Create Tag for updated TagRating
			try
			{	
				Query tagQuery=entityManager.createNamedQuery("tagRating.byThresholdAndId");
				tagQuery.setParameter("thresh", TAG_THRESHOLD);
				tagQuery.setParameter("id", d.getTagRating().getId());
				TagRating tr=(TagRating)tagQuery.getSingleResult();

				Tagging tg=new Tagging();
				tg.setResource(tr.getResource());
				tg.setTag(tr.getTag());
				
				tg.setGameRound(gameSessionBeanNew.getGameRound());
				tg.setCreated(new Date());
				tg.setPerson(null);

				log.info("Creating Tag for "+d.getTagRating().getTag().getName()+" on resource "+d.getTagRating().getResource()+".");

				entityManager.persist(tg);
			}
			catch(NoResultException e)
			{
				log.info("Tag for "+d.getTagRating().getTag().getName()+" on resource "+d.getTagRating().getResource()+" already exists.");
			}
			catch(Exception e)
			{
				String m=e.getMessage();
			}*/
	
			i++;				
		}
		
	
	}
}
