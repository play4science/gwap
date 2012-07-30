/*
t * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.model;

import gwap.model.resource.Resource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@NamedQueries({
	@NamedQuery(name="topic.topicsByName", 
				query="select t from Topic t order by t.name")
})
/**
 * A topic is a combination of resources and can be used to group
 * resources into a common theme.
 * 
 * @author Fabian Kneißl
 *
 */
@Entity
@Name("topic")
@Scope(ScopeType.CONVERSATION)
public class Topic implements Serializable {

	private static final long serialVersionUID = 0L;

	@Id @GeneratedValue
	private Long id;
	
	private String name;

	@ManyToMany
	private List<Resource> resources = new ArrayList<Resource>();

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

	public List<Resource> getResources() {
		return resources;
	}

	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}

}
