/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.model;

import gwap.model.action.Action;
import gwap.model.resource.Location;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import org.hibernate.validator.Email;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Range;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.security.management.UserFirstName;
import org.jboss.seam.annotations.security.management.UserLastName;
import org.jboss.seam.annotations.security.management.UserPassword;
import org.jboss.seam.annotations.security.management.UserPrincipal;
import org.jboss.seam.annotations.security.management.UserRoles;

@NamedQueries( {
		@NamedQuery(name = "person.byId", query = "select p from Person p where p.id=:id"),
		@NamedQuery(name = "person.usernameById", query = "select p.username from Person p where p.id=:id"),
		@NamedQuery(name = "person.authentication", query = "select p from Person p where lower(p.username)=lower(:username)"),
		@NamedQuery(name = "person.byUsername", query = "select p from Person p where lower(p.username)=lower(:username)"),
		@NamedQuery(name = "person.byEmail", query = "select p from Person p where lower(p.email)=lower(:email)"),
		@NamedQuery(name = "person.byPasswordResetToken", query = "select p from Person p where p.passwordResetToken = :passwordResetToken"),
		@NamedQuery(name = "person.byEmailVerifyToken", query = "select p from Person p where p.emailVerifyToken = :emailVerifyToken"),
		@NamedQuery(name = "person.countByUsername", query = "select count(p) from Person p where lower(p.username)=lower(:username)"),
		@NamedQuery(name = "person.hasRole", query = "select p from Person p join p.roles r where r.role=:role and p.id=:personId"),
		@NamedQuery(name = "person.hasRole", query = "select p from Person p join p.roles r where r.role=:role and p.id=:personId")
		//@NamedQuery(name = "person.allPoints", query = "select p.name,sum(la.score) from Person p, LocationAssignment la, StatementCharacterization sc where la.person = p group by p")
})

/**
 * A person represents a user. Usually its a human. 
 * 
 * @author Christoph Wieser
 */

@Entity
@Name("person")
@Scope(ScopeType.SESSION)
public class Person implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id	@GeneratedValue
	private Long id;
	
	private String externalUsername; 
	
	@ManyToOne								private Person personConnected; // the person it is connected to (if it exists)
	@OneToMany(mappedBy="personConnected")	private Set<Person> connectedPersons = new HashSet<Person>(); // persons, that are connected to this person (reflexive).
	@OneToMany(mappedBy="person")			private List<GameRound> gameRounds = new ArrayList<GameRound>();
	@OneToMany(mappedBy="person")			private List<Action> actions = new ArrayList<Action>();
	@ManyToMany	@UserRoles					private Set<Role> roles = new HashSet<Role>();
	
	// private data
	@UserFirstName
	private String forename;
	@UserLastName
	private String surname;
	private Date death;
	
	@Enumerated(EnumType.STRING)
	private Gender gender;
	@Range(min=1900, max=2011)
	private Integer birthyear;
	@ManyToOne
	private Location hometown;
	@Enumerated(EnumType.STRING)
	private Education education;
	
	@Email
	private String email;
	@Email
	private String unverifiedEmail;
	@NotNull
	@UserPrincipal
	private String username;
	@UserPassword(hash="md5")
	private String password;
	private String language;
	private Date lastLogin;
	private Date lastLogout;
	private Date registration;
	private String passwordResetToken;
	private String emailVerifyToken;
	private Date passwordResetDate;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void addConnectedPerson(Person person) {
		connectedPersons.add(person);
	}
	
	public String getForename() {
		return forename;
	}

	public Date getDeath() {
		return death;
	}

	public void setDeath(Date death) {
		this.death = death;
	}

	public void setForename(String forename) {
		this.forename = forename;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public Date getLastLogout() {
		return lastLogout;
	}
	
	public void setLastLogout(Date lastLogout) {
		this.lastLogout = lastLogout;
	}
	
	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public Set<Person> getConnectedPersons() {
		return connectedPersons;
	}

	public void setConnectedPersons(Set<Person> connectedPersons) {
		this.connectedPersons = connectedPersons;
	}

	public Date getRegistration() {
		return registration;
	}

	public void setRegistration(Date registration) {
		this.registration = registration;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Date getPasswordResetDate() {
		return passwordResetDate;
	}

	public void setPasswordResetDate(Date passwordResetDate) {
		this.passwordResetDate = passwordResetDate;
	}

	public String getPasswordResetToken() {
		return passwordResetToken;
	}

	public void setPasswordResetToken(String passwordResetToken) {
		this.passwordResetToken = passwordResetToken;
	}

	public Person getPersonConnected() {
		return personConnected;
	}

	public void setPersonConnected(Person personConnected) {
		this.personConnected = personConnected;
	}

	public List<GameRound> getGameRounds() {
		return gameRounds;
	}

	public void setGameRounds(List<GameRound> gameRounds) {
		this.gameRounds = gameRounds;
	}

	public List<Action> getActions() {
		return actions;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

	public String toString() {
		if (username != null && username.length()>0)
			return "User#" + id + "(" + username + ")";
		return "User#" + id;
	}
	
	public String getName() {
		String name = "";
		if (forename != null && forename.length() > 0)
			name = forename;
		if (surname != null && surname.length() > 0) {
			if (name.length() == 0)
				name = surname;
			else
				name = name + " " + surname;
		}
		if (name.length()==0)
			name = username;
		return name;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = Gender.valueOf(gender);
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public Integer getBirthyear() {
		return birthyear;
	}

	public void setBirthyear(Integer birthyear) {
		this.birthyear = birthyear;
	}

	public Location getHometown() {
		return hometown;
	}

	public void setHometown(Location hometown) {
		this.hometown = hometown;
	}

	public Education getEducation() {
		return education;
	}

	public void setEducation(Education education) {
		this.education = education;
	}

	public String getExternalUsername() {
		return externalUsername;
	}

	public void setExternalUsername(String externalUsername) {
		this.externalUsername = externalUsername;
	}

	public String getUnverifiedEmail() {
		return unverifiedEmail;
	}

	public void setUnverifiedEmail(String unverifiedEmail) {
		this.unverifiedEmail = unverifiedEmail;
	}

	public String getEmailVerifyToken() {
		return emailVerifyToken;
	}

	public void setEmailVerifyToken(String emailVerifyToken) {
		this.emailVerifyToken = emailVerifyToken;
	}
}
