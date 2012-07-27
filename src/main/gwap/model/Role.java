/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.management.RoleName;

@NamedQueries({
		@NamedQuery(name="role.player", query="from Role where role='player'"),
		@NamedQuery(name="role.admin", query="from Role where role='admin'")
})

/**
 * The role of a person such as "player" or "admin". 
 * 
 * @author Christoph Wieser
 */

@Entity
@Name("role")
public class Role implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id	@GeneratedValue
	private Long id;
	
	@ManyToMany(mappedBy="roles") private List<Person> persons;
	
	@RoleName
	private String role;
	private String roleName;
	
	public List<Person> getPersons() {
		return persons;
	}
	public void setPersons(List<Person> persons) {
		this.persons = persons;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String toString() {
		return role;
	}
}
