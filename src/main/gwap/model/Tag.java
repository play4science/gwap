/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.model;

import gwap.model.action.Tagging;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import org.hibernate.validator.Length;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * This is general declaration of tags that must not be used in games.
 * 
 * @author Christoph Wieser
 */

@NamedQueries( {
	@NamedQuery(name = "tag.all", query = "select t from Tag t"),
	@NamedQuery(name = "tag.byName", query = "select t from Tag t where lower(t.name) = lower(:name)"),
	@NamedQuery(name = "tag.byId", query = "select t from Tag t where t.id = :id"),
	@NamedQuery(name = "tag.byIds", query = "select t from Tag t where t.id in (:ids)"),
	@NamedQuery(name = "tag.tagByNameAndLanguage",
			    query = "select t " +
			    		"from Tag t " +
			    		"where t.language=:language and lower(t.name)=lower(:name)"),
	@NamedQuery(name = "tag.tagFrequencyOfResourceByNameAndLanguage",
			    query = "select count(t) " +
			    		"from Tagging t " +
			    		"where t.resource=:resource and lower(t.tag.name)=lower(:tagName) and t.tag.language=:language"),
	//Select a tag from a resource and a list of resources that is applied to only the given resource in the list
	@NamedQuery(name = "tag.uniqueByResourceList",
				query = "select t1.tag " +
						"from ArtResource r " +
						"left join r.taggings t1 " +
						"left join t1.tag t " +
						"left join t.taggings t2 " +
						"where r=:res and t2.resource in (:reslist) and t1.tag.language=:language and " +
						"not t1.tag in (:taglist) " +
						"group by t1.tag.id, t.id, t.blacklisted, t.language, t.name " +
						"having count(distinct t2.resource)=1 " +
						"order by count(t2) desc"),
	@NamedQuery(name="tag.byResource",
				query="select t.tag.id from Tagging t " +
				 	  "where t.resource=:resource and t.tag.language=:language " +
					  "group by t.tag.id, t.resource " +
					  "having count(t.id)>=:minOccurrence " +
					  "order by random()"),
	@NamedQuery(name="tag.tagNamesByResource",
				query="select t.tag.name from Tagging t " +
				 	  "where t.resource=:resource and t.tag.language=:language " +
					  "group by t.tag.name, t.resource " +
					  "having count(t.id)>=:minOccurrence " +
					  "order by count(t.id) desc")
})

@Entity
@Name("tag")
@Scope(ScopeType.EVENT)
public class Tag implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id	
	@GeneratedValue
	private Long id;

	@OneToMany(mappedBy="tag")	private List<Tagging> taggings = new ArrayList<Tagging>();
	@ManyToMany(mappedBy="opponentTags") private List<GameRound> gameRounds = new ArrayList<GameRound>();

	@Length(min=1,max=50)
	private String name;
	private Boolean blacklisted;
	private String language;

	
	public Boolean getBlacklisted() {
		return blacklisted;
	}

	public void setBlacklisted(Boolean blacklisted) {
		this.blacklisted = blacklisted;
	}

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

	public String toString() {
		return name;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public List<Tagging> getTaggings() {
		return taggings;
	}

	public void setTaggings(List<Tagging> taggings) {
		this.taggings = taggings;
	}

	public List<GameRound> getGameRounds() {
		return gameRounds;
	}

	public void setGameRounds(List<GameRound> gameRounds) {
		this.gameRounds = gameRounds;
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && 
				other instanceof Tag &&
				((Tag)other).getId() != null &&
				((Tag)other).getId().equals(getId());
	}
	
}
