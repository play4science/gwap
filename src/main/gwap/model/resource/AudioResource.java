/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.model.resource;

import gwap.model.Person;
import gwap.model.Source;
import gwap.tools.AudioAccessBean;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@NamedQueries({
	// internal usage only (no enabled check)
	@NamedQuery(name="audioResource.all", query="select r from AudioResource r"),
	@NamedQuery(name="audioResource.randomEnabled", query="select r from AudioResource r where enabled = true order by random()"),
	@NamedQuery(name="audioResource.randomEnabledId", query="select r.id from AudioResource r where enabled = true order by random()"),
	@NamedQuery(name="audioResource.randomNotAssigned", 
			query="select r from AudioResource r where r.enabled = true " +
					"and not exists (select l.id from r.locationAssignments l where l.person = :person) " +
					"order by random()"),
	@NamedQuery(name="audioResource.randomNotAssignedUniqueLocationInGamesession", 
			query="select r from AudioResource r where r.enabled = true " +
					"and not exists (select l.id from r.locationAssignments l where l.person = :person) " +
					"and not exists (select l2.id from LocationAssignment l2 where " +
					"	l2.resource.location.id = r.location.id and l2.gameRound.gameSession = :gamesession and l2.person = :person) " +
					"order by random()")
})

/**
 * @author Fabian Kneißl
 */
@Entity
@Name("audioResource")
@Scope(ScopeType.CONVERSATION)
public class AudioResource extends Resource {
	
	private static final long serialVersionUID = 1L;

	@ManyToOne                          private Person creator;
	@ManyToOne							private Source source;
	@ManyToOne                          private Location location;
	
	private String path;
	private String dateCreated;
	
	@Transient
	private String url;
	
	public Person getCreator() {
		return creator;
	}
	public void setCreator(Person creator) {
		this.creator = creator;
	}
	public String getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public Source getSource() {
		return source;
	}
	public void setSource(Source source) {
		this.source = source;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}	
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUrl() {
		if (url == null && path != null && source != null)
			AudioAccessBean.setResourceUrl(this);
		return url;
	}
	@Override
	public String toString() {
		return id + ": " + url;
	}
}
