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

package gwap.model.action;

import gwap.model.Tag;
import gwap.model.resource.Resource;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * TagATag Data
 * 
 * @author Christoph Wieser
 */

@NamedQueries( { 
	@NamedQuery(
			name = "metatagging.tagFrequencyByMetaTagAndLanguage",
			query = "select new gwap.wrapper.TagFrequency(m.tag.name, count(m.tag.name)) " +
					"from MetaTagging m " +
					"where m.tag.language=:language " +
					"group by m.tag.name " +
					"having count(m.tag.name) >= :threshold " +
					"order by count(m.tag.name) desc"),
	@NamedQuery(
			name = "metatagging.tagFrequencyByMetaTagAndLanguageAndResource",
			query = "select new gwap.wrapper.TagFrequency(m.tag.name, count(m.tag.name)) " +
					"from MetaTagging m " +
					"where m.tagResource=:tagResource and m.tag.language=:language " +
					"group by m.tag.name " +
					"having count(m.tag.name) >= :threshold " +
					"order by count(m.tag.name) desc")
})

@Entity
@Name("metaTagging")
@Scope(ScopeType.EVENT)
public class MetaTagging extends Action {

	private static final long serialVersionUID = 1L;
	
	@ManyToOne	private Tag tag;
	@ManyToOne	private Tag tagResource;
	@ManyToOne	private Resource resource;

	public Tag getTag() {
		return tag;
	}
	
	public void setTag(Tag tag) {
		this.tag = tag;
	}
	
	public Tag getTagResource() {
		return tagResource;
	}

	public void setTagResource(Tag tagResource) {
		this.tagResource = tagResource;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	@Override
	public String toString() {
		if (person!=null)
			return getPerson().toString() + " tagged " + tagResource.toString() + " with " + tag;
		else
			return "The players tagged " + tagResource.toString() + " with " + tag;			
	}
	
}
