/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.elearn;

import gwap.model.GameRound;
import gwap.model.resource.Term;
import gwap.wrapper.BackstageAnswer;
import gwap.wrapper.BackstageQuestion;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.log.Log;

import com.google.gson.Gson;

/**
 * @author Mislav Boras
 */
@Name("elearnBackstageIntegration")
@Path("/backstage")
public class BackstageIntegration {
	public String quizSessionId;

	@Logger                  private Log log;
	@In private EntityManager entityManager;
	
	public String getQuizSessionId() {
		return quizSessionId;
	}

	public void setQuizSessionId(String quizSessionId) {
		this.quizSessionId = quizSessionId;
	}	
	
	
	//The quizSessionId is our externalSessionId
	@GET
	@Produces("application/json")
	@Path("/results/{quizSessionId}")
	public String getResults(@PathParam("quizSessionId") String quizSessionId){
		
		
		String jsonDozentScore = null;
		String test = null;
		
		Gson gson = new Gson();
		
		HashMap<String, Object> map = new HashMap<String, Object>();

		Query q = entityManager
				.createNamedQuery("gameRound.ScoreByExternalSessionId");
		q.setParameter("externalSessionId", quizSessionId);
		List<Object[]> listDozentScore = q.getResultList();
		test = gson.toJson(listDozentScore);
		System.out.println(test);
		map.put("result", listDozentScore);
		System.out.println(map);
		jsonDozentScore = gson.toJson(map);

		log.info("dozentScore " + jsonDozentScore);

		return jsonDozentScore;

	}
	
	@GET
	@Produces("application/json")
	@Path("/results/{quizSessionId}/{externalUsername}")
	public String getRoundscoreStudent (@PathParam("quizSessionId") String quizSessionId, 
			@PathParam("externalUsername") String externalUsername ){
		
		String jsonStudentRoundScore = null;
		
		Gson gson = new Gson();
		
	try {
		List<Object[]> listStudentRoundScore = null;
		List<Object[]> listStudentAvgScore = null;
		HashMap<String, Object> map = new HashMap<String, Object>();

		
		Query q;
		
		q = entityManager.createNamedQuery("gameRound.ScoreByGameRoundStudent");
		q.setParameter("externalSessionId", quizSessionId);
		q.setParameter("externalUsername", externalUsername);
		listStudentRoundScore = q.getResultList();
		q = entityManager.createNamedQuery("gameRound.ScoreOverallStudent");
		q.setParameter("externalSessionId", quizSessionId);
		q.setParameter("externalUsername", externalUsername);
		listStudentAvgScore = q.getResultList();
		map.put("resultRound", listStudentRoundScore);
		map.put("resultAverage", listStudentAvgScore);
		
		jsonStudentRoundScore = gson.toJson(map);
		
	} catch (Throwable t) {
		log.error(t);
	}	
		
			
	
				  log.info("studentRoundScore " + jsonStudentRoundScore);

		return jsonStudentRoundScore; 
		
	}
	
	//The quizSessionId is our externalSessionId
	//this method sets the endtime of the quiz to the actual and ends the quiz
	@GET
	@Produces("application/json")
	@Path("/termina/{quizSessionId}")
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Transactional
	public String terminaQuiz (@PathParam("quizSessionId") String quizSessionId){
		log.info("stopping quiz");
		
		Query q;
		q = entityManager.createNamedQuery("gameRound.stopTermina");
		q.setParameter("externalSessionId", quizSessionId);
		List<GameRound> list = q.getResultList();
		Date date = new Date();
		
		for (GameRound o : list){
			o.setEndDate(date);
		}
		
		entityManager.flush();
		return "stop({\"status\": \"1\"});";
		
	}
	
	
	@GET
	@Produces("application/json")
	@Path("/results/topGood/{quizSessionId}/")
	public String topGood (@PathParam("quizSessionId") String quizSessionId){
		log.info("top 5 good");
		
		Query q;
		q = entityManager.createNamedQuery("term.byExternalSessionId");
		q.setParameter("externalSessionId", quizSessionId);
		List<Term> termList = q.getResultList();
		HashMap<Integer, Object> result = new HashMap<Integer, Object>();
		HashMap<String, Object> jsonResult = new HashMap<String, Object>();
		Integer roundNumber = 0;
		
		for(Term term : termList){
			roundNumber++;
			q = entityManager.createNamedQuery("tagging.topCorrectAnswers");
			q.setParameter("externalSessionId", quizSessionId);
			q.setParameter("resourceId", term.getId());
			q.setMaxResults(5);
			List<BackstageAnswer> list2 = q.getResultList();
			BackstageQuestion bq = new BackstageQuestion(term.getTag().getName(), list2);
			result.put(roundNumber, bq);
		}
		jsonResult.put("result", result);
		
		String json = new Gson().toJson(jsonResult);
		return json;
	}
	
	@GET
	@Produces("application/json")
	@Path("/results/topBad/{quizSessionId}/")
	public String topBad (@PathParam("quizSessionId") String quizSessionId){
		log.info("top 5 bad");
		
		Query q;
		q = entityManager.createNamedQuery("term.byExternalSessionId");
		q.setParameter("externalSessionId", quizSessionId);
		List<Term> termList = q.getResultList();
		HashMap<Integer, Object> result = new HashMap<Integer, Object>();
		HashMap<String, Object> jsonResult = new HashMap<String, Object>();
		Integer roundNumber = 0;
		
		for(Term term : termList){
			roundNumber++;
			q = entityManager.createNamedQuery("tagging.topWrongAnswers");
			q.setParameter("externalSessionId", quizSessionId);
			q.setParameter("resourceId", term.getId());
			q.setMaxResults(5);
			List<BackstageAnswer> list2 = q.getResultList();
			BackstageQuestion bq = new BackstageQuestion(term.getTag().getName(), list2);
			result.put(roundNumber, bq);
		}
		jsonResult.put("result",result);
		
		String json = new Gson().toJson(jsonResult);
		return json;
	}
	
	
}
