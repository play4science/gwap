/*
 * This file is part of gwap
 *
 * Copyright (C) 2013
 * Project play4science
 * Lehr- und Forschungseinheit für Programmier- und Modellierungssprachen
 * Ludwig-Maximilians-Universität München
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
