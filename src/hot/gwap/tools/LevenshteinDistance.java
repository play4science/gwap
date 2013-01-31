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
