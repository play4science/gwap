/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.model.action;

import gwap.model.Tag;
import gwap.model.resource.Resource;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * This class represents a tagging that has been added artificially to the pool,
 * for example for highlighting certain tags in the tag cloud for demonstration
 * purposes.
 * 
 * @author Fabian Kneißl
 */
@NamedQueries({
	@NamedQuery(
			name = "virtualTagging.tagsByResourceAndLanguage",
			query = "select t.tag " +
					"from VirtualTagging t " +
					"where t.resource=:resource and t.tag.language=:language")
})
@Entity
@Name("virtualTagging")
@Scope(ScopeType.EVENT)
public class VirtualTagging extends Action {
	
	private static final long serialVersionUID = -5008563057236047839L;

	@ManyToOne
	private Resource resource;
	
	@ManyToOne
	private Tag tag;
	
	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}
}
