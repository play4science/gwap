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

import javax.faces.context.FacesContext;
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
	private QuizQuestionBean[] questions;
	private JSONObject jsonResult;

	@Factory(value = "quizSession", scope = ScopeType.EVENT)
	public QuizSessionBean createQuizSession() {

		return this;
	}

	private void createWoelfflinResource() {
		log.info("Updating Woelfflin Resource");

		this.questions = new QuizQuestionBean[15];
		ArtResource[] artResources = new ArtResource[15];
		for (int i = 0; i < 15; ++i) {
			// Resources with Desciptions
			try {
				boolean imageFound = false;

				while (!imageFound) {
					Query query = entityManager
							.createNamedQuery("artResource.woelfflin");
					query.setMaxResults(1);
					artResources[i] = (ArtResource) query.getSingleResult();

					String dateCreated = validateYear(artResources[i]
							.getDateCreated());

					String forename = artResources[i].getArtist().getForename();
					String surname = artResources[i].getArtist().getSurname();
					if(!(forename == null && surname == null)){
						if (dateCreated != null) {

							artResources[i].setDateCreated(dateCreated);
							imageFound = true;
						}
					}
				
					// for(Tagging t: artResources[i].getTaggings()){
					// if(t.getTag().getName().startsWith("PORT")){
					// imageFound = false;
					// }
					// }
				}

				this.questions[i] = new QuizQuestionBean(i, artResources[i]);
				questions[i].generateAnswers();

			} catch (Exception e) {
				facesMessages.add("#{messages['general.noResource']}");
			}
		}

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
		return questions[questionNumber];

	}

	public JSONObject getJSONResult() {
		String viewId = FacesContext.getCurrentInstance().getViewRoot()
				.getViewId();
		createWoelfflinResource();

		JSONArray gameArray = new JSONArray();

		for (int i = 0; i < 15; ++i) {
			gameArray.add(questions[i].generateJSONObject());
		}

		this.jsonResult = new JSONObject();
		jsonResult.put("Array", gameArray);
		return this.jsonResult;
	}

}
