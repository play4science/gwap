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

package gwap.search;

/**
 * @author Fabian Kneißl
 */
public enum ArtResourceSchema implements Field {
	
	ID("id"),
	TAG("tag"),
	TITLE("title"),
	DATE_CREATED("date_created"),
	LOCATION("location"),
	INSTITUTION("institution"),
	TEASER("teaser"),
	ARTIST("artist"),
	ARTIST_BIRTHYEAR("artist_birthyear");
	
	String name;
	
	private ArtResourceSchema(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

}
