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

package gwap.mit;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helps with processing input text
 * 
 * @author Fabian Kneißl
 */
public class TextHelper {
	
	private static Pattern pattern = Pattern.compile("[\\p{L}]+|[^\\p{L}\\s]"); // \p{L}: unicode letter

	public static List<String> splitIntoTokens(String text) {
		List<String> tokens = new ArrayList<String>();
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			tokens.add(matcher.group());
		}
		return tokens;
	}

}
