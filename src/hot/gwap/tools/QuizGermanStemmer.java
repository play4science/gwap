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

package gwap.tools;

import gwap.model.Person;

import java.util.ArrayList;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * A stemmer for German words.
 * <p>
 * The algorithm is based on the report
 * "A Fast and Simple Stemming Algorithm for German Words" by J&ouml;rg Caumanns
 * (joerg.caumanns at isst.fhg.de). Adapted by Elena Levushkina
 * </p>
 */
public class QuizGermanStemmer {
	/**
	 * Buffer for the terms while stemming them.
	 */
	private StringBuilder sb = new StringBuilder();

	/**
	 * Amount of characters that are removed with <tt>substitute()</tt> while
	 * stemming.
	 */
	private int substCount = 0;

	/**
	 * Stemms the given term to an unique <tt>discriminator</tt>.
	 * 
	 * @param term
	 *            The term that should be stemmed.
	 * @return Discriminator for <tt>term</tt>
	 */
	public static String stem(Person person) {
		String term = "";
		try {
		term = person.getName();
		

			if (term.length() > 20) {
				if(person.getForename()!=null && person.getSurname()!=null){
					term = person.getForename().substring(0, 1) + ". "
					+ person.getSurname();
				}
				
			}
			term = replaceUnknown(term);

		} catch (Exception e) {
			// Fehler bei Konvertierung
			e.printStackTrace();
		}
		return term;
	}

	public static String stemText(String title) {
		try {

			title = replaceUnknown(title);

		} catch (Exception e) {
			// Fehler bei Konvertierung
			e.printStackTrace();
		}
		return title;
	}

	private static String replaceUnknown(String str) {

		String[] search;
		String[] replace;
		ArrayList<String> searchList = new ArrayList<String>();
		ArrayList<String> replaceList = new ArrayList<String>();

		
		// O

		searchList.add("Ö");
		replaceList.add("Oe");
		
		// U

		searchList.add("Ü");
		replaceList.add("Ue");

		// u

		searchList.add("ü");
		replaceList.add("ue");

		// s

		searchList.add("ß");
		replaceList.add("ss");
		// A
		searchList.add("Á");
		replaceList.add("A");

		searchList.add("À");
		replaceList.add("A");

		searchList.add("Â");
		replaceList.add("A");

		searchList.add("Ã");
		replaceList.add("A");

		searchList.add("Ä");
		replaceList.add("Ae");

		searchList.add("Å");
		replaceList.add("A");

		searchList.add("Æ");
		replaceList.add("Ae");

		searchList.add("Ā");
		replaceList.add("A");

		// a
		searchList.add("ä");
		replaceList.add("ae");

		searchList.add("à");
		replaceList.add("a");

		searchList.add("á");
		replaceList.add("a");

		searchList.add("â");
		replaceList.add("a");

		searchList.add("ã");
		replaceList.add("a");

		searchList.add("å");
		replaceList.add("a");

		searchList.add("æ");
		replaceList.add("ae");

		// E
		searchList.add("È");
		replaceList.add("E");

		searchList.add("É");
		replaceList.add("E");

		searchList.add("Ê");
		replaceList.add("E");

		searchList.add("Ë");
		replaceList.add("E");

		searchList.add("Ē");
		replaceList.add("E");

		// e
		searchList.add("è");
		replaceList.add("e");

		searchList.add("é");
		replaceList.add("e");

		searchList.add("ê");
		replaceList.add("e");

		searchList.add("ë");
		replaceList.add("e");

		searchList.add("ē");
		replaceList.add("e");

		// C
		searchList.add("Ç");
		replaceList.add("ç");

		searchList.add("ç");
		replaceList.add("c");

		// o
		searchList.add("ó");
		replaceList.add("o");

		searchList.add("ö");
		replaceList.add("oe");

		searchList.add("ô");
		replaceList.add("o");

		searchList.add("ö");
		replaceList.add("o");

		searchList.add("õ");
		replaceList.add("o");

		searchList.add("Ã$");
		replaceList.add("c");

		searchList.add("Ã");
		replaceList.add("c");
		
		// z
		
		searchList.add("ž");
		replaceList.add("z");
		
		
		//i
		searchList.add("í");
		replaceList.add("i");
		
		searchList.add("ì");
		replaceList.add("i");

		search = (String[]) searchList.toArray(new String[searchList.size()]);

		replace = (String[]) replaceList
				.toArray(new String[replaceList.size()]);

		for (int i = 0; i < search.length; i++) {

			str = str.replaceAll(search[i], replace[i]);
		}

		return str;
	}

}
