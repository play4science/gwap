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

package gwap.game.quiz.tools;

import gwap.game.quiz.WrongAnswerBean;
import gwap.model.Person;
import gwap.model.Tag;
import gwap.model.action.Tagging;
import gwap.model.resource.ArtResource;
import gwap.model.resource.ArtResourceTeaser;
import gwap.tools.QuizGermanStemmer;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.log.Log;
import org.json.simple.JSONObject;

/**
 * @author Jonas Hoelzler
 */
public class QuizQuestionBean {

	private ArtResource artResource;
	private JSONObject jsonObject;
	private Person answerA;
	private Person answerB;
	private Person answerC;
	private Person answerD;
	private Person correctAnswer;
	private int questionNumber;
	private int correctAnswerPos;
	@Logger
	private static Log logger;

	public QuizQuestionBean(int id, ArtResource artResource) {
		this.artResource = artResource;
		this.setQuestionNumber(id + 1);
	}

	public String getPath() {
		return artResource.getPath();
	}

	public String getUrl() {
		return artResource.getUrl();
	}

	public Object getArtistName() {
		return artResource.getArtistName();
	}

	public void generateAnswers() {
		correctAnswer = artResource.getArtist();
		List<Person> wrongAnswers = getWrongAnswers();

		for (Person p : wrongAnswers) {
			if (p == null) {
				logger.error("Ein Kuenstler ist null!");
			}
		}
		if (correctAnswer == null) {
			logger.error("Ein Kuenstler (coorectAnswer) ist null!");
		}

		Random R = new Random();
		int randomNum = R.nextInt(4);
		if (randomNum == 0) {
			answerA = correctAnswer;
			answerB = wrongAnswers.get(0);
			answerC = wrongAnswers.get(1);
			answerD = wrongAnswers.get(2);
			setCorrectAnswerPos(0);
		} else if (randomNum == 1) {
			answerA = wrongAnswers.get(0);
			answerB = correctAnswer;
			answerC = wrongAnswers.get(1);
			answerD = wrongAnswers.get(2);
			setCorrectAnswerPos(1);
		} else if (randomNum == 2) {
			answerA = wrongAnswers.get(0);
			answerB = wrongAnswers.get(1);
			answerC = correctAnswer;
			answerD = wrongAnswers.get(2);
			setCorrectAnswerPos(2);
		} else {
			answerA = wrongAnswers.get(0);
			answerB = wrongAnswers.get(1);
			answerC = wrongAnswers.get(2);
			answerD = correctAnswer;
			setCorrectAnswerPos(3);
		}

	}

