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

	public static boolean isAssociationInList(String association, List<Tag> tagList) {
		return checkAssociationInList(association, tagList) != null;
	}
	
	public static Tag checkAssociationInList(String association, List<Tag> tagList) {
		String associationNormalized = normalize(association);
		for (Tag tag : tagList) {
			if (associationNormalized.equals(normalize(tag.getName())))
				return tag;
		}
		return null;
	}
	
	private static String normalize(String tag) {
		return TagSemantics.normalizeDiacritics(TagSemantics.normalize(tag)).replaceAll("[^a-z0-9]", "");
	}
}
