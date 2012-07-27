/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.model.resource;

import gwap.model.Person;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.jboss.seam.annotations.Name;

@NamedQueries({
	@NamedQuery(name = "artResourceTeaser.all", query = "select r from ArtResourceTeaser r"),
	@NamedQuery(name = "artResourceTeaser.byLanguageAndResource", 
			query = "select r from ArtResourceTeaser r where r.language = :language and r.resource = :resource"),
	@NamedQuery(name = "artResourceTeaser.count",
			query = "select t.language, count(*) from ArtResourceTeaser t join t.resource a where a.enabled=true group by t.language")
})

/**
 * Description for an ArtResource, depends on language.
 * 
 * @author Christoph Wieser
 */

@Entity
@Name("artResourceTeaser")
public class ArtResourceTeaser implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id	@GeneratedValue
	private Long id;

	@ManyToOne     private ArtResource resource;
	@ManyToOne     private Person creator;
	
	private String language;
	@Lob
	private String description;
	
	private Date createDate;
	
	// for logging purposes
	private String internalNote;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public Person getCreator() {
		return creator;
	}

	public void setCreator(Person creator) {
		this.creator = creator;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getInternalNote() {
		return internalNote;
	}

	public void setInternalNote(String internalNote) {
		this.internalNote = internalNote;
	}

	@Override
	public String toString() {
		return "" + id + ": " + description;
	}
}
