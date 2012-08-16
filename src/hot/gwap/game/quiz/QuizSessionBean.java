/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.game.quiz;

import gwap.game.quiz.tools.QuizQuestionBean;
import gwap.model.Person;
import gwap.model.resource.ArtResource;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * This is the backing bean for one game session. It handles all actions that
 * can be executed during a game session. The game session itself is organized
 * in a business process.
 * 
 * @author Jonas Hoelzler
 */

@Name("quizSessionBean")
@Scope(ScopeType.CONVERSATION)
public class QuizSessionBean implements Serializable {

	@Create
	public void init() {
		log.info("Creating");
	}

	@Destroy
	public void destroy() {
		log.info("Destroying");
	}

	@Logger
	protected Log log;
	@In
	protected FacesMessages facesMessages;
	@In
	protected EntityManager entityManager;
	@In(create = true)
	protected Person person;

	private static final long serialVersionUID = 1L;
	private ArrayList<QuizQuestionBean> questions;
	private JSONObject jsonResult;

	@Factory(value = "quizSession", scope = ScopeType.EVENT)
	public QuizSessionBean createQuizSession() {

		return this;
	}

	/**
	 * Creates a new game array for the Quiz Game with 15 images
	 */
	private boolean createWoelfflinResource() {

		this.questions = new ArrayList<QuizQuestionBean>(15);
		ArtResource[] artResources = new ArtResource[15];

		Query query = entityManager.createNamedQuery("artResource.woelfflin");
		query.setMaxResults(500);
		List<ArtResource> resultList = (List<ArtResource>) query
				.getResultList();

		int numImagesFound = 0;

		int listCounter = 0;
		for (ArtResource a : resultList) {
			listCounter++;
			String dateCreated = validateYear(a.getDateCreated());
			if (dateCreated != null) {
				String forename = a.getArtist().getForename();
				String surname = a.getArtist().getSurname();
				if (!(forename == null && surname == null)) {
					a.setDateCreated(dateCreated);
					QuizQuestionBean q = new QuizQuestionBean(numImagesFound, a);
					q.generateAnswers();
					questions.add(q);
					numImagesFound++;
					if (numImagesFound == 15) {
						log.info("Found valid quiz game setup after observing " + listCounter + " ArtResources");
						return true;
						
					}
					

				}
			}

		}
		log.error("Did not find 15 valid ArtResources out of 500 for a valid quiz game setup!");
		return false;

	}

	/**
	 * 
	 * @param dateCreated
	 *            The date created from DB
	 * @return The year created if conversion succeeded
	 */
	private String validateYear(String dateCreated) {
		try {
			int intYear = Integer.parseInt(dateCreated.substring(0, 4));
			if (intYear > 1420 && intYear < 1800) {
				return "" + intYear;
			} else {
				return null;
			}
		} catch (NumberFormatException e) {
			return null;
		}

	}

	public QuizQuestionBean getQuizQuestion(int questionNumber) {
		return questions.get(questionNumber);

	}

	public JSONObject getJSONResult() {

		boolean imageFound = createWoelfflinResource();
		if (!imageFound) {
			// retry
			imageFound = createWoelfflinResource();
			if (!imageFound) {
				log.error("Could not find 15 valid images for quiz game setup");
			}
		}

		JSONArray gameArray = new JSONArray();

		for (int i = 0; i < 15; ++i) {
			gameArray.add(questions.get(i).generateJSONObject());
		}

		this.jsonResult = new JSONObject();
		jsonResult.put("Array", gameArray);
		return this.jsonResult;
	}

}
