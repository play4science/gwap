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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.jboss.seam.annotations.Name;

/**
 * @author maders, wieser
 */
@NamedQueries({
	@NamedQuery(name="badge.byPlatform",
			query="from Badge where platform = :platform order by worth"),
	@NamedQuery(name="badge.byDeviceId",
			query="select b from Badge b join b.persons p where p.deviceId = :deviceId"),
	@NamedQuery(name="badge.nextForPerson",
			query="select b from Badge b " +
					"where b.worth > all (select b2.worth from Person p join p.badges b2 where p = :person) " +
					"and b.platform = :platform " +
					"order by b.worth"),
	@NamedQuery(name="badge.bestForPerson",
			query="select b from Person p join p.badges b where p = :person order by b.worth desc")
})
@Entity
@Name("badge")
public class Badge implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue
	private Long id;
	
	private String name;
	
	@Lob
	private String description;
	private Integer worth;
	private Integer condition;
	
	private String platform;
	
	@ManyToMany(mappedBy="badges")
	private Set<Person> persons = new HashSet<Person>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getWorth() {
		return worth;
	}
	public void setWorth(Integer worth) {
		this.worth = worth;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Set<Person> getPersons() {
		return persons;
	}
	public void setPersons(Set<Person> persons) {
		this.persons = persons;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	public Integer getCondition() {
		return condition;
	}
	public void setCondition(Integer condition) {
		this.condition = condition;
	}
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Badge) {
			Badge other = (Badge) obj;
			return this.id != null && this.id.equals(other.id);
		}
		return false;
	};
	public String toString() {
		return "Badge#"+getId()+"["+getName()+"]";
	}
}
