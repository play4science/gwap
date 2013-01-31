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

package gwap.model.action;

import gwap.model.resource.Resource;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * Used for characterizing statements with a custom Integer value.
 * 
 * @author Fabian Kneißl
 */
@NamedQueries({
	@NamedQuery(name="characterization.scoreSumByPerson",
			query="select sum(a.score) from Characterization a where a.person = :person"),
	@NamedQuery(name="characterization.byResource",
			query="from Characterization where resource = :resource"),
	@NamedQuery(name="characterization.groupedResults",
			query="select name, c.value, count(c.value) from Characterization c where c.resource.id = :resourceId group by name, value")
})
@Entity
@Name("characterization")
@Scope(ScopeType.PAGE)
public class Characterization extends Action {

	public enum Name {
		gender, maturity, cultivation;
	}

	private static final long serialVersionUID = 1L;

	private Integer value;
	
	@ManyToOne
	private Resource resource;
	
	@Enumerated(EnumType.STRING)
	private Name name;
	
	public Characterization() { }
	public Characterization(Name name) {
		this.name = name;
	}
	
	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource statement) {
		this.resource = statement;
	}
	
	public Name getName() {
		return name;
	}

	public void setName(Name name) {
		this.name = name;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}
	
}
