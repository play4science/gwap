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

package gwap.authentication;

import gwap.model.Education;
import gwap.model.Gender;
import gwap.model.Person;
import gwap.model.resource.Location;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.MissingResourceException;

import javax.persistence.EntityManager;

import org.hibernate.validator.Email;
import org.hibernate.validator.Length;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Renderer;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.Credentials;

@Name("profile")
@Scope(ScopeType.EVENT)
public class Profile implements Serializable {
		
	private static final long serialVersionUID = 1L;
	
	@In	                     private FacesMessages facesMessages;
	@In @Out                 private Person person;
	@In                      private LocaleSelector localeSelector;
	@In                      private EntityManager entityManager;
	@In                      private Credentials credentials;
	@In(create=true)		 private Renderer renderer;
	@Length(min=6, max=20)   private String password;
	@Email					 private String email;
	@In(required=false)@Out(required=false) private Long locationId;
	private String homeTownName;
	@In(create=true)         private String platform;
	

	private String passwordConfirmation;
	
	private static final Integer[] birthyears;

	private String emailVerifyToken;
	
	static {
		int year = GregorianCalendar.getInstance().get(Calendar.YEAR);
		int startyear = 1900;
		birthyears = new Integer[year - startyear];
		for (int i = 0; i < birthyears.length; i++) {
			birthyears[i] = startyear + birthyears.length - i; //reverse
		}
	}
	
	public String update() {
		try {
			entityManager.merge(person);
			facesMessages.addFromResourceBundle("profile.updated");
		} catch(Exception e) {
			facesMessages.addFromResourceBundle(Severity.ERROR, "profile.updateError");
		}
		return null;
	}
	
	public void updateSilent() {
		try {
			entityManager.merge(person);
		} catch (Exception e) { }
	}

	public String updateProfile() {
		if (isShowHometownField()) {
			//set hometown of this person
			
			Location hometown = null;
			if(locationId != null)
				hometown = entityManager.find(Location.class, locationId);
			if (locationId == null || !hometown.getName().equals(homeTownName)) { 
				@SuppressWarnings("unchecked")
				List<Location> list = entityManager.createNamedQuery("location.likeName")
					.setParameter("name", homeTownName)
					.getResultList();
				if (list.size() > 0)
					person.setHometown(list.get(0));
				else {
					hometown = new Location();
					hometown.setName(homeTownName);
					hometown.setType(Location.LocationType.USER_DEFINED);
					entityManager.persist(hometown);
					person.setHometown(hometown);
				}
			} else {
				person.setHometown(hometown);
			}
		}

		// Set new language of this person as session language	 
		localeSelector.selectLanguage(person.getLanguage());
		return update();
	}
	
	public String updatePassword() {
		if (password.equals(passwordConfirmation)) {
			person.setPassword(MD5Crypt.crypt(password));
		} else {
			facesMessages.addFromResourceBundle(Severity.ERROR, "register.passwordMismatch");
			return null;
		}
		credentials.setPassword(password);
		return update();
	}
	
	public void updateEmail() {
		// Test if email already exists
		List<Person> sameEmail = entityManager.createNamedQuery("person.byEmail").setParameter("email", email).getResultList();
		if (!sameEmail.isEmpty()) {
			facesMessages.addFromResourceBundle(Severity.ERROR, "profile.changeEmail.alreadyRegistered");
			return;
		}
		
		person = entityManager.find(Person.class, person.getId());
		person.setUnverifiedEmail(email);
		emailVerifyToken = Authenticator.generateRandomPassword();
		person.setEmailVerifyToken(emailVerifyToken);
		entityManager.merge(person);
		entityManager.flush();
		renderer.render("/email/verifyEmail.xhtml");
		facesMessages.addFromResourceBundle("login.verifyEmail.wait");
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
	
	public Gender[] getGenders() {
		return Gender.values();
	}
	
	public Education[] getEducations() {
		List<Education> educations = new ArrayList<Education>();
		Education[] educationValues = new Education[] {
				Education.NONE, Education.APPRENTICESHIP, Education.HIGHSCHOOL, 
				Education.COLLEGE, Education.UNIVERSITY, Education.DOCTOR
		};
		if (platform.equals("metropolitalia")) {
			educationValues = new Education[] {
					Education.NONE, Education.ELEMENTARY_SCHOOL, Education.HIGHSCHOOL, 
					Education.COLLEGE, Education.UNIVERSITY
			};
		}
		for (Education education : educationValues ) {
			try {
				String localized = SeamResourceBundle.getBundle().getString(education.getKey());
				if (localized != null && localized.length() > 0)
					educations.add(education);
			} catch (MissingResourceException e) { /* don't add it */ }
		}
		Education[] result = new Education[educations.size()];
		return educations.toArray(result);
	}
	
	public boolean isShowHometownField() {
		return platform.equals("metropolitalia");
	}
	
	public Integer[] getBirthyears() {
		return birthyears;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmailVerifyToken() {
		return emailVerifyToken;
	}
	
	public String getHomeTownName() {
		if (homeTownName == null)
			if(person.getHometown() != null)
				homeTownName = person.getHometown().getName();
		return homeTownName;
	}

	public void setHomeTownName(String homeTownName) {
		this.homeTownName = homeTownName;
	}
	
}
