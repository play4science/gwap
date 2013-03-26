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
import gwap.model.action.VirtualTagging;
import gwap.model.action.VirtualTaggingType;
import gwap.model.resource.ArtResource;
import gwap.model.resource.GeoPoint;
import gwap.model.resource.Location;
import gwap.model.resource.Location.LocationType;
import gwap.model.resource.LocationGeoPoint;

import java.io.Serializable;
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
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.log.Log;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * For pictures that are added by users on their own.
 * 
 * @author maders, wieser
 */
 @Path("/newpicture")
 @Name("newPicture")
public class NewPicture implements Serializable {
	 
	private static final long serialVersionUID = 1L;

	@In private EntityManager entityManager;
	
	@Logger
	private Log log;
	
	private JSONParser parser = new JSONParser();
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Transactional
	public Response createNewpicture(String string) {
		JSONObject jsonObject = null;
		try {
			jsonObject = (JSONObject) parser.parse(string);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ArtResource artResource = new ArtResource();
		
		Query query = entityManager.createNamedQuery("person.byDeviceId");
		query.setParameter("deviceId", jsonObject.get("userid").toString());
		Person person = (Person) query.getSingleResult();
		artResource.setArtist(person);
		
		Calendar now = GregorianCalendar.getInstance();
		artResource.setDateCreated(new SimpleDateFormat("dd.MM.yyyy").format(now.getTime()));
		
		artResource.setOrigin(ArtResource.ORIGIN_APP_USER);
		artResource.setSkip(true); // should not show up for artigo tagging

		Location location = new Location();
		location.setType(LocationType.APP);
		location.setName(jsonObject.get("name").toString());
		
		GeoPoint geoPoint = new GeoPoint();
		geoPoint.setLatitude(Float.parseFloat(jsonObject.get("latitude").toString()));
		geoPoint.setLongitude(Float.parseFloat(jsonObject.get("longitude").toString()));
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
	
		VirtualTagging virtualTagging = new VirtualTagging();
		virtualTagging.setResource(artResource);
		
		VirtualTaggingType virtualTaggingType = entityManager.find(VirtualTaggingType.class, Long.parseLong(jsonObject.get("topic").toString()));
		virtualTagging.getVirtualTaggingTypes().add(virtualTaggingType);
		
		entityManager.persist(virtualTagging);
		entityManager.flush();
		
		log.info("Created user picture: #0", artResource.getId());
	
		return Response.status(Response.Status.CREATED).build();
	}
	
	@PUT
	@Path("/{id:[0-9][0-9]*}")
	@Consumes(MediaType.APPLICATION_JSON)
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Transactional
	public Response rateNewPicture(@PathParam("id") String idString, String payloadString) {
		Long id = Long.parseLong(idString);
		JSONObject jsonObject = null;
		try {
			jsonObject = (JSONObject) parser.parse(payloadString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
		ArtResource artResource = entityManager.find(ArtResource.class, id);
		artResource.getRatings().add(artResourceRating);
		artResourceRating.setResource(artResource);
				
		log.info("Updated NewPicture #0", artResource.getId());
		return Response.status(Response.Status.OK).build();
	}
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRandomNewPictures(@QueryParam("count") String stringCount, @QueryParam("userid") String stringUserId) {
		Query query = entityManager.createNamedQuery("artResource.getRandomNewPictures");
		query.setMaxResults(Integer.parseInt(stringCount));
		List<ArtResource> artResources = query.getResultList();
		
		JSONArray jsonArray = new JSONArray();
		for(ArtResource artResource: artResources)  {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", artResource.getId());
			jsonObject.put("latitude", artResource.getShownLocation().getGeoRepresentation().get(0).getGeoPoint().getLatitude());
			jsonObject.put("longitude", artResource.getShownLocation().getGeoRepresentation().get(0).getGeoPoint().getLongitude());
			jsonObject.put("name", artResource.getShownLocation().getName());
			jsonObject.put("likes", 0);
			jsonObject.put("dislikes", 0);
			jsonObject.put("imageData", "9083W95NSPRGVNASOIÖTUNOIARÖUT5IOW4ÖU");
			jsonArray.add(jsonObject);
		}
		
		return Response.ok(jsonArray.toString(), MediaType.APPLICATION_JSON).build();
	}
	
}
