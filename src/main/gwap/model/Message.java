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
import javax.persistence.Lob;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

@NamedQueries({
		@NamedQuery(name = "message.keysByLocale", query = "select m.key from Message m where m.locale=:locale", cacheable = true),
		@NamedQuery(name = "message.valueByLocaleAndKey", query = "select m.value from Message m where m.locale=:locale and m.key=:key", cacheable = true)
})

@Entity
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
public class Message implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	private String locale;

	private String key;

	@Lob
	private String value;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
