/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.search;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.Redirect;

/**
 * @author Fabian Kneißl
 */
@Name("queryBean")
@Scope(ScopeType.CONVERSATION)@AutoCreate
public class QueryBean implements Serializable {
	
	private static final long serialVersionUID = -2139435394363943555L;
	
	private String queryString;
	private String tags;
	private String artist;
	private String title;
	private String location;
	private String year;
	private String letter;
	
	public void setNotEmptyParameters(Redirect redirect) {
		if (!isNullOrEmpty(queryString))
			redirect.setParameter("queryString", queryString);
		if (!isNullOrEmpty(tags))
			redirect.setParameter("tags", tags);
		if (!isNullOrEmpty(artist))
			redirect.setParameter("artist", artist);
		if (!isNullOrEmpty(title))
			redirect.setParameter("title", title);
		if (!isNullOrEmpty(location))
			redirect.setParameter("location", location);
		if (!isNullOrEmpty(year))
			redirect.setParameter("year", year);
		if (!isNullOrEmpty(letter))
			redirect.setParameter("letter", letter);
	}
	
	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getLetter() {
		return letter;
	}

	public void setLetter(String letter) {
		this.letter = letter;
	}

}
