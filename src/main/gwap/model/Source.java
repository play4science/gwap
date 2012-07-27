/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.jboss.seam.annotations.Name;

/**
 * Sources for resources like who offered an image and 
 * where can I find it. 
 * 
 * @author Christoph Wieser
 *
 */

@NamedQueries( {
	@NamedQuery(name = "source.byName",
			    query = "select s " +
			    		"from Source s " +
			    		"where s.name = :name")
						})

@Entity
@Name("source")
public class Source implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id	@GeneratedValue
	private Long id;
	
	private String url;
	private String name;
	private String homepage;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHomepage() {
		return homepage;
	}
	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}
	@Override
	public String toString() {
		return "Source#" + id;
	}
}
