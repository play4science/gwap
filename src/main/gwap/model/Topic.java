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

package gwap.model;

import gwap.model.resource.Resource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@NamedQueries({
	@NamedQuery(name="topic.all", 
				query="from Topic order by name"),
	@NamedQuery(name="topic.allCustom", 
				query="from Topic where source = :source order by name"),
	@NamedQuery(name="topic.enabled",
				query="from Topic where enabled=true order by name"),
	@NamedQuery(name="topic.enabledCustom",
				query="from Topic where enabled=true and source = :source order by name"),
	@NamedQuery(name="topic.byName",
				query="from Topic where name = :name"),
	@NamedQuery(name="topic.byNameCustom",
				query="from Topic where name = :name and source = :source"),
	@NamedQuery(name="topic.byResourceCustom",
				query="from Topic top where source = :source and " +
						"exists (from top.resources res where res = :resource)")
})
/**
 * A topic is a combination of resources and can be used to group
 * resources into a common theme.
 * 
 * @author Fabian Kneißl
 *
 */
@Entity
@Name("topic")
@Scope(ScopeType.CONVERSATION)
public class Topic implements Serializable {

	private static final long serialVersionUID = 0L;

	@Id @GeneratedValue
	private Long id;
	
	private String name;
	
	private Boolean enabled;
	
	@ManyToOne
	private Source source;

	@ManyToMany
	private List<Resource> resources = new ArrayList<Resource>();

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

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public List<Resource> getResources() {
		return resources;
	}

	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}
	
	public String toString() {
		if (getId() != null) {
			String val = "Topic#"+getId();
			if (getName() != null)
				val += ":"+getName();
			return val;
		}
		return super.toString();
	}

}
