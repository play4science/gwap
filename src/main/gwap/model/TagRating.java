/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.model;

import gwap.model.resource.Resource;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@NamedQueries( {
	@NamedQuery(name = "tagRating.byId",
			query = "select t from TagRating t where " +
			"t.id=:id"),
	@NamedQuery(name = "tagRating.byResourceAndTag",
			query = "select t from TagRating t where " +
			"t.resource.id=:resid and t.tag=:tag"),
	@NamedQuery(name = "tagRating.addRating",
					query = "update TagRating t " +
							"set t.rating=t.rating+:rating " +
							"where t.id=:id"),
	@NamedQuery(name = "tagRating.byThresholdAndId",
			query="select tr from TagRating tr " +
					"where tr.rating>:thresh " +
					"and (not tr.resource.id in " +
					"(select tgs.resource from tr.tag.taggings tgs where tgs.person=null)) " +
					"and tr.id=:id")
})

@Entity
public class TagRating implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue
	private Long id;
	
	@ManyToOne	private Tag tag;
	@ManyToOne	private Resource resource;
				private Double rating=0.0;
	
				
	public Long getId() {
		return id;
	}

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public Double getRating() {
		return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public String toString() {
		return "("+(resource!=null?resource.toString():"null")+","+tag+"="+rating.toString()+")";
	}
}