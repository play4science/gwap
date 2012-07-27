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
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * It is saved whether a resource is familiar to a user or not.
 * 
 * @author Fabian Kneißl
 */

@Entity
@Name("familiarity")
@Scope(ScopeType.EVENT)
public class Familiarity extends Action {

	private static final long serialVersionUID = 1L;

	@ManyToOne	private Resource resource;

	private Boolean familiar;
	
	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public Boolean getFamiliar() {
		return familiar;
	}

	public void setFamiliar(Boolean familiar) {
		this.familiar = familiar;
	}
	
}