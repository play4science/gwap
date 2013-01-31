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

import gwap.game.quiz.tools.QuizQuestionBean;
import gwap.model.Person;
import gwap.model.action.PerceptionPair;

import java.util.Date;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.log.Log;

/**
 * Gives a recommendation for the answers i.e. Answer A: 50% Answer B: 25% =
 * AnswerC AnswerD=0%
 * 
 * @author Jonas Hoelzler
 */

@Name("ratingEvaluator")
@Scope(ScopeType.APPLICATION)
public class RatingEvaluator {

	private PerceptionPair[] userPairList;
	int VOLLER_BONUS = 1;
	int HALBER_BONUS = 0;
	int KEIN_BONUS = -1;

	@Create
	public void init() {
		log.info("Creating");
	}

	@Destroy
	public void destroy() {
		log.info("Destroying");
	}

	@Logger
	private Log log;
	@In
	private FacesMessages facesMessages;
	@In
	private EntityManager entityManager;
	@In
	private LocaleSelector localeSelector;

	/**
	 * Calculates the recommandation for feedback: Answer A, ANswer B, Answer C,
	 * Answer D
	 * 
	 * @param question
	 * @param userPerceptionRating
	 * @param avgUserRating
	 * @return int[4]
	 */
	public int[] calculateRecommendationForFeedbackJoker(
			QuizQuestionBean question,
			UserPerceptionRating userPerceptionRating, int[] avgUserRating) {
		this.userPairList = userPerceptionRating.getPairs();

		if (avgUserRating[0] == -1) {
			int[] algorithmicRating = calcEstimatedWoelfflinValue(question);
			return algorithmicRating;
		} else {
			return avgUserRating;
		}

	}

	public int[] calcAvgUserRating(QuizQuestionBean question) {
		int[] rating = new int[5];
		String[] s = new String[] { "LinearVsMalerisch", "VielheitVsEinheit",
				"FlaecheVsTiefe", "KlarheitVsUnklarheitUndBewegtheit",
				"GeschlossenVsOffen" };

		for (int i = 0; i < 5; ++i) {
			String pairname = s[i];
			Query query = entityManager
					.createNamedQuery("PerceptionPair.averageRatingByResourceAndPairname");
			query.setParameter("resource", question.getArtResource());
			query.setParameter("pairname", pairname);
			try {
				rating[i] = ((Double) query.getSingleResult()).intValue();
			} catch (NullPointerException e) {
				rating[i] = -1;
			}

		}

		return rating;
	}

	/*
	 * calculates the Woelfflin guess value (0,0,0,0,0) for year < 1480
	 * (100,100,100,100,100) for year > 1680 linear interpolation between 1480
	 * and 1680
	 */
	private int[] calcEstimatedWoelfflinValue(QuizQuestionBean question) {
		int questionNum = question.getQuestionNumber();
		assert (questionNum > 0 && questionNum <= 15);
		if (questionNum <= 5) {
			return generateRandomRecommendation(question);
		} else {
			int[] algorithmicRating = generateRealRecommendation(question);
			// now calculate distances using norm
			int[] distances = new int[4];

			for (int i = 0; i < 4; ++i) {
				distances[i] = calculateDistance(new int[]{algorithmicRating[i],algorithmicRating[i],algorithmicRating[i],algorithmicRating[i],algorithmicRating[i]});

			}

			// now all four distances are between 0 and 100
			// we want to return the overall probabilty
			// i.e. the p(A) + p(B) + p(C) + p(D) = 1
			float denom = distances[0] + distances[1] + distances[2]
					+ distances[3];
			int[] result = new int[4];
			result[0] = (int) (distances[0] / denom * 100);
			result[1] = (int) (distances[1] / denom * 100);
			result[2] = (int) (distances[2] / denom * 100);
			result[3] = (int) (distances[3] / denom * 100);

			return result;
		}

	}

