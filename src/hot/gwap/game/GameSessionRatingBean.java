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

package gwap.game;

import gwap.model.GameSession;
import gwap.model.GameSessionRating;
import gwap.model.Person;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

@Name("gameSessionRatingBean")
@Scope(ScopeType.CONVERSATION)
public class GameSessionRatingBean implements Serializable {
	@Logger				private Log log;
	@In					private EntityManager entityManager;
		
	@In					private GameSession gameSession;
						
	@In(create=true)	private Person person;
	
	@In(create=true) @Out
	private GameSessionRating gameSessionRating;
	
	private static final long MAXRATING=5;
	
	
	public long getRating()
	{
		return gameSessionRating.getRating();		
	}
	
	public List<Boolean> getRatingArray()
	{
		long rating=gameSessionRating.getRating();
		List<Boolean> result=new ArrayList<Boolean>((int)MAXRATING);
		for (int i=0;i<MAXRATING;i++)
		{
			result.add(i<rating);			
		}
		return result;			
	}
		
	public void setRating(long rating)
	{		 
		gameSessionRating.setRating(rating);
		gameSessionRating.setPerson(person);
		gameSessionRating.setGameSession(gameSession);
		if (gameSessionRating.getId()!=null)
			gameSessionRating=entityManager.merge(gameSessionRating);
		else
			entityManager.persist(gameSessionRating);
		log.info("Person #0 rated GameSession #1 #2 (#3)", person, gameSession, rating, gameSessionRating.getId());
	}

	public long getMaxRating() {
		return MAXRATING;
	}
}
