/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap;

import gwap.authentication.Authenticator;
import gwap.model.Person;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.log.Log;

@Name("personBean")
@Scope(ScopeType.STATELESS)
public class PersonBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Logger	                 private Log log;
	@Out                     private Person person;
	@In                      private EntityManager entityManager;
	@In                      private LocaleSelector localeSelector;
	@In(create=true)         private Authenticator authenticator;
	
	@Factory("person")
	public void createAnonymousPerson() {
		if (authenticator.tryLogin()) {
			person = authenticator.getPerson();
		} else {
			log.info("Creating anonymous person.");
			person = new Person();
			person.setLanguage(localeSelector.getLanguage());
			person.setUsername("");
			person.setLastLogin(new Date());
			entityManager.persist(person);
			entityManager.flush(); // needed to persist immediately
		}
	}
}