	/*
	 * create a new JSON Object from scratch
	 */
	@SuppressWarnings("unchecked")
	public JSONObject generateJSONObject() {
		jsonObject = new JSONObject();
		jsonObject.put("Title", QuizGermanStemmer.stemText(artResource.getDefaultTitle()));

		jsonObject.put("Teaser", QuizGermanStemmer.stemText(getTeaser(artResource)));

		if (artResource.getLocation() != null) {
			jsonObject.put("Location", QuizGermanStemmer.stemText(artResource.getLocation()));
		} else {
			jsonObject.put("Location", "");
		}

		if (artResource.getDateCreated() != null) {
			jsonObject.put("Datierung", artResource.getDateCreated());
		} else {
			jsonObject.put("Datierung", "");
		}

		if (artResource.getInstitution() != null) {
			jsonObject.put("Institution",
					QuizGermanStemmer.stemText(artResource.getInstitution()));
		} else {
			jsonObject.put("Institution", "");
		}

		HashMap<String, Integer> taggings = cleanUpAndGetTaggins(artResource
				.getTaggings());
		jsonObject.put("NumTags", taggings.size());

		int ii = 0;
		for (Entry<String, Integer> s : taggings.entrySet()) {
			if (s.getValue() > 1) {
				jsonObject.put("Tag" + ii, QuizGermanStemmer.stemText(s.getKey()));
				jsonObject.put("TagNum" + ii, s.getValue());
			}

			ii++;
		}

		jsonObject.put("A", QuizGermanStemmer.stem(answerA));
		jsonObject.put("B", QuizGermanStemmer.stem(answerB));
		jsonObject.put("C", QuizGermanStemmer.stem(answerC));
		jsonObject.put("D", QuizGermanStemmer.stem(answerD));
		jsonObject.put("CorrectAnswer", QuizGermanStemmer.stem(correctAnswer));

		if (answerA.getDeath() != null) {
			jsonObject.put("DA", answerA.getDeath().getYear() + 1900);
		} else {
			jsonObject.put("DA",
					Integer.parseInt(artResource.getDateCreated()) + 30);
		}
		if (answerB.getDeath() != null) {
			jsonObject.put("DB", answerB.getDeath().getYear() + 1900);
		} else {
			jsonObject.put("DB",
					Integer.parseInt(artResource.getDateCreated()) + 30);
		}
		if (answerC.getDeath() != null) {
			jsonObject.put("DC", answerC.getDeath().getYear() + 1900);
		} else {
			jsonObject.put("DC",
					Integer.parseInt(artResource.getDateCreated()) + 30);
		}
		if (answerD.getDeath() != null) {
			jsonObject.put("DD", answerD.getDeath().getYear() + 1900);
		} else {
			jsonObject.put("DD",
					Integer.parseInt(artResource.getDateCreated()) + 30);
		}

		jsonObject.put("URL", artResource.getUrl().split("image/")[1]);

		return this.jsonObject;
	}

	private HashMap<String, Integer> cleanUpAndGetTaggins(Set<Tagging> taggings) {

		HashMap<String, Integer> tagMap = new HashMap<String, Integer>();

		for (Tagging tagging : taggings) {

			Tag tag = tagging.getTag();
			if (tag != null && tag.getLanguage() != null
					&& tag.getBlacklisted() != null) {
				if (!tag.getBlacklisted() && tag.getLanguage().equals("de")) {
					String name = tag.getName();
					if (tagMap.containsKey(name)) {
						tagMap.put(name, tagMap.get(name) + 1);
					} else {
						tagMap.put(tag.getName(), 1);
					}

				}

			}

		}

		return tagMap;
	}

	private String getTeaser(ArtResource artResource) {
		List<ArtResourceTeaser> teaser = artResource.getTeasers();
		if (teaser != null) {
			for (ArtResourceTeaser t : teaser) {
				if (t.getLanguage().equals("de")) {
					String text = t.getDescription();
					if (text != null
							&& (artResource.getArtist().getForename() == null || !text.contains(artResource.getArtist().getForename()))
							&& (artResource.getArtist().getSurname() == null  || !text.contains(artResource.getArtist().getSurname())) ) {
						return text;
					}
				}
			}
		}

		return "";
	}

	private List<Person> getWrongAnswers() {
		int level = getQuestionNumber();

		WrongAnswerBean wrongAnswerBean = (WrongAnswerBean) Component
				.getInstance("wrongAnswerBean");
		int year;
		if (correctAnswer.getDeath() != null) {
			year = correctAnswer.getDeath().getYear() + 1900;
		} else {
			year = Integer.parseInt(artResource.getDateCreated());
			year = year + 30;
		}

		return wrongAnswerBean.createWrongAnswers(level, year);

	}

	public Person[] getAnswers() {
		return new Person[] { answerA, answerB, answerC, answerD };
	}

	public void setQuestionNumber(int questionNumber) {
		this.questionNumber = questionNumber;
	}

	public int getQuestionNumber() {
		return questionNumber;
	}

	public void setCorrectAnswerPos(int correctAnswerPos) {
		this.correctAnswerPos = correctAnswerPos;
	}

	public Person getCorrectAnswer() {
		Person[] p = new Person[] { answerA, answerB, answerC, answerD };
		return p[correctAnswerPos];
	}

	public int getCorrectAnswerPos() {
		return correctAnswerPos;
	}

	public ArtResource getArtResource() {
		return artResource;
	}

}
