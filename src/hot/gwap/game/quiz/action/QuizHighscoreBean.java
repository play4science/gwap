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

package gwap.game.quiz.action;

import gwap.model.QuizHighscore;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

@Name("quizHighscoreBean")
@Scope(ScopeType.APPLICATION)
public class QuizHighscoreBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Create
	public void init() {
		log.info("Creating new game");
	}

	@Destroy
	public void destroy() {
		log.info("Destroying");
	}

	@Logger
	private Log log;
//	@In
//	private FacesMessages facesMessages;
	@In(create = true)
	private EntityManager entityManager;
//	@In
//	private LocaleSelector localeSelector;

	public List<QuizHighscore> getHighScoresAllTime() {
		Query query = entityManager.createNamedQuery("quizHighscore.allTime");
		query.setMaxResults(100);
		// QuizHighscore[] highscores = (QuizHighscore[]) query
		// .getResultList().toArray();
		List<QuizHighscore> highscores2 = (List<QuizHighscore>) query.getResultList();;
		return highscores2;
	}

	public List<QuizHighscore> getHighScoresThisWeek() {
		Query query = entityManager.createNamedQuery("quizHighscore.thisWeek");
		query.setMaxResults(100);
		// QuizHighscore[] highscores = (QuizHighscore[]) query
		// .getResultList().toArray();

		List<QuizHighscore> highscores2 = (List<QuizHighscore>) query.getResultList();
		return highscores2;
	}

	public void addHighscore(QuizHighscore quizHighscore) {
		entityManager.persist(quizHighscore);
		entityManager.flush();

	}

	public int getPlaceAllTime(int score) {
		Query query = entityManager.createNamedQuery(
				"quizHighscore.placeAllTime").setParameter("score", score);
		int placeAllTime = ((Long) query.getSingleResult()).intValue();
		return placeAllTime;
	}

	public int getPlaceThisWeek(int score) {
		Query query = entityManager.createNamedQuery(
				"quizHighscore.placeThisWeek").setParameter("score", score);
		int placeThisWeek = ((Long) query.getSingleResult()).intValue();
		return placeThisWeek;
	}

}
