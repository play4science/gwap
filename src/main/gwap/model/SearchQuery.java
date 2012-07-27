/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Saves search queries entered by users
 * 
 * @author Fabian Kneissl
 */
@Entity
public class SearchQuery implements Serializable {
	
	private static final long serialVersionUID = 188617162714560293L;
	
	@Id @GeneratedValue
	private Long id;
	private String query;
	private Date created;
	@ManyToOne
	private Person person;
	private String userAgent;
	private String platform;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Person getPerson() {
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public String getUserAgent() {
		return userAgent;
	}
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	
}
