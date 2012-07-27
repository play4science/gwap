/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.wrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Fabian Kneißl
 */
public class MatchingTag {

	private String tag;
	
	private boolean directMatch = false;
	
	private boolean indirectMatch = false;
	
	private int score = 0;
	
	private List<String> alternativeTags;
	
	private boolean tagCorrectionCompleted = false;

	public MatchingTag() {
	}
	
	public MatchingTag(String tag) {
		super();
		this.tag = tag;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public boolean isDirectMatch() {
		return directMatch;
	}

	public void setDirectMatch(boolean directMatch) {
		this.directMatch = directMatch;
	}
	
	public boolean isIndirectMatch() {
		return indirectMatch;
	}

	public void setIndirectMatch(boolean indirectMatch) {
		this.indirectMatch = indirectMatch;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getScore() {
		return score;
	}
	
	public List<String> getAlternativeTags() {
		return alternativeTags;
	}

	public void setAlternativeTags(List<String> alternativeTags) {
		this.alternativeTags = alternativeTags;
	}

	public boolean isTagCorrectionCompleted() {
		return tagCorrectionCompleted;
	}

	public void setTagCorrectionCompleted(boolean tagCorrectionCompleted) {
		this.tagCorrectionCompleted = tagCorrectionCompleted;
	}

	public boolean hasCorrection() {
		return alternativeTags != null && alternativeTags.size() > 0;
	}
	
	public String getMatchType() {
		if (isDirectMatch())
			return "directMatch";
		else if (isIndirectMatch())
			return "indirectMatch";
		else
			return null;
	}
	
	public String getCssClass() {
		String s = getMatchType();
		if (s == null)
			s = "";
		if (hasCorrection())
			s = s + " wavyUnderline";
		return s;
	}

	public boolean equals(String tag) {
		return this.tag != null && this.tag.equals(tag);
	}

	public void addAlternativeTag(String tag) {
		if (alternativeTags == null)
			alternativeTags = new ArrayList<String>();
		alternativeTags.add(tag);
	}
	
}
