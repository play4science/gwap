/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
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
