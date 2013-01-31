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

package gwap.authentication;

import gwap.model.Person;
import gwap.model.Role;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Renderer;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;

/**
 * The authenticator is responsible for user logins. It also handles the roles
 * and the mapping of persons to BPM actors.
 * 
 * @author Christoph Wieser
 */
@AutoCreate
@Name("authenticator")
@Scope(ScopeType.SESSION)
public class Authenticator implements Serializable {

	private static final long serialVersionUID = 1L;

	@Logger
	private Log log;
	@In
	private EntityManager entityManager;
	@In
	private Credentials credentials;
	@In
	private Identity identity;
	@In
	private LocaleSelector localeSelector;
	@In
	private FacesMessages facesMessages;
	@In(create = true)
	private Renderer renderer;
	// @In private Actor actor; // jBPM
	@In(required = false)
	@Out(required = false)
	private Person person;
	private Person candidate;

	public boolean authenticate() {
		if (credentials.getUsername() == null || credentials.getUsername().length() == 0) {
			facesMessages.addToControl("username", Severity.ERROR, "#{messages['person.username']} #{messages['validator.notNull']}");
			return false;
		}
		if (credentials.getPassword() == null || credentials.getPassword().length() == 0) {
			facesMessages.addToControl("password", Severity.ERROR, "#{messages['person.password']} #{messages['validator.notNull']}");
			return false;
		}
		try {
			// Get candidatePerson from the database by form data
			Query query = entityManager
					.createNamedQuery("person.authentication");
			query.setParameter("username", credentials.getUsername());
			Person candidatePerson = (Person) query.getSingleResult();

			// Case insensitive => set login username
			credentials.setUsername(candidatePerson.getUsername());

			// Verify password with database password
			String verifiedPassword = MD5Crypt.crypt(credentials.getPassword(),
					candidatePerson.getPassword());
			boolean passwordMatch = verifiedPassword.equals(candidatePerson
					.getPassword());

			Calendar calendar = GregorianCalendar.getInstance();
			Date now = new Date();

			// Do not allow users to log in twice (here: 60 seconds barrier)
			// TODO: Implement it in a better way :)
			boolean duplicateLogin = false;
			if (candidatePerson.getLastLogin() != null) {
				calendar.setTime(candidatePerson.getLastLogin());
				calendar.add(Calendar.SECOND, 60); // duration of timeout
				Date timeout = calendar.getTime();
				duplicateLogin = !timeout.before(now);
				if (duplicateLogin && candidatePerson.getLastLogout() != null)
					duplicateLogin = !candidatePerson.getLastLogout().before(now);
			}

			if ((passwordMatch) && !duplicateLogin) {

				// Connect anonymous person with registered person
				// (e.g. for saving anonymously achieved game results)
				boolean anonymousUserCreated = (person != null) && (person.getId() != null);
				if (anonymousUserCreated) {
					person = entityManager.find(Person.class, person.getId());
					if (person != null && person.getId() != candidatePerson.getId()) {
						person.setPersonConnected(candidatePerson);
						facesMessages.addFromResourceBundle("register.pointsSaved");
					}
				}

				// Login candidatePerson
				person = candidatePerson;
				log.info("Login: #0", person.getUsername());

				updatePersonOnLogin(person);
				
				// jBPM stuff
				// actor.setId(Long.toString(person.getId()));
				// actor.getGroupActorIds().add("player");
				// log.info("Login: #0 (id: #1)", person.getUsername(),
				// actor.getId());

				return true;
			} else {
				facesMessages.addToControl("profileForm", Severity.ERROR, "#{messages['org.jboss.seam.loginFailed']}");
				return false;
			}
		}

		catch (NoResultException ex) {
			return false;
		}
	}
	
	private void updatePersonOnLogin(Person person) {
		// Set meta data
		try {
			for (SelectItem locale : localeSelector.getSupportedLocales())
				if (locale.getValue().equals(person.getLanguage()))
					localeSelector.setLanguage(person.getLanguage());
		} catch (NullPointerException e) {
			// Not supported for restful services
		}

		if (person.getRoles() != null) {
			for (Role role : person.getRoles()) {
				identity.addRole(role.getRole());
			}
		}
	}

	public boolean tryLogin() {
		try {
			if (identity.isLoggedIn())
				return true;
			else if (identity.tryLogin()) {
				person = (Person) entityManager.createNamedQuery("person.byUsername")
						.setParameter("username", identity.getPrincipal().getName())
						.getSingleResult();
				credentials.setUsername(person.getUsername());
				updatePersonOnLogin(person);
				// does not get triggered automatically 
				Events.instance().raiseEvent("org.jboss.seam.security.loginSuccessful");
				log.info("Person logged in with remember-me method #0", person);
				return true;
			}
		} catch (Exception e) {
			// do nothing
			log.info("Could not login with remember me method", e);
		}
		return false;
	}

	@Observer("org.jboss.seam.security.loginSuccessful")
	public void loginSuccessful() {
		person.setLastLogout(null);
		person.setLastLogin(new Date());
		entityManager.merge(person);
	}

	@Observer("org.jboss.seam.security.loggedOut")
	public void logout() {
		person.setLastLogout(new Date());
		entityManager.merge(person);
	}

	public void resetPassword() {
		try {
			log.info("Reset password for user with email " + person.getEmail());
			Query query = entityManager.createNamedQuery("person.byEmail");
			query.setParameter("email", person.getEmail());
			candidate = (Person) query.getSingleResult();

			// Create random password
			candidate.setPasswordResetToken(generateRandomPassword());
			candidate.setPasswordResetDate(new Date());

			renderer.render("/email/resetPassword.xhtml");
			facesMessages
					.add("#{messages['general.emailSentSuccessfully']}");

			entityManager.merge(candidate);
		} catch (NoResultException e) {
			facesMessages.add("#{messages['general.emailNotFound']}");
		} catch (Exception e) {
			facesMessages.add(
					"#{messages['general.emailSendingFailed']}",
					e.getMessage());
			log.info("Email sending failed: " + e.getMessage());
		}
	}

	public Person getCandidate() {
		return candidate;
	}

	/**
	 * This method is used to generate a random password
	 * 
	 * @param int - length of the password
	 * @return String - random password
	 */
	public static String generateRandomPassword(int length) {
		String passarray = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		int range = passarray.length();
		String passwd = "";
		Random generator = new Random();
		for (int i = 0; i < length; i++) {
			int rnd = generator.nextInt(range);
			String ch = passarray.substring(rnd, rnd + 1);
			passwd += ch;
		}
		return passwd;
	}
	
	/**
	 * This method is used to generate a random password with a default length of 30
	 */
	public static String generateRandomPassword() {
		return Authenticator.generateRandomPassword(30);
	}
	
	public Person getPerson() {
		return person;
	}
}
