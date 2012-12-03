/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.model.resource;


import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * @author Fabian Kneißl
 */
@NamedQueries({
	@NamedQuery(name="locationHierarchy.allMIT", query="SELECT lh FROM LocationHierarchy lh WHERE lh.name='mit'"),
	@NamedQuery(name="locationHierarchy.allMIT1stOrder", 
		query="select lh.location, lh.sublocation from LocationHierarchy lh where lh.name='mit'"),
	@NamedQuery(name="locationHierarchy.allMIT2ndOrder", 
		query="select lh.location, lh2.sublocation from LocationHierarchy lh join lh.sublocation.hierarchies lh2 where lh.name='mit' and lh2.name='mit'"),
	@NamedQuery(name="locationHierarchy.allMIT3rdOrder", 
		query="select lh.location, lh3.sublocation from LocationHierarchy lh join lh.sublocation.hierarchies lh2 join lh2.sublocation.hierarchies lh3 where lh.name='mit' and lh2.name='mit' and lh3.name='mit'"),
	@NamedQuery(name="locationHierarchy.allMIT4thOrder", 
		query="select lh.location, lh4.sublocation from LocationHierarchy lh join lh.sublocation.hierarchies lh2 join lh2.sublocation.hierarchies lh3 join lh3.sublocation.hierarchies lh4 where lh.name='mit' and lh2.name='mit' and lh3.name='mit' and lh4.name='mit'"),
	@NamedQuery(name="locationHierarchy.bySublocationId", query="SELECT lh FROM LocationHierarchy lh WHERE lh.name='mit' AND lh.sublocation.id=:sublocationId"),
	@NamedQuery(name="locationHierarchy.byLocationsAndName", query="SELECT lh FROM LocationHierarchy lh WHERE name=:name AND location=:location AND sublocation=:sublocation"),
	@NamedQuery(name="locationHierarchy.nextLevelFromType", query="SELECT lh FROM LocationHierarchy lh WHERE lh.name='mit' AND lh.sublocation.id=:sublocationId and lh.location.type = :locationType"),
	@NamedQuery(name="locationHierarchy.correlationByLocations", query="select correlation from LocationHierarchy where name = 'mit.scoring' and location = :location and sublocation = :sublocation"),
	@NamedQuery(name="locationHierarchy.parentLocationsBySublocationId", query="select lh.location from LocationHierarchy lh where " +
			"lh.name = 'mit.scoring' and lh.type='DESCENDANT' and lh.sublocation.id = :sublocationId " +
			"and lh.location.type not in ('COUNTRY', 'MISCELLANEOUS', 'USER_DEFINED')")
})
@Entity
public class LocationHierarchy implements Serializable {
	
	public enum LocationHierarchyType {
		SELF(1.0f), NEIGHBOR(0.8f), 
		DESCENDANT(0.8f), ANCESTOR(0.5f), 
		NEIGHBOR_DESCENDANT(0.6f), NEIGHBOR_ANCESTOR(0.3f);
		
		private float correlation;
		private LocationHierarchyType(float correlation) {
			this.correlation = correlation;
		}
		public float getCorrelation() {
			return correlation;
		}
	}

	private static final long serialVersionUID = 1L;
	
	@Id @GeneratedValue
	private Long id;
	
	private String name;
	
	@ManyToOne
	private Location location;
	
	@ManyToOne
	private Location sublocation;
	
	private Float correlation;
	
	// Name is defined as to be from location to sublocation
	@Enumerated(EnumType.STRING)
	private LocationHierarchyType type;
	
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

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Location getSublocation() {
		return sublocation;
	}

	public void setSublocation(Location sublocation) {
		this.sublocation = sublocation;
	}

	public Float getCorrelation() {
		return correlation;
	}

	public void setCorrelation(Float correlation) {
		this.correlation = correlation;
	}

	public LocationHierarchyType getType() {
		return type;
	}

	public void setType(LocationHierarchyType type) {
		this.type = type;
	}

}
