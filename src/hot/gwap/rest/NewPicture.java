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

import gwap.model.action.VirtualTagging;
import gwap.model.action.VirtualTaggingType;
import gwap.model.resource.ArtResource;

import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
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

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * For pictures that are added by users on their own.
 * 
 * @author maders, wieser, kneissl
 */
@Path("/newpicture")
@Name("restNewPicture")
public class NewPicture extends UserPicture {
	 
	private static final long serialVersionUID = 1L;
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Transactional
	public Response createNewpicture(String string) {
		JSONObject payload = parse(string);
		ArtResource artResource = createPicture(payload);

		artResource.setOrigin(ArtResource.ORIGIN_APP_USER);
		artResource.getShownLocation().setName(payload.get("name").toString());
		
		VirtualTagging virtualTagging = new VirtualTagging();
		virtualTagging.setResource(artResource);
		entityManager.persist(virtualTagging);
		
		VirtualTaggingType virtualTaggingType = entityManager.find(VirtualTaggingType.class, Long.parseLong(payload.get("topic").toString()));
		virtualTagging.getVirtualTaggingTypes().add(virtualTaggingType);
		
		entityManager.flush();
		
		log.info("Created picture: #0", artResource.getId());
	
		return Response.status(Response.Status.CREATED).build();
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Transactional
	@Path("/{id:[0-9][0-9]*}")
	public Response rateNewPicture(@PathParam("id") String idString, String payloadString) {
		return ratePicture(idString, payloadString);
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRandomNewPictures(@QueryParam("count") String count, @QueryParam("userid") String deviceId) {
		List<ArtResource> artResources = getRandomPictures(count, deviceId, ArtResource.ORIGIN_APP_CRIMESCENE);
		JSONArray jsonArray = new JSONArray();
		for(ArtResource artResource: artResources) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", artResource.getId());
//			jsonObject.put("latitude", artResource.getShownLocation().getGeoRepresentation().get(0).getGeoPoint().getLatitude());
//			jsonObject.put("longitude", artResource.getShownLocation().getGeoRepresentation().get(0).getGeoPoint().getLongitude());
			jsonObject.put("name", artResource.getShownLocation().getName());
			jsonObject.put("likes", 0);
			jsonObject.put("dislikes", 0);
			jsonObject.put("imageData", "9083W95NSPRGVNASOIÖTUNOIARÖUT5IOW4ÖU");
			jsonArray.add(jsonObject);
		}
		
		return Response.ok(jsonArray.toString(), MediaType.APPLICATION_JSON).build();
	}
	
}
