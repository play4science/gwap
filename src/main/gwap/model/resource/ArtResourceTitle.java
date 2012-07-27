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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import org.jboss.seam.annotations.Name;

/**
 * Title for an ArtResource, depends on language.
 * 
 * @author Fabian Kneißl
 */

@Entity
@Name("artResourceTitle")
public class ArtResourceTitle implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id	@GeneratedValue
	private Long id;
	
	@ManyToOne     private ArtResource resource;
	
	private String language;
	
	@Lob
	private String title;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public ArtResource getResource() {
		return resource;
	}
	public void setResource(ArtResource resource) {
		this.resource = resource;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
}
