/*
 * This file is part of gwap, an open platform for games with a purpose
 *
 * Copyright (C) 2013
 * Project play4science
 * Lehr- und Forschungseinheit für Programmier- und Modellierungssprachen
 * Ludwig-Maximilians-Universität München
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
