/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.tools;

import gwap.model.Tag;
import gwap.wrapper.TagFrequency;

import java.util.List;


/**
 * @author Fabian Kneißl
 */
public class TagSemantics {

	public static String removePunctuation(String tag)
	{
		return tag.replaceAll("[,.?!§$%&/()={}\"\\[\\]\\-\\_\\s']+"," ");		 
	}
	
	public static int wordCount(String tag)
	{
		return tag.split("\\s+").length;		
	}
	
	
	public static String normalize(String tag) {
		if (tag != null && !tag.isEmpty()) {
			tag = tag.trim().toLowerCase();
		}
		return tag;
	}
	
	public static String normalizeDiacritics(String tag) {
		if (tag != null && !tag.isEmpty()) {
			tag = tag.replaceAll("ß", "ss").replaceAll("ä", "ae").replaceAll("ö", "oe").replaceAll("ü", "ue");
		}
		return tag;
	}
	
	public static boolean equals(String tag1, String tag2) {
		String tag1Normalized = normalizeDiacritics(normalize(tag1));
		String tag2Normalized = normalizeDiacritics(normalize(tag2));
		return tag1Normalized.equals(tag2Normalized);
	}
	
	public static Tag containsNotNormalized(List<Tag> tagList, String tag) {
		String tagNormalized = normalizeDiacritics(normalize(tag));
		for (Tag tag2 : tagList) {
			if (tagNormalized.equals(normalizeDiacritics(normalize(tag2.getName()))))
				return tag2;
		}
		return null;
	}
	
	public static TagFrequency containsNotNormalized2(List<TagFrequency> tagList, String tag) {
		String tagNormalized = normalizeDiacritics(normalize(tag));
		for (TagFrequency tagFrequency : tagList) {
			if (tagNormalized.equals(normalizeDiacritics(normalize(tagFrequency.getName()))))
				return tagFrequency;
		}
		return null;
	}
}
