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

package gwap.model.resource;

import gwap.model.action.LocationAssignment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
import org.jboss.seam.annotations.Name;

@NamedQueries({
	@NamedQuery(name="location.allButUserDefined", query="from Location where type != 'USER_DEFINED'", cacheable=true),
	@NamedQuery(name="location.byNameAndType", query="from Location where lower(name) = lower(:name) and type = :type", cacheable=true),
	@NamedQuery(name="location.byType", query="from Location where type = :type", cacheable=true),
	@NamedQuery(name="location.likeName", query="from Location where lower(name) like lower(:name) order by name,type", cacheable=true),
	@NamedQuery(name="location.likeNameDefinedTypesOnly", query="from Location where lower(name) like lower(:name) and type in " +
			"('MUNICIPALITY', 'PROVINCE', 'REGION', 'AREA', 'DIVISION', 'CANTON') " +
			"order by name,type", cacheable=true),
	@NamedQuery(name="location.containedIn", query="select l from Location l join l.hierarchyParents p " +
			"where p.location.id = :id and p.name = :hierarchyName group by l.id, l.name, l.type", cacheable=true),
	@NamedQuery(name="location.topLevelByHierarchyName", query="select l from Location l join l.hierarchyParents p " +
			"where p.name = :hierarchyName and p.location is null group by l.id, l.name, l.type", cacheable=true),
	@NamedQuery(name="location.orderedById", query="from Location l order by id", cacheable=true),
	@NamedQuery(name="location.regionFromMunicipality", query="select p2.location from Location l " +
			"join l.hierarchyParents p1 join p1.location.hierarchyParents p2 where l.id=:id", cacheable=true),
	@NamedQuery(name="location.orderedByName", query="select l from Location l order by l.name", cacheable=true)
})

/**
 * Location specifies a location which has a geo-position. It can be of
 * a certain <code>LocationType</code> and contains other Locations.
 * 
 * @author Fabian Kneißl
 */
@Entity
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Name("location")
public class Location implements Serializable {
	
	/**
	 * Ordered by size from small to big
	 *  
	 * @author Fabian Kneißl
	 */
	public enum LocationType {
		MUNICIPALITY(5), PROVINCE(4), REGION(3), AREA(2), COUNTRY(1), DIVISION(3), CANTON(4), MISCELLANEOUS(9), USER_DEFINED(10);
		private int level;
		private LocationType(int level) {
			this.level = level;
		}
		public int getLevel() {
			return level;
		}
	}

	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue
	private Long id;

	private String name;
	
	@Enumerated(EnumType.STRING)
	private LocationType type;
	
	@OneToMany(mappedBy="location")
	private List<LocationAssignment> locationAssignments = new ArrayList<LocationAssignment>();
	
	@OneToMany(mappedBy="location")
	private List<LocationHierarchy> hierarchies = new ArrayList<LocationHierarchy>();
	
	@OneToMany(mappedBy="sublocation")
	private List<LocationHierarchy> hierarchyParents = new ArrayList<LocationHierarchy>();
	
	@OneToMany(mappedBy="location", cascade=CascadeType.REMOVE)
	@OrderBy
	private List<LocationGeoPoint> geoRepresentation;
	
	@ManyToMany
	@JoinTable(name="locationneighbor", joinColumns=@JoinColumn(name="location_id"), inverseJoinColumns=@JoinColumn(name="neighbor_id"))
	private List<Location> neighbors = new ArrayList<Location>();
	
	@Transient
	private String extendedName;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<LocationAssignment> getLocationAssignments() {
		return locationAssignments;
	}

	public void setLocationAssignments(List<LocationAssignment> locationAssignments) {
		this.locationAssignments = locationAssignments;
	}

	public LocationType getType() {
		return type;
	}

	public void setType(LocationType type) {
		this.type = type;
	}

	public List<LocationGeoPoint> getGeoRepresentation() {
		return geoRepresentation;
	}

	public void setGeoRepresentation(List<LocationGeoPoint> geoRepresentation) {
		this.geoRepresentation = geoRepresentation;
	}

	public List<LocationHierarchy> getHierarchies() {
		return hierarchies;
	}

	public void setHierarchies(List<LocationHierarchy> hierarchies) {
		this.hierarchies = hierarchies;
	}

	public List<LocationHierarchy> getHierarchyParents() {
		return hierarchyParents;
	}

	public void setHierarchyParents(List<LocationHierarchy> hierarchyParents) {
		this.hierarchyParents = hierarchyParents;
	}

	public List<Location> getNeighbors() {
		return neighbors;
	}

	public void setNeighbors(List<Location> neighbors) {
		this.neighbors = neighbors;
	}

	public String getExtendedName() {
		return extendedName;
	}

	public void setExtendedName(String extendedName) {
		this.extendedName = extendedName;
	}

	@Override
	public String toString() {
		return id + "#" + name + "(" + type + ")";
	}
}
