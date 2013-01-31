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

package gwap.game.quiz;

import gwap.model.Person;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Serializable;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

/**
 * Erzeugt falsche Antworten fuers Woelfflin-Quiz
 * 
 * @author Jonas Hölzler
 * 
 */
@Name("wrongAnswerBean")
@Scope(ScopeType.STATELESS)
public class WrongAnswerBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Logger
	private Log log;
	@In
	private EntityManager entityManager;

	private String pre = "/quiz/";

	private int ANFANG_RENAISSANCE = 1420;
	private int ENDE_BAROCK = 1800;
	private int MITTE = 1600;

	public List<Person> createArtistList(int level, int year) {
		assert (level >= 10 || level <= 15);

		int minyear = 0;
		int maxyear = 0;

//		switch (level) {
//		case 9:
//			minyear = ENDE_BAROCK - 100;
//			maxyear = 2020 + 100;
//		case 10:
//			minyear = ENDE_BAROCK -100;
//			maxyear = 2020 + 100;
//		case 11:
//			minyear = ANFANG_RENAISSANCE -100;
//			maxyear = ENDE_BAROCK + 100;
//		case 12:
//			minyear = ANFANG_RENAISSANCE - 100;
//			maxyear = ENDE_BAROCK + 100;
//		case 13:
//			if (year <= 1600) {
//				minyear = ANFANG_RENAISSANCE;
//				maxyear = MITTE;
//			} else if (year >= 1600) {
//				minyear = MITTE;
//				maxyear = ENDE_BAROCK;
//			}
//		case 14:
//			if (year <= 1600) {
//				minyear = ANFANG_RENAISSANCE;
//				maxyear = MITTE;
//			} else if (year >= 1600) {
//				minyear = MITTE;
//				maxyear = ENDE_BAROCK;
//			}
//
//		case 15:
//			minyear = year - 100;
//			maxyear = year + 100;
//		}

		int dist = 0;
		
		switch (level) {
		case 10:
			dist = 200;
			return entityManager
			.createQuery(
					"SELECT p FROM Person p WHERE death is not null and (death < '01.01."
							+ (year - dist) + "' or death > '01.01." + (year + dist)
							+ "') ORDER by random()").setMaxResults(3)
			.setHint("org.hibernate.readOnly", true)
			.getResultList();
		case 11:
			dist = 200;
			return entityManager
			.createQuery(
					"SELECT p FROM Person p WHERE death is not null and (death < '01.01."
							+ (year - dist) + "' or death > '01.01." + (year + dist)
							+ "') ORDER by random()").setMaxResults(3)
			.setHint("org.hibernate.readOnly", true)
			.getResultList();
		case 12:
			dist = 150;
			return entityManager
			.createQuery(
					"SELECT p FROM Person p WHERE death is not null and (death < '01.01."
							+ (year - dist) + "' or death > '01.01." + (year + dist)
							+ "') ORDER by random()").setMaxResults(3)
			.setHint("org.hibernate.readOnly", true)
			.getResultList();
		case 13:
			if (year <= 1600) {
				minyear = ANFANG_RENAISSANCE;
				maxyear = MITTE;
			} else if (year >= 1600) {
				minyear = MITTE;
				maxyear = ENDE_BAROCK;
			}
			return entityManager
			.createQuery(
					"SELECT p FROM Person p WHERE death is not null and death > '01.01."
							+ minyear + "' and death < '01.01." + maxyear
							+ "' ORDER by random()").setMaxResults(3)
			.setHint("org.hibernate.readOnly", true)
			.getResultList();
		case 14:
			if (year <= 1600) {
				minyear = ANFANG_RENAISSANCE;
				maxyear = MITTE;
			} else if (year >= 1600) {
				minyear = MITTE;
				maxyear = ENDE_BAROCK;
			}
			return entityManager
			.createQuery(
					"SELECT p FROM Person p WHERE death is not null and death > '01.01."
							+ minyear + "' and death < '01.01." + maxyear
							+ "' ORDER by random()").setMaxResults(3)
			.setHint("org.hibernate.readOnly", true)
			.getResultList();
		case 15:
			minyear = year - 100;
			maxyear = year + 100;
			return entityManager
			.createQuery(
					"SELECT p FROM Person p WHERE death is not null and death > '01.01."
							+ minyear + "' and death < '01.01." + maxyear
							+ "' ORDER by random()").setMaxResults(3)
			.setHint("org.hibernate.readOnly", true)
			.getResultList();
		}
		
		System.out.println("Fehler!");
		dist = 200;
		return entityManager
		.createQuery(
				"SELECT p FROM Person p WHERE death is not null and death < '01.01."
						+ (year - dist) + "' or death > '01.01." + (year + dist)
						+ "' ORDER by random()").setMaxResults(3)
		.setHint("org.hibernate.readOnly", true)
		.getResultList();
		
		
		
	}

	public List<Person> createFamousNonArtistList() {
		String resource = selectFamousNonArtistResource();
		return getPersonsFromCSV(resource);

	}

	public List<Person> createNonArtistList() {
		String resource = selectNonArtistResource();
		return getPersonsFromCSV(resource);

	}

	public List<Person> createEasyArtistList() {
		String resource = selectEasyArtistResource();
		return getPersonsFromCSV(resource);

	}

	private List<Person> getPersonsFromCSV(String resource) {

		List<String[]> stringList = new ArrayList<String[]>();
		String trennzeichen = ";";

		try {
			URL resourceURL = WrongAnswerBean.class.getResource(resource);
			BufferedReader in = new BufferedReader(new FileReader(new File((resourceURL.toURI()))));
			String readString;
			while ((readString = in.readLine()) != null) {
				stringList.add(readString.split(trennzeichen));
			}
			in.close();
		} catch (Exception e) {
			log.error("Could not read from file #0", e, resource);
		}
		List<Person> result = new ArrayList<Person>();
		Random r = new Random();
		int[] randInt = new int[3];
		randInt[0] = r.nextInt(stringList.size());
		randInt[1] = r.nextInt(stringList.size());
		randInt[2] = r.nextInt(stringList.size());
		while(randInt[0]==randInt[1]){
			randInt[1] = r.nextInt(stringList.size());
		}
		while(randInt[1]==randInt[2] || randInt[0]==randInt[2]){
			randInt[2] = r.nextInt(stringList.size());
		}
		
		for (int i = 0; i < 3; ++i) {
			
			int tmp = randInt[i];
			String[] artistString = stringList.get(tmp);

			Person p = new Person();
			p.setForename(artistString[0]);
			p.setSurname(artistString[1]);
			Date d;
			d = new Date(2012, 1, 1);
			p.setDeath(d);
			if(artistString.length == 3){
				try{
					d = new Date(Integer.parseInt(artistString[2])-1900, 1, 1);
					p.setDeath(d);
				}catch(Exception e){
					e.printStackTrace();
					p.setDeath(new Date(1900,1,1));
				}
				
			}
			result.add(p);
		}
		return result;
	}

	private String selectFamousNonArtistResource() {
		float x = (float) Math.random();

		if (x < 0.2) {
			return pre + "Sportler.csv";
		} else if (x < 0.4) {
			return pre + "Film.csv";
		} else if (x < 0.6) {
			return pre + "Beruehmt.csv";
		} else if (x < 0.8) {
			return pre + "Komiker.csv";
		} else {
			return pre + "Musiker.csv";
		}

	}

	private String selectNonArtistResource() {

		return pre + "Top200.csv";

	}

	private String selectEasyArtistResource() {
		return pre + "Maler.csv";

	}

	public List<Person> createWrongAnswers(int level, int year) {
		try{
			switch (level) {
			case 1:
				return createFamousNonArtistList();
			case 2:
				return createFamousNonArtistList();
			case 3:
				return createFamousNonArtistList();
			case 4:
				return createNonArtistList();
			case 5:
				return createNonArtistList();
			case 6:
				return createEasyArtistList();
			case 7:
				return createEasyArtistList();
			case 8:
				return createEasyArtistList();
			case 9:
				return createEasyArtistList();
			case 10:
				return createArtistList(10, year);
			case 11:
				return createArtistList(11, year);
			case 12:
				return createArtistList(12, year);
			case 13:
				return createArtistList(13, year);
			case 14:
				return createArtistList(14, year);
			case 15:
				return createArtistList(15, year);
			}
		}catch(Exception e){
			//error, probably files not found
			log.equals("Error while reading CSV-Files in Folder /quiz/");
			return createArtistList(10, year);
		}
		
		
		return null;
	}
}
