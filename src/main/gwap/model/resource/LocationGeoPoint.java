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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;


/**
 * Persisted Many-To-Many relationship table
 * 
 * @author Fabian Kneißl
 */
@Entity
public class LocationGeoPoint implements Serializable {
	
private static final long serialVersionUID = 1L;
	
	@Id @GeneratedValue
	private Long id;
	
	@ManyToOne(optional=false)
	private Location location;
	
	@ManyToOne(optional=false)
	private GeoPoint geoPoint;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public GeoPoint getGeoPoint() {
		return geoPoint;
	}

	public void setGeoPoint(GeoPoint geoPoint) {
		this.geoPoint = geoPoint;
	}
	
}
