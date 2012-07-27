/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.model.resource;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * GeoPoint is a point consisting of a latitude and a longitude position.
 * 
 * @author Fabian Kneißl
 */
@Entity
public class GeoPoint implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id @GeneratedValue
	private Long id;
	
	@Column(nullable=false)
	private Float latitude;
	
	@Column(nullable=false)
	private Float longitude;
	
	public GeoPoint() { }
	
	public GeoPoint(Float latitude, Float longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Float getLatitude() {
		return latitude;
	}

	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}

	public Float getLongitude() {
		return longitude;
	}

	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}

	@Override
	public String toString() {
		return "Lat: " + latitude + ", lng: " + longitude;
	}

}
