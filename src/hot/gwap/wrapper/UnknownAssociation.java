/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.wrapper;

import gwap.model.Tag;
import gwap.model.resource.Term;

import java.util.List;

/**
 * A utility class for displaying associations together with term 
 * and confirmedTags in the GUI.
 * 
 * @author Fabian Kneissl
 */
public class UnknownAssociation {
	private List<Tag> associations;
	private Term term;

	public List<Tag> getAssociations() {
		return associations;
	}
	public void setAssociations(List<Tag> associations) {
		this.associations = associations;
	}
	public Term getTerm() {
		return term;
	}
	public void setTerm(Term term) {
		this.term = term;
	}
}
