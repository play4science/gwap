/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
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
