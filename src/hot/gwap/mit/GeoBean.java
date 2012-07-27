/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.mit;

import gwap.model.action.Bet;
import gwap.model.resource.GeoPoint;
import gwap.model.resource.Location;
import gwap.model.resource.Location.LocationType;
import gwap.model.resource.LocationGeoPoint;
import gwap.model.resource.LocationHierarchy;
import gwap.model.resource.Statement;
import gwap.wrapper.LocationPercentage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

/**
 * Handles processing of geo data for display in the user interface
 * 
 * @author Fabian Kneißl
 */
@Name("geoBean")
@Scope(ScopeType.SESSION)
@Path("/geodata")
public class GeoBean implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Logger                  private Log log;
	@In
	private EntityManager entityManager;
	@In
	private PokerScoring mitPokerScoring;



	private List<Location> locations;
	private List<SelectItem> locationsAsSelectItems;

	private EntityManager getEntityManager() {
		if (entityManager == null || !entityManager.isOpen())
			entityManager = (EntityManager) Component.getInstance("entityManager");
		return entityManager;
	}
	
	@GET
	@Path("/markers/{hierarchyName}")
	public String getMarkers(@PathParam("hierarchyName") String hierarchyName) {
		return getMarkersInternal(null, hierarchyName, false);
	}
	@GET
	@Path("/markers/{hierarchyName}/{parentId}")
	public String getMarkers(@PathParam("parentId") Long parentId, @PathParam("hierarchyName") String hierarchyName) {
		return getMarkersInternal(parentId, hierarchyName, false);
	}
	@GET
	@Path("/markers-extended-title/{hierarchyName}")
	public String getMarkersExtendedTitle(@PathParam("hierarchyName") String hierarchyName) {
		return getMarkersInternal(null, hierarchyName, true);
	}
	@GET
	@Path("/markers-extended-title/{hierarchyName}/{parentId}")
	public String getMarkersExtendedTitle(@PathParam("parentId") Long parentId, @PathParam("hierarchyName") String hierarchyName) {
		return getMarkersInternal(parentId, hierarchyName, true);
	}
	
	@GET
	@Path("/percentagesbybet/{betId}")
	public String getPercentagesByBet(@PathParam("betId") Long betId){
		StringBuffer sb = new StringBuffer("{ \"markers\" : [");
		GeoPoint min = new GeoPoint(Float.MAX_VALUE, Float.MAX_VALUE);
		GeoPoint max = new GeoPoint(Float.MIN_VALUE, Float.MIN_VALUE);
		Bet bet=  getEntityManager().find(Bet.class, betId);
		List<LocationPercentage> lp = mitPokerScoring.getStatementPercentages(bet.getResource());

		Collections.sort(lp, new Comparator<LocationPercentage>() {
			@Override
			public int compare(LocationPercentage lp1, LocationPercentage lp2) {
				return lp1.getLocation().getType().getLevel() - lp2.getLocation().getType().getLevel();
			}
		});
		
		for(int i = 0; i<lp.size(); i++){
			boolean highlightLocation = lp.get(i).getLocation().getId().equals(bet.getLocation().getId());
			googleMapsLocation(sb, lp.get(i).getLocation(), min, max, false, lp.get(i).getPercentage(), highlightLocation);
		}
		
		sb.append("],\n");
		sb.append(" \"bounds\" : new google.maps.LatLngBounds("+geoPointToLatLng(min)+","+geoPointToLatLng(max)+")");
		sb.append("\n}");
		return sb.toString();
	}
	
	
	@GET
	@Path("/percentages/{statementId}")
	public String getPercentages(@PathParam("statementId") Long statementId){
		StringBuffer sb = new StringBuffer("{ \"markers\" : [");
		GeoPoint min = new GeoPoint(Float.MAX_VALUE, Float.MAX_VALUE);
		GeoPoint max = new GeoPoint(Float.MIN_VALUE, Float.MIN_VALUE);
		
		Statement statement =  getEntityManager().find(Statement.class, statementId);
		List<LocationPercentage> lp = mitPokerScoring.getStatementPercentages(statement);
		
		for(int i = 0; i<lp.size(); i++){
			googleMapsLocation(sb, lp.get(i).getLocation(), min, max, false, lp.get(i).getPercentage(), false);
		}
		sb.append("],\n");
		sb.append(" \"bounds\" : new google.maps.LatLngBounds("+geoPointToLatLng(min)+","+geoPointToLatLng(max)+")");
		sb.append("\n}");
		return sb.toString();
	}
	
	private String getMarkersInternal(Long parentId, String hierarchyName, boolean extendedTitle) {
		log.info("Get locations for hierarchy #0 and parentId #1", hierarchyName, parentId);
		boolean bottomLevel = false;
		try {
			Query q;
			if (parentId == null) {
				q = getEntityManager().createNamedQuery("location.topLevelByHierarchyName");
			} else {
				q = getEntityManager().createNamedQuery("location.containedIn");
				q.setParameter("id", parentId);
			}
			q.setParameter("hierarchyName", hierarchyName);
			locations = q.getResultList();
			if (locations.size() == 1)
				return getMarkersInternal(locations.get(0).getId(), hierarchyName, extendedTitle);
			// Special case if we are at the bottom level
			else if (locations.size() == 0)
				bottomLevel = true;
		} catch (Exception e) {
			log.error("Could not get locations", e);
			locations = null;
		}
		Location parentLocation = null;
		if (parentId != null) {
			parentLocation = getEntityManager().find(Location.class, parentId);
		}
		StringBuffer sb = new StringBuffer("{ \"markers\" : [");
		GeoPoint min = new GeoPoint(Float.MAX_VALUE, Float.MAX_VALUE);
		GeoPoint max = new GeoPoint(Float.MIN_VALUE, Float.MIN_VALUE);
		
		if (!bottomLevel) {
			for (Location location : locations) {
				googleMapsLocation(sb, location, min, max, extendedTitle, null, false);
			}

			HashSet<Location> neighborLocations = new HashSet<Location>();
			for (Location location : locations) {
				List<Location> neighborList = location.getNeighbors();
				for(Location locationTwo : neighborList)
					neighborLocations.add(locationTwo);			
			}
			for(Location neighborLocation : neighborLocations){
				if(!locations.contains(neighborLocation))
					googleMapsLocation(sb, neighborLocation, null, null, extendedTitle, null, false);
			}
			
		} else
			googleMapsLocation(sb, parentLocation, min, max, extendedTitle, null, false);
		sb.append("],\n");
		sb.append(" \"bounds\" : new google.maps.LatLngBounds("+geoPointToLatLng(min)+","+geoPointToLatLng(max)+")");
		if (parentLocation != null && parentLocation.getGeoRepresentation().size() > 0) {
			sb.append(",\n \"parentMarker\" : ");
			googleMapsLocation(sb, parentLocation, min, max, extendedTitle, null, false);
		}
		sb.append("\n}");
		log.info("finished");
		return sb.toString();
	}
	
	private void googleMapsLocation(StringBuffer sb, Location location, GeoPoint min, GeoPoint max, boolean extendedTitle, Double percentage, boolean highlightLocation) {
		if (location == null)
			return;
		if(percentage != null && location.getType().equals(LocationType.MUNICIPALITY)) {
			Location newLocation = nextHigherLocation(location);
			if (newLocation == null) {
				log.warn("Could not find nextLevelFromMunicipality for location #0", location);
				return;
			}
			location = newLocation;
		}
		String locationName = location.getName();
		if (extendedTitle)
			locationName = getExtendedName(location);
		if (location.getGeoRepresentation().size() == 1) {
			// Point
			GeoPoint geoPoint = location.getGeoRepresentation().get(0).getGeoPoint();
			sb.append("new google.maps.Marker({ id : "+location.getId()+", title: \""+locationName+"\", map: gmap, " +
					"position: new google.maps.LatLng("+geoPoint.getLatitude()+","+geoPoint.getLongitude()+"), " +
					"icon : map.markerImage, shadow : map.shadowImage}),");
			updateBounds(geoPoint, min, max);
		} else if (location.getGeoRepresentation().size() > 0) {
			// Polygon
			sb.append("new google.maps.Polygon({ id : "+location.getId()+", title : \"");
			sb.append(locationName);
			sb.append("\", neighbors : [");
			//Neighbors
			List<Location> neighborList = location.getNeighbors();

			for(int i = 0; i<neighborList.size(); i++){
				if(i != 0)
					sb.append(", " + neighborList.get(i).getId());
				else
					sb.append(neighborList.get(i).getId());
			}
			//Bets for visualization
			sb.append("],\n");
			if(percentage != null){
				sb.append("percentage : ");
				double aux = percentage;
				sb.append((int)aux);
				sb.append(",\n");
			}		
			sb.append(" paths : [");
			
			StringBuffer polygon = new StringBuffer("[");
			GeoPoint beginPoint = new GeoPoint();
			for (LocationGeoPoint point : location.getGeoRepresentation()) {
				updateBounds(point.getGeoPoint(), min, max);
				polygon.append(geoPointToLatLng(point.getGeoPoint()));
				polygon.append(",");
				if (beginPoint.getLatitude() == null || beginPoint.getLongitude() == null) {
					beginPoint = point.getGeoPoint();
				} else if (beginPoint.getLatitude().equals(point.getGeoPoint().getLatitude()) && 
						beginPoint.getLongitude().equals(point.getGeoPoint().getLongitude())) {
					polygon.append("],");
					sb.append(polygon);
					polygon = new StringBuffer("[");
					beginPoint = new GeoPoint();
				}
			}
			if(percentage == null){
				if(highlightLocation)
					sb.append("], strokeColor : \"#FF0000\", strokeOpacity : 1.0, strokeWeight : 2, fillColor : \"#0000FF\", fillOpacity : 0.05, map : gmap}), \n");
				else
					sb.append("], strokeColor : \"#3333FF\", strokeOpacity : 1.0, strokeWeight : 2, fillColor : \"#0000FF\", fillOpacity : 0.05, map : gmap}), \n");
			}else{
				double calcFillOpacity;
				if(percentage <= 10.0)
					calcFillOpacity =  0.3;
				else if(percentage <= 20.0)
					calcFillOpacity =  0.4;
				else if(percentage <= 30.0)
					calcFillOpacity =  0.5;
				else if(percentage <= 40.0)
					calcFillOpacity = 0.6;
				else if(percentage <= 50.0)
					calcFillOpacity = 0.7;
				else
					calcFillOpacity = 0.8;
				
				if(highlightLocation)
					sb.append("], strokeColor : \"#58D3F7\", strokeOpacity: 1.0, strokeWeight : 3, fillColor : \"#0000FF\", fillOpacity : " + calcFillOpacity + ", map : gmap,}), \n");
				else
					sb.append("], strokeColor : \"#3333FF\", strokeOpacity : 0.0, strokeWeight : 2, fillColor : \"#0000FF\", fillOpacity :" + calcFillOpacity + ", map : gmap,}), \n");
			}
		}
	}
	
	private Location nextHigherLocation(Location location) {
		Location nextHigherLocation = null;
		try {
			LocationHierarchy locationHierarchy = (LocationHierarchy) entityManager
				.createNamedQuery("locationHierarchy.nextLevelFromType")
				.setParameter("sublocationId", location.getId())
				.setParameter("locationType", LocationType.PROVINCE)
				.getSingleResult();
			nextHigherLocation = locationHierarchy.getLocation();
		} catch (NoResultException e) {
			try {
				LocationHierarchy locationHierarchy = (LocationHierarchy) entityManager
						.createNamedQuery("locationHierarchy.nextLevelFromType")
						.setParameter("sublocationId", location.getId())
						.setParameter("locationType", LocationType.CANTON)
						.getSingleResult();
				nextHigherLocation = locationHierarchy.getLocation();
			} catch (NoResultException e2) {
			}
		}
		return nextHigherLocation;
	}
	
	private String geoPointToLatLng(GeoPoint geoPoint) {
		return "new google.maps.LatLng(" + geoPoint.getLatitude() + "," + geoPoint.getLongitude() +")";
	}
	
	private void updateBounds(GeoPoint point, GeoPoint min, GeoPoint max) {
		if (min != null && max != null) {
			min.setLatitude( Math.min(min.getLatitude(),  point.getLatitude()));
			min.setLongitude(Math.min(min.getLongitude(), point.getLongitude()));
			max.setLatitude( Math.max(max.getLatitude(),  point.getLatitude()));
			max.setLongitude(Math.max(max.getLongitude(), point.getLongitude()));
		}
	}
	
	public String getExtendedName(Location location) {
		try {
			Location region = (Location) entityManager.createNamedQuery("location.regionFromMunicipality")
					.setParameter("id", location.getId()).setMaxResults(1).getSingleResult();
			location.setExtendedName(location.getName() + " (" + region.getName() + ")");
		} catch (NoResultException e) {
			location.setExtendedName(location.getName());
		}
		return location.getExtendedName();
	}
	
	public List<SelectItem> getLocationsAsSelectItems() {
		if (locationsAsSelectItems == null) {
			Query q = getEntityManager().createNamedQuery("location.orderedByName");
			List<Location> locations = q.getResultList();
			locationsAsSelectItems = new ArrayList<SelectItem>();
			for (Location location : locations) {
				SelectItem item = new SelectItem(location.getId(), location.getExtendedName());
				locationsAsSelectItems.add(item);
			}
		}
		return locationsAsSelectItems;
	}
	
	public List<Location> getLocationsByApproximateName(Object search) {
		if (search instanceof String) {
			String startOfName = (String)search;
			startOfName = startOfName.replaceAll("%", "");
			List<Location> locations = getEntityManager()
					.createNamedQuery("location.likeNameDefinedTypesOnly")
					.setParameter("name", startOfName + "%")
					.getResultList();
			List<Location> sorted = new ArrayList<Location>();
			int i = 0;
			for (Location l : locations) {
				if (l.getName().equalsIgnoreCase(startOfName))
					sorted.add(i++, l); // add it to the front
				else
					sorted.add(l);
				if (sorted.size() >= 20)
					break;
			}
			return sorted;
		}
		return null;
	}
	
	public void doNothing() {
		
	}
}
