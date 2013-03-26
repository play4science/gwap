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

import gwap.model.ArtResourceRating;
import gwap.model.Person;
import gwap.model.resource.ArtResource;
import gwap.model.resource.GeoPoint;
import gwap.model.resource.Location;
import gwap.model.resource.Location.LocationType;
import gwap.model.resource.LocationGeoPoint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * For pictures that are added in a mobile app game.
 * 
 * @author maders, wieser, kneissl
 */
@Path("/userpicture")
@Name("restUserPicture")
public class UserPicture extends RestService {
	
	@In
	protected EntityManager entityManager;
	
	private static final long serialVersionUID = 1L;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Transactional
	public Response createUserPicture(String string) {
		JSONObject payload = parse(string);
		ArtResource artResource = createPicture(payload);
		
		artResource.setOrigin(ArtResource.ORIGIN_APP_CRIMESCENE);
		ArtResource isVersionOf = entityManager.find(ArtResource.class, Long.parseLong(payload.get("artigoid").toString()));
		artResource.setIsVersionOf(isVersionOf);
		
		//TODO Bild anlegen
		
		log.info("Created picture: #0", artResource.getId());
		return Response.status(Response.Status.CREATED).build();
	}
	
	protected ArtResource createPicture(JSONObject payload) {
		ArtResource artResource = new ArtResource();
		
		Query query = entityManager.createNamedQuery("person.byDeviceId");
		query.setParameter("deviceId", payload.get("userid").toString());
		Person person = (Person) query.getSingleResult();
		artResource.setArtist(person);
		
		Calendar now = GregorianCalendar.getInstance();
		artResource.setDateCreated(new SimpleDateFormat("dd.MM.yyyy").format(now.getTime()));
		
		artResource.setSkip(true); // should not show up for artigo tagging

		Location location = new Location();
		location.setType(LocationType.APP);
		
		GeoPoint geoPoint = new GeoPoint();
		geoPoint.setLatitude(Float.parseFloat(payload.get("latitude").toString()));
		geoPoint.setLongitude(Float.parseFloat(payload.get("longitude").toString()));
		entityManager.persist(geoPoint);
		
		entityManager.persist(location);
		entityManager.flush();
		LocationGeoPoint locationGeoPoint = new LocationGeoPoint();
		locationGeoPoint.setGeoPoint(geoPoint);
		locationGeoPoint.setLocation(location);
		entityManager.persist(locationGeoPoint);
		entityManager.flush();
		location.getGeoRepresentation().add(locationGeoPoint);
		
		artResource.setShownLocation(location);
		entityManager.persist(artResource);
		
		entityManager.flush();
		return artResource;
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Transactional
	@Path("/{id:[0-9][0-9]*}")
	public Response ratePicture(@PathParam("id") String idString, String payloadString) {
		JSONObject jsonObject = parse(payloadString);
		
		Query query = entityManager.createNamedQuery("person.byDeviceId");
		query.setParameter("deviceId", jsonObject.get("userid").toString());
		Person person = (Person) query.getSingleResult();
		
		ArtResourceRating artResourceRating = new ArtResourceRating();
		artResourceRating.setPerson(person);
		
		if (jsonObject.containsKey("likes")) 
			artResourceRating.setRating(1L);
		else
			artResourceRating.setRating(-1L);

		entityManager.persist(artResourceRating);
		ArtResource artResource = entityManager.find(ArtResource.class, Long.parseLong(idString));
		artResource.getRatings().add(artResourceRating);
		artResourceRating.setResource(artResource);
		entityManager.flush();
		log.info("Added ArtResourceRating #0", artResourceRating.getId());

		log.info("Updated UserPicture #0", artResource.getId());
		return Response.ok().build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRandomUserpictures(@QueryParam("count") String count, @QueryParam("userid") String deviceId) {
		List<ArtResource> artResources = getRandomPictures(count, deviceId, ArtResource.ORIGIN_APP_USER);
		JSONArray jsonArray = new JSONArray();
		for(ArtResource artResource: artResources) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", artResource.getId());
//			jsonObject.put("latitude", artResource.getShownLocation().getGeoRepresentation().get(0).getGeoPoint().getLatitude());
//			jsonObject.put("longitude", artResource.getShownLocation().getGeoRepresentation().get(0).getGeoPoint().getLongitude());
			jsonObject.put("likes", 0);
			jsonObject.put("dislikes", 0);
			jsonObject.put("imageData_user", "3980452309582");
			jsonObject.put("imageData_artigo", "s04930qw0ßsd9r0ßas");
			jsonArray.add(jsonObject);
		}
		return Response.ok(jsonArray.toString(), MediaType.APPLICATION_JSON).build();
	}

	protected List<ArtResource> getRandomPictures(String count, String deviceId, String origin) {
		Query query = entityManager.createNamedQuery("artResource.getRandomPictures");
		query.setParameter("deviceId", deviceId);
		query.setParameter("origin", origin);
		query.setMaxResults(Integer.parseInt(count));
		List<ArtResource> artResources = query.getResultList();
		return artResources;
	}

}
