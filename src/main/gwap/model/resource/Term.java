/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.model.resource;

import gwap.model.Tag;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;

import org.hibernate.validator.NotNull;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@NamedQueries({
	@NamedQuery(name="term.randomByLevel", 
				query="select p from Term p where p.enabled = true and p.rating = :level and p.tag.language = :language order by random()"),
	@NamedQuery(name="term.randomByLevelNotInGameSession", 
				query="select p from Term p where p.enabled = true and p.rating = :level and p.tag.language = :language " +
						"and p.id not in (select r2.id from GameRound r join r.resources r2 where r.gameSession=:gameSession) " +
						"order by random()"),
	@NamedQuery(name="term.sensibleRandomForGame", 
				query="select p from Term p where p.enabled = true and p.rating = :level and p.tag.language = :language " +
						"and p.id not in (select r2.id from GameRound r join r.resources r2 where r.gameSession=:gameSession) " +
						"and (select count(*) from p.confirmedTags) >= :minConfirmedTags " +
						"order by random()"),
	@NamedQuery(name="term.sensibleRandomForGameWithTopic", 
				query="select p from Term p join p.topics t where p.enabled = true and t = :topic and p.rating = :level and p.tag.language = :language " +
						"and p.id not in (select r2.id from GameRound r join r.resources r2 where r.gameSession=:gameSession) " +
						"and (select count(*) from p.confirmedTags) >= :minConfirmedTags " +
						"order by random()"),
	@NamedQuery(name="term.sensibleRandomForGameWithoutConfig", 
				query="select p from Term p where p.enabled = true and p.tag.language = :language " +
						"and p.id not in (select r2.id from GameRound r join r.resources r2 where r.gameSession=:gameSession) " +
						"order by random()"),
	@NamedQuery(name="term.randomByTopic", 
				query="select p from Term p join p.topics t where p.enabled = true and t = :topic and p.tag.language = :language order by random()"),
	@NamedQuery(name="term.randomByTopicNotInGameSession", 
				query="select p from Term p join p.topics t where p.enabled = true and t = :topic and p.tag.language = :language " +
						"and p.id not in (select r2.id from GameRound r join r.resources r2 where r.gameSession=:gameSession) " +
						"order by random()"),
	@NamedQuery(name="term.randomTagsNotRelated", 
				query="select t from Term p join p.confirmedTags t where p.enabled = true and p != :term and t.language = :language " +
						"order by random()"),					
	@NamedQuery(name = "term.allTerms",
				query = "select t from Term t join t.tag g order by g.name"),			
	@NamedQuery(name="term.byExternalSessionId", 
				query="select t from Term t join t.gameRounds r join r.gameSession s " +
						"where s.externalSessionId=:externalSessionId " +
						"group by t.id, t.externalId, t.enabled, t.tag, t.rating, r.number order by r.number")
})
@Entity
@Name("term")
@Scope(ScopeType.CONVERSATION)
public class Term extends Resource {

	private static final long serialVersionUID = 0L;

	@NotNull
	@OneToOne
	private Tag tag;

	private Integer rating;
	
	@ManyToMany
	@OrderBy("name")
	private List<Tag> confirmedTags = new ArrayList<Tag>();

	public Term() {
	}

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}

	public List<Tag> getConfirmedTags() {
		return confirmedTags;
	}

	public void setConfirmedTags(List<Tag> confirmedTags) {
		this.confirmedTags = confirmedTags;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}
	
	public String toString() {
		return "Term#" + tag.getId() + "[name=" + tag.getName() + "]";
	}

}
