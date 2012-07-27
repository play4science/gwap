/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
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
