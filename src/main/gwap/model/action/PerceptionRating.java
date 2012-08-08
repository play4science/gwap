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
public class PerceptionRating extends Action {

	private static final long serialVersionUID = 1L;

	@ManyToOne	private Resource resource;
    private Long fillOutTimeMs;

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

}