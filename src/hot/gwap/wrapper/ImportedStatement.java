/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.wrapper;

import gwap.model.Person;
import gwap.model.resource.Location;

/**
 * @author  Fabian Kneißl
 */
public class ImportedStatement {
	private Location location;
	private String statement;
	private String description;
	private String category;
	private String italianoStandard;
	private String comment;
	private Person creator;

	public ImportedStatement() {
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatement() {
		return statement;
	}

	public void setStatement(String statement) {
		this.statement = statement;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getItalianoStandard() {
		return italianoStandard;
	}

	public void setItalianoStandard(String italianoStandard) {
		this.italianoStandard = italianoStandard;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Person getCreator() {
		return creator;
	}

	public void setCreator(Person player) {
		this.creator = player;
	}
	
}