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

package gwap.mit.admin;

import gwap.model.resource.Location;
import gwap.model.resource.LocationHierarchy;
import gwap.model.resource.LocationHierarchy.LocationHierarchyType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.log.Log;

/**
 * @author kneissl
 */
@Name("mitAdminLocationHierarchyCreator")
@Restrict("#{s:hasRole('admin')}")
public class LocationHierarchyCreator implements Serializable {
	
	@In
	private EntityManager entityManager;
	
	@Logger
	private Log log;
	
	private Map<LocationHierarchyType,Integer> insertedLHs;
	private Map<Long,Float> insertedLocationPairs;
	
	private static final String hierarchyName = "mit.scoring";

	private Long MAX_LOCATION_ID;
	
	@SuppressWarnings("unchecked")
	public void recreateHierarchy() {
		int deleteResult = entityManager.createQuery("delete from LocationHierarchy where name = :name")
			.setParameter("name", hierarchyName)
			.executeUpdate();
		entityManager.flush();
		log.info("Deleted #0 rows from LocationHierarchy", deleteResult);
		
		MAX_LOCATION_ID = ((Number) entityManager.createQuery("select max(id) from Location").getSingleResult()).longValue();

		insertedLHs = new HashMap<LocationHierarchyType, Integer>();
		insertedLocationPairs = new HashMap<Long, Float>();
		
		// Insert self and neighbors
		List<Location> allLocations = entityManager.createNamedQuery("location.allButUserDefined").getResultList();
		for (Location l : allLocations) {
			createLH(l, l, LocationHierarchyType.SELF);
			for (Location n : l.getNeighbors()) {
				createLH(l, n, LocationHierarchyType.NEIGHBOR);
			}
		}
		log.info("Inserted #0 locations and #1 neighbors", insertedLHs.get(LocationHierarchyType.SELF), insertedLHs.get(LocationHierarchyType.NEIGHBOR));
		entityManager.flush();
		
		insertFromHierarchy("locationHierarchy.allMIT1stOrder");
		insertFromHierarchy("locationHierarchy.allMIT2ndOrder");
		insertFromHierarchy("locationHierarchy.allMIT3rdOrder");
		insertFromHierarchy("locationHierarchy.allMIT4thOrder");
		log.info("Inserted #0 descendants and #0 ancestors", insertedLHs.get(LocationHierarchyType.DESCENDANT), insertedLHs.get(LocationHierarchyType.ANCESTOR));
		entityManager.flush();
		
		// Insert hierarchical neighbors
		List<Object[]> flattenedNeighbors = entityManager.createQuery(
				"select l1, l2 from Location l1 join l1.neighbors n join n.hierarchies lh join lh.sublocation l2 " +
				"where lh.name=:name and lh.type =:type")
			.setParameter("name", hierarchyName)
			.setParameter("type", LocationHierarchyType.DESCENDANT)
			.getResultList();
		for (Object[] locations : flattenedNeighbors) {
			Location l1 = (Location) locations[0];
			Location l2 = (Location) locations[1];
			createLH(l1, l2, LocationHierarchyType.NEIGHBOR_DESCENDANT);
			createLH(l2, l1, LocationHierarchyType.NEIGHBOR_ANCESTOR);
		}
		log.info("Inserted #0 descendant-neighbors and #0 ancestor-neighbors", insertedLHs.get(LocationHierarchyType.NEIGHBOR_DESCENDANT), insertedLHs.get(LocationHierarchyType.NEIGHBOR_ANCESTOR));
		
		entityManager.flush();
		log.info("Finished");
	}
	
	private void insertFromHierarchy(String query) {
		List<Object[]> locationsList = entityManager.createNamedQuery(query).getResultList();
		for (Object[] locations : locationsList) {
			Location l1 = (Location) locations[0];
			Location l2 = (Location) locations[1];
			createLH(l1, l2, LocationHierarchyType.DESCENDANT);
			createLH(l2, l1, LocationHierarchyType.ANCESTOR);
		}
	}
	
	private void createLH(Location location, Location sublocation, LocationHierarchyType type) {
		Float higherValue = insertedLocationPairs.get(location.getId() + sublocation.getId()*(MAX_LOCATION_ID+1));
		if (higherValue != null) {
			if (higherValue.floatValue() >= type.getCorrelation())
				return;
			else {
				// Delete old LocationHierarchy
				Object old = entityManager.createNamedQuery("locationHierarchy.byLocationsAndName")
					.setParameter("name", hierarchyName)
					.setParameter("location", location)
					.setParameter("sublocation", sublocation)
					.getSingleResult();
				entityManager.remove(old);
			}
		}
		insertedLocationPairs.put(location.getId() + sublocation.getId()*(MAX_LOCATION_ID+1), type.getCorrelation());
		LocationHierarchy create = new LocationHierarchy();
		create.setLocation(location);
		create.setSublocation(sublocation);
		create.setName(hierarchyName);
		create.setType(type);
		create.setCorrelation(type.getCorrelation());
		entityManager.persist(create);
		insertedLHs.put(type, (insertedLHs.get(type) == null ? 0 : insertedLHs.get(type)) + 1);
	}

}
