/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.wrapper;

import java.util.List;

/**
 * @author  Fabian Kneißl
 */
public class ImportedTerm {
	private String term;
	private Integer rating;
	private List<String> associations;

	public ImportedTerm() {
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public List<String> getAssociations() {
		return associations;
	}

	public void setAssociations(List<String> associations) {
		this.associations = associations;
	}

}