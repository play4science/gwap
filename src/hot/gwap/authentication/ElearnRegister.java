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

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

/**
 * @author kneissl
 */
@Name("elearnRegister")
public class ElearnRegister extends Register {

	@In RemoteAuthenticator remoteAuthenticator;
	private String externalPassword;
	
	public String createPerson() {
		boolean backstageUserExists = remoteAuthenticator.backstageUserExists(person.getExternalUsername(), externalPassword);
		if (!backstageUserExists) {
			facesMessages.addToControlFromResourceBundle("externalUsername", "register.externalUserNotFound");
			return "register";
		}
		return super.createPerson();	
	}

	public String getExternalPassword() {
		return externalPassword;
	}

	public void setExternalPassword(String externalPassword) {
		this.externalPassword = externalPassword;
	}
	
}
