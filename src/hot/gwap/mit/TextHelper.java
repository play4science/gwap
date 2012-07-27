/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
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
