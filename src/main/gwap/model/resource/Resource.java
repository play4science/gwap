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

import gwap.model.GameRound;
import gwap.model.Topic;
import gwap.model.action.LocationAssignment;
import gwap.model.action.Tagging;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

/**
 * Base class for all kinds of resources
 * 
 * @author Christoph Wieser
 */

@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class Resource implements Serializable {
	
	protected static final long serialVersionUID = 1L;

	@Id	@GeneratedValue
	protected Long id;
	
	protected String externalId;
	
	/** This field may be used as computed field for several other properties **/
	protected Boolean enabled;
	
	@ManyToMany(mappedBy="resources")	protected List<GameRound> gameRounds = new ArrayList<GameRound>();
	
	@OneToMany(mappedBy="resource")	    private Set<Tagging> taggings = new HashSet<Tagging>();
	
	@OneToMany(mappedBy="resource", cascade=CascadeType.REMOVE)
	private List<LocationAssignment> locationAssignments = new ArrayList<LocationAssignment>();
	
	@ManyToMany(mappedBy="resources")
	protected List<Topic> topics = new ArrayList<Topic>();
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<GameRound> getGameRounds() {
		return gameRounds;
	}

	public void setGameRounds(List<GameRound> gameRounds) {
		this.gameRounds = gameRounds;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public List<LocationAssignment> getLocationAssignments() {
		return locationAssignments;
	}

	public void setLocationAssignments(List<LocationAssignment> locationAssignments) {
		this.locationAssignments = locationAssignments;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
	
	public Set<Tagging> getTaggings() {
		return taggings;
	}

	public void setTaggings(Set<Tagging> taggings) {
		this.taggings = taggings;
	}

	public List<Topic> getTopics() {
		return topics;
	}

	public void setTopics(List<Topic> topics) {
		this.topics = topics;
	}

	
}
