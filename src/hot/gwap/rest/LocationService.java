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

import gwap.model.action.VirtualTaggingType;
import gwap.model.resource.ArtResource;
import gwap.model.resource.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * @author maders, wieser
 */

@Path("/location")
@Scope(ScopeType.SESSION)
@Name("locationService")
public class LocationService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Logger	private Log log;
	@In     private EntityManager entityManager;
	
	/**
	 * A topic denotes the set of pictues that can be used in a game such as "Munich" or "Baroque" 
	 * 
	 * @param userId
	 * @return
	 */
	@GET
	@Path("/topics") // $HOST/seam/resource/rest/location/topics
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response getTopics(@QueryParam("userid") String userId) {
		
		if (userId == null)
			return Response.status(Response.Status.NOT_ACCEPTABLE).build();
		
		Query query = entityManager.createNamedQuery("virtualTaggingType.all");
		ArrayList<VirtualTaggingType> virtualTaggingTypes = (ArrayList<VirtualTaggingType>) query.getResultList();
				
		JSONArray jsonArray = new JSONArray();
		for (VirtualTaggingType virtualTaggingType : virtualTaggingTypes) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("text", virtualTaggingType.getName());
			jsonObject.put("value", virtualTaggingType.getId());
			jsonObject.put("available", true); 	//TODO: Calculate availability 
			jsonArray.add(jsonObject);
		}
		
		log.info("UserId: #0", userId);
		return Response.ok(jsonArray.toString(), MediaType.APPLICATION_JSON).build();
	}

	/**
	 * Locations a user may visit in the App
	 * @param latitude
	 * @param longitude
	 * @param userId
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response getGameLocations(
			@QueryParam("currentLatitude") String latitude,
			@QueryParam("currentLongitude") String longitude,
			@QueryParam("userid") String userId, 
			@QueryParam("topic") Long topic) {

		if(latitude == null || longitude == null) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		} else {
			Query locationQuery = entityManager.createNamedQuery("artResource.gameLocations");
			locationQuery.setParameter("virtualTaggingTypeId", topic);
			locationQuery.setParameter("userId", userId);
			List<ArtResource> locations = locationQuery.getResultList(); 

			JSONArray gameLocations = new JSONArray();
			JSONObject gameLocationJSON;
			Double min, distance;
			int minIndex;
			ArtResource gameLocation;
			double currentLatitude = Double.parseDouble(latitude);
			double currentLongitude = Double.parseDouble(longitude);

			// Berechnet die Orte, die im Laufe des Spiels vom Spieler besucht werden
			// Dabei wird immer der geographisch nahste Ort zum Spieler, bzw. zum Tatort
			// zuvor berechnet
			for(int i = 0; i < 6; i++) {
				// Das erste Minimum ist der Abstand vom Spieler zur ersten Location
				gameLocation = locations.get(0);
				GeoPoint geoPoint = gameLocation.getShownLocation().getSingleGeoPoint();
				min = getDistance(currentLatitude, currentLongitude, 
						geoPoint.getLatitude(), 
						geoPoint.getLongitude());
				minIndex = 0;
				// Fuer alle ueberigen Locations: Uberpruefen, ob eine dieser Locations naeher als die erste ist.
				for(int j = 1; j < locations.size(); j++) {
					gameLocation = locations.get(j);
					geoPoint = gameLocation.getShownLocation().getSingleGeoPoint();
					distance = getDistance(currentLatitude, currentLongitude,
							geoPoint.getLatitude(), 
							geoPoint.getLongitude());
					if(distance < min) {
						min = distance;
						minIndex = j;
					}
				}
				// Konstruiere das JSON-Objekt des gefundenen Ortes
				gameLocation = locations.get(minIndex);
				gameLocationJSON = new JSONObject();
				gameLocationJSON.put("id", gameLocation.getId());
				gameLocationJSON.put("name", gameLocation.getShownLocation().getName());
				gameLocationJSON.put("latitude", gameLocation.getShownLocation().getSingleGeoPoint().getLatitude());
				gameLocationJSON.put("longitude", gameLocation.getShownLocation().getSingleGeoPoint().getLongitude());
				gameLocationJSON.put("distance", min);
				gameLocationJSON.put("url", gameLocation.getUrl()); // If null, check Outjection in ReouceBean

				// Fuege es der Liste aller Gamelocations hinzu
				gameLocations.add(gameLocationJSON);

				// Der Standort, von dem weitergesucht wird ist der Standort der letzten Gamelocation
				currentLatitude = gameLocation.getShownLocation().getSingleGeoPoint().getLatitude();
				currentLongitude = gameLocation.getShownLocation().getSingleGeoPoint().getLongitude();
				// Die aktuelle Gamelocation kann nicht mehr besucht werden, wird also aus der Liste entfernt
				locations.remove(minIndex);
			}
			return Response.ok(gameLocations.toString(), MediaType.APPLICATION_JSON).build();
		}
	}
	
	
	/**
	 * Berechnet die Entfernung zwischen position und destination in Metern.
	 * @param positionLatitude: Latitude der Ausgangsposition
	 * @param positionLongitude: Longitude der Ausgangsposition
	 * @param destinationLatitude: Latitude des Ziels
	 * @param destinationLongitude: Longitude des Ziels
	 * @return Entfernung in Metern
	 */
	private Double getDistance(double positionLatitude, 
			double positionLongitude, 
			double destinationLatitude, 
			double destinationLongitude) {
		double earthRadius = 6369000;
		double dLat = Math.toRadians(destinationLatitude - positionLatitude);
		double dLng = Math.toRadians(destinationLongitude - positionLongitude);
		double sindLat = Math.sin(dLat / 2);
		double sindLng = Math.sin(dLng / 2);
	    double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2) * Math.cos(Math.toRadians(positionLatitude)) * Math.cos(Math.toRadians(destinationLatitude));
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double distance = earthRadius * c;
	    return distance;
	}
}
