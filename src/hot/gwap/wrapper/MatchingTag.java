/*
 * This file is part of gwap, an open platform for games with a purpose
 *
 * Copyright (C) 2013
 * Project play4science
 * Lehr- und Forschungseinheit für Programmier- und Modellierungssprachen
 * Ludwig-Maximilians-Universität München
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
