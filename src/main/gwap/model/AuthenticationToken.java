/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.TokenUsername;
import org.jboss.seam.annotations.security.TokenValue;

@Entity
@Name("authenticationToken")
public class AuthenticationToken implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id	@GeneratedValue
	private Long tokenId;
	@TokenUsername
	private String username;
	@TokenValue
	private String value;


	public Long getTokenId() {
		return tokenId;
	}

	public void setTokenId(Long tokenId) {
		this.tokenId = tokenId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}