	/**
	 * 
	 * @param rating Array[5]
	 * @return The Woelfflin Distance
	 */
	private int calculateDistance(int[] rating) {
		Float distance;
		if (rating[0] < 0) {
			distance = new Random().nextFloat() * 100f;
		} else {

			distance = 0f;
			for (int i = 0; i < 5; ++i) {
				// for (PerceptionPair p : userPairList) {
				distance += Math.abs(userPairList[i].getValue() - rating[i]) / 100.0f;
			}
			distance = distance / 5.0f;
			distance = distance * 100.0f;
		}
		return distance.intValue();
	}

	private int[] generateRealRecommendation(QuizQuestionBean question) {
		Person[] persons = question.getAnswers();
		int[] algorithmicRating = new int[4];
		for (int i = 0; i < 4; ++i) {
			algorithmicRating[i] = calcWoelfflinValue(persons[i], question);
		}
		return algorithmicRating;
	}

	/**
	 * 
	 * @param person
	 * @param question
	 * @return Calculates the Woelfflin Value i.e. value between 0 and 100
	 */
	private int calcWoelfflinValue(Person person, QuizQuestionBean question) {
		Float algorithmicRating;
		int year = estimateYearForPerson(person, question);

		if (year < 1420) {
			return -1;
		} else if (year < 1510) {
			algorithmicRating = (year - 1420f) / (1510f - 1420f);
		} else if (year < 1600) {
			algorithmicRating = 1 - ((year - 1510f) / (1600f - 1510f));
		} else if (year < 1680) {
			algorithmicRating = (year - 1600f) / (1680f - 1600f);
		} else if (year < 1760) {
			algorithmicRating = 1f;
		} else if (year < 1800) {
			algorithmicRating = 1 - ((year - 1760f) / (1800f - 1760f));
		} else {
			return -1;
		}

		Float tmp = (algorithmicRating * 100f);

		return tmp.intValue();

	}

	/*
	 * estimates the year for a person
	 */
	private int estimateYearForPerson(Person person, QuizQuestionBean question) {
		Date d = person.getDeath();

		int year;
		if (d == null) {
			try {
				year = Integer.parseInt(question.getArtResource()
						.getDateCreated());
			} catch (NumberFormatException e) {
				year = 1600;
			}
		} else {
			year = d.getYear() + 1900 - 30;
		}
		return year;
	}

	private int[] generateRandomRecommendation(QuizQuestionBean question) {

		int[] result = new int[4];
		int questionNum = question.getQuestionNumber();
		assert (questionNum > 0);
		int correctAnswerPos = question.getCorrectAnswerPos();
		Random R = new Random();

		int sum = 0;
		for (int i = 0; i < result.length; ++i) {
			if (i != correctAnswerPos) {
				result[i] = R.nextInt(questionNum * 5);
				sum += result[i];
			}
		}
		result[correctAnswerPos] = 100 - sum;

		return result;

	}

	/**
	 * 
	 * @param quizQuestion
	 * @param recommendedRating
	 * @param avgUserRating Array[5]
	 * @return Calculates the bonus: Full/Half/No Bonus
	 */
	public int calculateBonus(QuizQuestionBean quizQuestion,
			UserPerceptionRating recommendedRating, int[] avgUserRating) {

		Person correctPerson = quizQuestion.getCorrectAnswer();

		float distance;
		if (avgUserRating[0] < 0) {
			int[] algorithmicRating = new int[5];
			for (int i = 0; i < 5; ++i) {
				algorithmicRating[i] = calcWoelfflinValue(correctPerson,
						quizQuestion);
			}
			distance = calculateDistance(algorithmicRating);
		} else {
			distance = calculateDistance(avgUserRating);
		}
		log.info("Distance: "+ distance);
		if (distance < 25) {
			return VOLLER_BONUS;
		} else if (distance < 40) {
			return HALBER_BONUS;
		} else {
			return KEIN_BONUS;
		}

	}

}
