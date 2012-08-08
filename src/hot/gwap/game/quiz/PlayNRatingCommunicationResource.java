/*
# * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.game.quiz;

import gwap.game.quiz.action.PerceptionBean;
import gwap.game.quiz.tools.QuizQuestionBean;
import gwap.model.PerceptionPair;
import gwap.model.action.PerceptionRating;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.Log;
import org.jboss.seam.servlet.ContextualHttpServletRequest;
import org.jboss.seam.transaction.Transaction;
import org.jboss.seam.web.AbstractResource;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * This servlet handles the ratings provided from the PlayN-Interface and
 * provides a feedback by returning a HttpServletRespone in JSON-Format
 * 
 * @author Jonas Hoelzler
 * 
 */
@Startup
@Scope(ScopeType.APPLICATION)
@Name("playNRatingCommunicationResource")
@BypassInterceptors
public class PlayNRatingCommunicationResource extends AbstractResource {
	@Logger
	private Log logger;
	private HttpServletResponse response;

	private String sessionID = null;
	
	@Override
	public String getResourcePath() {
		return "/rating";
	}

	@Override
	public void getResource(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {
		new ContextualHttpServletRequest(request) {
			@Override
			public void process() throws IOException {
				doWork(request, response);
			}
		}.run();
	}

	private void doWork(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		this.response = response;
		UserPerceptionRating userPerceptionRating = readOutJSONData(request);

		try {
			logger.info("Create perception rating for resource #0, question #1 with pairs #2", 
					userPerceptionRating.getResourceId(), userPerceptionRating.getQuestionNumber(), userPerceptionRating.getPairs());

			// get Session
			HttpSession ses = SessionTracker.instance().getSession(sessionID);

			Transaction.instance().begin();

			QuizSessionBean quizSession = (QuizSessionBean) ses
					.getAttribute("quizSession");

			QuizQuestionBean quizQuestion = quizSession
					.getQuizQuestion(userPerceptionRating.getQuestionNumber());

			// create PerceptionRating
			PerceptionRating perceptionRating = new PerceptionRating();
			perceptionRating.setPerson(quizSession.person);
			perceptionRating.setCreated(new Date());
			perceptionRating.setResource(quizQuestion.getArtResource());
			perceptionRating.setFillOutTimeMs(userPerceptionRating.getFillOutTimeMs());
			userPerceptionRating.setPerceptionRating(perceptionRating);

			

			RatingEvaluator ratingEvaluator = (RatingEvaluator) Component
					.getInstance("ratingEvaluator");

			// give Feedback and calculate Bonus

			int[] avgUserRating = ratingEvaluator
					.calcAvgUserRating(quizQuestion);

			int[] feedback = ratingEvaluator
					.calculateRecommendationForFeedbackJoker(quizQuestion,
							userPerceptionRating, avgUserRating);

			int bonus = ratingEvaluator.calculateBonus(quizQuestion,
					userPerceptionRating, avgUserRating);

			JSONObject jsonObject = createJSONForFeedback(feedback, bonus);

			sendJSONObject(jsonObject);

			// save in DB
			PerceptionBean perceptionBean = (PerceptionBean) Component
					.getInstance("perceptionBean");
			perceptionBean.addUserPerceptionRating(userPerceptionRating);
			Transaction.instance().commit();
			logger.info("Created perception ratings");

		} catch (NotSupportedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SystemException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RollbackException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HeuristicMixedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HeuristicRollbackException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void sendJSONObject(JSONObject jsonObject) {
		OutputStream outstream = null;

		try {
			response.setContentType("text/plain");
			outstream = response.getOutputStream();
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					outstream));
			jsonObject.writeJSONString(out);
			out.flush();
			outstream.flush();
			outstream.close();

		} catch (IOException e) {
			System.out.println("Exception!");
		}

	}

	private JSONObject createJSONForFeedback(int[] feedback, int bonus) {
		JSONObject jsonResult = new JSONObject();
		jsonResult.put("A", feedback[0]);
		jsonResult.put("B", feedback[1]);
		jsonResult.put("C", feedback[2]);
		jsonResult.put("D", feedback[3]);
		jsonResult.put("Bonus", bonus);
		return jsonResult;
	}

	private UserPerceptionRating readOutJSONData(HttpServletRequest request)
			throws IOException {
		UserPerceptionRating userRecommendedRating = null;
		BufferedReader reader = request.getReader();
		StringBuilder sb = new StringBuilder();
		String line = reader.readLine();
		while (line != null) {
			sb.append(line + "\n");
			line = reader.readLine();
		}
		reader.close();
		String data = sb.toString();

		JSONParser parser = new JSONParser();

		userRecommendedRating = new UserPerceptionRating();

		Object obj;
		PerceptionPair[] perceptionPair = new PerceptionPair[5];
		try {
			obj = parser.parse(data);
			JSONObject jsonObject = (JSONObject) obj;

			PerceptionPair p;
			for (Object k : jsonObject.keySet()) {

				Object value = jsonObject.get(k);
				String key = (String) k;

				if (key.equals("SID")) {
					this.sessionID = (String) value;
				} else if (key.equals("QuestionNumber")) {
					userRecommendedRating.setQuestionNumber(value);
				} else if (key.equals("LinearVsMalerisch")) {
					p = new PerceptionPair();
					p.setPairname((String) k);
					p.setValue((Long) value);
					perceptionPair[0] = p;
				} else if (key.equals("FlaecheVsTiefe")) {
					p = new PerceptionPair();
					p.setPairname((String) k);
					p.setValue((Long) value);
					perceptionPair[1] = p;
				} else if (key.equals("GeschlossenVsOffen")) {
					p = new PerceptionPair();
					p.setPairname((String) k);
					p.setValue((Long) value);
					perceptionPair[2] = p;
				} else if (key.equals("VielheitVsEinheit")) {
					p = new PerceptionPair();
					p.setPairname((String) k);
					p.setValue((Long) value);
					perceptionPair[3] = p;
				} else if (key.equals("KlarheitVsUnklarheitUndBewegtheit")) {
					p = new PerceptionPair();
					p.setPairname((String) k);
					p.setValue((Long) value);
					perceptionPair[4] = p;
				}else if(key.equals("FillOutTimeMs")){
					Long v;
					// for Java
					if( value instanceof Double){
						v = ((Double) value).longValue();
					// for HTML5
					}else{
						v = (Long) value;
					}

					userRecommendedRating.setFillOutTimeMs(v);
				}

			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassCastException e){
			e.printStackTrace();
		}
		userRecommendedRating.setPairs(perceptionPair);
		return userRecommendedRating;
	}
}