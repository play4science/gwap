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

import org.hibernate.validator.NotNull;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@NamedQueries({
	@NamedQuery(name="term.randomByLevel", 
				query="select p from Term p where p.rating = :level and p.tag.language = :language order by random()"),
	@NamedQuery(name="term.randomByLevelMinConfirmedTags", 
				query="select p from Term p where p.rating = :level and p.tag.language = :language and " +
						"(select count(*) from p.confirmedTags) >= :minConfirmedTags order by random()"),
	@NamedQuery(name="term.randomTagsNotRelated", 
				query="select t from Term p join p.confirmedTags t where p != :term and t.language = :language order by random()")
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
		return "Term " + tag.getName();
	}

}
