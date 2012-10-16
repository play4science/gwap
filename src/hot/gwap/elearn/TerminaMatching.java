/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.elearn;

import gwap.model.Tag;
import gwap.tools.TagSemantics;

import java.util.List;

/**
 * @author kneissl
 */
public class TerminaMatching {

	public static boolean isAssociationInList(String association, List<Tag> termList) {
		return checkAssociationInList(association, termList) != null;
	}
	
	public static Tag checkAssociationInList(String association, List<Tag> termList) {
		return TagSemantics.containsNotNormalized(termList, association);
	}
}
