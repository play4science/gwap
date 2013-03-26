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

package gwap.rest;

import gwap.model.Badge;
import gwap.model.Person;
import gwap.model.resource.ArtResource;
import gwap.wrapper.UserStatistics;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

/**
 * @author maders, wieser
 */

@Path("/user")
@Name("user")
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	@In private EntityManager entityManager;
	
	@GET
	@Path("/{id:[A-Za-z0-9][A-Za-z0-9]*}")
	public Response getUser(@PathParam("id") String deviceId) {
		Query query = entityManager.createNamedQuery("person.byDeviceId");
		query.setParameter("deviceId", deviceId);
		Person person = (Person) query.getSingleResult();
		
		String username = person.getExternalUsername();
		
		query = entityManager.createNamedQuery("gameRound.statisticsByPlayer");
		query.setParameter("deviceId", deviceId);
		UserStatistics userStatistics = (UserStatistics) query.getSingleResult();
		
		query = entityManager.createNamedQuery("gameRound.gamesWonByPlayer");
		query.setParameter("deviceId", deviceId);
		Long gamesWonByPlayer = ((Number)query.getSingleResult()).longValue();
		
		query = entityManager.createNamedQuery("artResource.byOriginAndDeviceId");
		query.setParameter("origin", ArtResource.ORIGIN_APP_USER);
		query.setParameter("deviceId", deviceId);
		Long photosTaken = ((Number)query.getSingleResult()).longValue();
		
		query = entityManager.createNamedQuery("artResource.byOriginAndDeviceId");
		query.setParameter("origin", ArtResource.ORIGIN_APP_CRIMESCENE);
		query.setParameter("deviceId", deviceId);
		Long crimescenesTaken = ((Number)query.getSingleResult()).longValue();
		
		query = entityManager.createNamedQuery("badge.byDeviceId");
		query.setParameter("deviceId", deviceId);
		List<Badge> unlockedBadges = query.getResultList();

		query = entityManager.createNamedQuery("badge.all");
		List<Badge> allBadges = query.getResultList();
		
		
		return Response.status(Response.Status.OK).build();
	}
}
