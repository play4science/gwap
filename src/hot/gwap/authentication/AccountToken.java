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

import gwap.model.Person;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.hibernate.validator.Length;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;

/**
 * Specifies actions that occur when a token has been accessed
 * @author Fabian Kneissl
 */
@Name("accountToken")
@Scope(ScopeType.PAGE)
public class AccountToken implements Serializable {

	@RequestParameter
	private String token;

	@In	                    private FacesMessages facesMessages;
	@In						private EntityManager entityManager;
	@In                     private Credentials credentials;
	@In                     private Identity identity; 
	@Logger					private Log log;
	@In(create=false,required=false) @Out(required=false)
							private Person person;
	
	private Person candidate;
	
	@Length(min=6, max=20)   private String password;
	private String passwordConfirmation;

	private boolean isVerifyEmail = false;
	private boolean isResetPassword = false;
	
	@Create
	public void checkToken() {
		if (token != null) {
			try {
				log.info("Accessed reset token #0", token);
				try {
					candidate = (Person) entityManager.createNamedQuery("person.byPasswordResetToken").setParameter("passwordResetToken", token).getSingleResult();
					isResetPassword = true;
					Calendar calendar = GregorianCalendar.getInstance();
					calendar.setTime(candidate.getPasswordResetDate());
					calendar.add(Calendar.HOUR, 24); // Token is only valid for 24 hours
					if (calendar.getTime().before(new Date())) { // too old
						candidate = null;
					}
				} catch (NoResultException e) {
					candidate = (Person) entityManager.createNamedQuery("person.byEmailVerifyToken").setParameter("emailVerifyToken", token).getSingleResult();
					isVerifyEmail = true;
				}
				
				if (isVerifyEmail) {
					candidate.setEmail(candidate.getUnverifiedEmail());
					candidate.setUnverifiedEmail(null);
					candidate.setEmailVerifyToken(null);
					entityManager.flush();
					if (person != null && person.getId().equals(candidate.getId()))
						person = candidate; // update e-mail in profile
					log.info("Sucessfully verified email for person #0", candidate);
				} else if (isResetPassword) {
					// user enters new password
					log.info("Reset token #0 is by person #1 for reset password", token);
				}
			} catch (Exception e) {
				log.info("Token is invalid", e);
				candidate = null;
			}
		}
	}
	
	public String updatePassword() {
		if (password.equals(passwordConfirmation)) {
			candidate = entityManager.find(Person.class, candidate.getId());
			candidate.setPassword(MD5Crypt.crypt(password));
			candidate.setPasswordResetToken(null);
			candidate.setPasswordResetDate(null);
			entityManager.flush();
		} else {
			facesMessages.add("#{messages['register.passwordMismatch']}");
			return null;
		}
		credentials.setUsername(candidate.getUsername());
		credentials.setPassword(password);
		identity.login();
		facesMessages.add("#{messages['login.passwordChanged']}");
		return "home";
	}
	
	public boolean isResetPassword() {
		return isResetPassword;
	}
	
	public boolean isVerifyEmail() {
		return isVerifyEmail;
	}
	
	public boolean isInvalidToken() {
		return !isResetPassword && !isVerifyEmail;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
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
