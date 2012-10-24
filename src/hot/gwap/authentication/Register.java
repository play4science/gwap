/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.authentication;

import gwap.model.Person;
import gwap.model.Role;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.validator.Length;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Renderer;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;

@Name("register")
public class Register implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Logger					 protected Log log;
	@In                      private EntityManager entityManager;
	@In                      protected FacesMessages facesMessages;
	@In                      private Credentials credentials;
	@In                      private Identity identity; 
	@In	@Out                 protected Person person;
	@In                      private LocaleSelector localeSelector;
	@In(create = true)		 private Renderer renderer;
	@Length(min=6, max=30)   private String password;
	private String passwordConfirmation;
	
	public String createPerson() {
		// User name available?
		Query query = entityManager.createNamedQuery("person.countByUsername");
		query.setParameter("username", person.getUsername());
		long countByUsername = (Long) query.getSingleResult();
		if (countByUsername > 0L) {
			facesMessages.addToControlFromResourceBundle("username", "register.usernameTaken");
			return "register";
		}
		
		if (password.equals(passwordConfirmation)) {
			person.setPassword(MD5Crypt.crypt(password));
		} else {
			facesMessages.addToControlFromResourceBundle("password", "register.passwordMismatch");
			return "register";
		}
		
		// E-Mail already used?
		query = entityManager.createNamedQuery("person.byEmail");
		query.setParameter("email", person.getEmail());
		if (query.getResultList().size() > 0) {
			facesMessages.addToControlFromResourceBundle("email", "register.emailAlreadyRegistered");
			return "register";
		}
		person.setLanguage(localeSelector.getLanguage());
		person.setRegistration(new Date());
		person.setLastLogin(null); // in order not to trigger the duplicate login functionality
		
		// User must verify E-Mail
		person.setUnverifiedEmail(person.getEmail());
		person.setEmailVerifyToken(Authenticator.generateRandomPassword());
		
		if (person.getId() == null) {
			// new person
			entityManager.persist(person);
		} else {
			// anonymous person so far
			entityManager.merge(person);
		}
		entityManager.flush();
		
		log.info("Sending welcome email to #0 <#1>", person.getName(), person.getEmail());
		renderer.render("/email/welcome.xhtml");
		log.info("Registered new user #0", person);
		
		// set role to 'player'
		Role playerRole = (Role) entityManager.createNamedQuery("role.player").getSingleResult();
		person.getRoles().add(playerRole);
	
		credentials.setUsername(person.getUsername());
		credentials.setPassword(password);
		identity.login();

		return "home";
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordConfirmation() {
		return passwordConfirmation;
	}

	public void setPasswordConfirmation(String passwordConfirmation) {
		this.passwordConfirmation = passwordConfirmation;
	}

}
