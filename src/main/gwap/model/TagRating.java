/*
 * This file is part of gwap
 *
 * Copyright (C) 2013
 * Project play4science
 * Lehr- und Forschungseinheit für Programmier- und Modellierungssprachen
 * Ludwig-Maximilians-Universität München
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
