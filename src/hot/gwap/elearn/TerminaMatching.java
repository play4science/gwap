/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.elearn;

import gwap.model.Tag;
import gwap.model.resource.Term;
import gwap.tools.TagSemantics;

/**
 * @author kneissl
 */
public class TerminaMatching {

	public static boolean isAssociationInConfirmedTags(String association, Term term) {
		return checkAssociationInConfirmedTags(association, term) != null;
	}
	
	public static Tag checkAssociationInConfirmedTags(String association, Term term) {
		return TagSemantics.containsNotNormalized(term.getConfirmedTags(), association);
	}
}
