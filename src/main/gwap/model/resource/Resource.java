/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
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
