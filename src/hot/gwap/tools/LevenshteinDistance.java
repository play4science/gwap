/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.tools;

/**
 * Calculation of Damerau-Levenshtein distance between two strings
 * 
 * @author Elena Levushkina
 */
public class LevenshteinDistance {
	
	private static int minimum(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
	}

	
	public static int computeLevenshteinDistance(CharSequence str1, CharSequence str2) {
		
        int[][] distance = new int[str1.length() + 1][str2.length() + 1];

        for (int i = 0; i <= str1.length(); i++)
                distance[i][0] = i;
        for (int j = 0; j <= str2.length(); j++)
                distance[0][j] = j;

        for (int i = 1; i <= str1.length(); i++){
                for (int j = 1; j <= str2.length(); j++){
                        distance[i][j] = minimum(
                                        distance[i - 1][j] + 1,
                                        distance[i][j - 1] + 1,
                                        distance[i - 1][j - 1] + ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0 : 1));
                        if(i > 1 && j > 1 && str1.charAt(i-1) == str2.charAt(j - 2) && str1.charAt(i - 2) == str2.charAt(j-1)) {
                        	distance[i][j] = Math.min(
                        			distance[i][j],
                        			distance[i - 2][j-2] + 1   // transposition
                        	);
                        } 
                }
        }
        return distance[str1.length()][str2.length()];
	}
}