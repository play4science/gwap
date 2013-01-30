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
 * This class represents a tagging that has been added artificially to the pool,
 * for example for highlighting certain tags in the tag cloud for demonstration
 * purposes.
 * 
 * @author Fabian Kneißl
 */
@NamedQueries({
	@NamedQuery(
			name = "virtualTagging.tagsByResourceAndLanguage",
			query = "select t.tag " +
					"from VirtualTagging t " +
					"where t.resource=:resource and t.tag.language=:language")
})
@Entity
@Name("virtualTagging")
@Scope(ScopeType.EVENT)
public class VirtualTagging extends Action {
	
	private static final long serialVersionUID = -5008563057236047839L;

	@ManyToOne
	private Resource resource;
	
	@ManyToOne
	private Tag tag;
	
	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}
}
