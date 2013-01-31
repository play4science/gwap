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
