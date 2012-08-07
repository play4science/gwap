/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.model;

import gwap.model.resource.Resource;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * A PerceptionRating saves which user added a perception pair to "rate" a resource.
 * 
 * @author Jonas Hoelzler
 */

@NamedQueries( { 
@NamedQuery(
			name="PerceptionRating.byResource",
			query="select t from PerceptionRating t")
})
@Entity
public class PerceptionRating implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue
	private Long id;
	private Date created;
	@ManyToOne	private PerceptionPair perceptionPair;
	@ManyToOne	private Resource resource;
    private Long fillOutTimeMs;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public PerceptionPair getPerceptionPair() {
		return perceptionPair;
	}

	public void setPerceptionPair(PerceptionPair perceptionPair) {
		this.perceptionPair = perceptionPair;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}
	
	public Long getFillOutTimeMs() {
		return fillOutTimeMs;
	}
	
	public void setFillOutTimeMs(Long fillOutTimeMs) {
		this.fillOutTimeMs = fillOutTimeMs;
		
	}

	@Override
	public String toString() {
		return "The player rated " + resource.toString() + " with " + perceptionPair;			
	}

}