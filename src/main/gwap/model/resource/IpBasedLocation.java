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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * the location, where the player's id is from
 * 
 * @author Andre Reichstaller
 */
@NamedQueries({
	@NamedQuery(name="byCountryRegionCity", query="select il from IpBasedLocation il where il.country = :country and il.region = :region and il.city = :city")
})
@Entity
public class IpBasedLocation implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue
	private Long id;

	private String country;
	private String region;
	private String city;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	
	@Override
	public String toString() {
		return "Location#" + id + "(" + city + "/" + region + "/" + country + ")";
	}

}
