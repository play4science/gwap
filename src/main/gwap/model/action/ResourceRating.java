/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.model.action;

import gwap.model.resource.Resource;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Scope;

/**
 * 
 * @author Fabian Kneißl
 */
@Entity
//@Name("locationAssignment")
@Scope(ScopeType.EVENT)
public class ResourceRating extends Action {
	
	private static final long serialVersionUID = 1L;

	@ManyToOne	private Resource resource;
	
	private Long rating;
	
	public Resource getResource() {
		return resource;
	}
	public void setResource(Resource resource) {
		this.resource = resource;
	}
	public Long getRating() {
		return rating;
	}
	public void setRating(Long rating) {
		this.rating = rating;
	}
	
}
