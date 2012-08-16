/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.game.quiz;

import gwap.game.quiz.tools.FacesContextBuilder;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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

/**
 * This servlet provides a new Quiz Game for the PlayN-HTML5-Interface by
 * returning a HttpServletRespone in JSON-Format
 * 
 * @author Jonas Hoelzler
 * 
 */
@Startup
@Scope(ScopeType.APPLICATION)
@Name("playNCommunicationResource")
@BypassInterceptors
public class PlayNCommunicationResource extends AbstractResource {
	@Logger
	private Log logger;

	private HttpServletRequest request;

	private HttpServletResponse response;

	private ExpressionFactory elFactory;

	private ELContext elc;

	private HttpSession ses;

	private QuizSessionBean quizSessionBean;

	@Override
	public String getResourcePath() {
		return "/quiz";
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

		// HttpSession session = request.getSession();
		this.request = request;
		this.response = response;

		ses = request.getSession();
		SessionTracker.instance().add(ses);

		// }

		// give it ten trys to find a valid game configuration
		for (int ii = 0; ii < 10; ++ii) {
			JSONObject jsonObject = createJSONObjectForNewGame();
			if (jsonObject != null) {

				jsonObject.put("SID", ses.getId());
				InputStream instream = null;
				OutputStream outstream = null;

				try {
					instream = request.getInputStream();
					response.setContentType("text/plain");
					outstream = response.getOutputStream();
					BufferedWriter out = new BufferedWriter(
							new OutputStreamWriter(outstream));
					jsonObject.writeJSONString(out);
					out.flush();
					outstream.flush();
					outstream.close();

					instream.close();
					logger.info("Successfully initialized game");
					break;
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				logger.info("Couldn't initialize a game in try no." + ii);
			}

		}

	}

	/**
	 * Sets up a new Quiz Game Session
	 * 
	 * @return gameArray Array for JSON
	 */
	private JSONObject createJSONObjectForNewGame() {

		try {
			/*
			 * setting up dummy JSF FacesContext
			 */
			Transaction.instance().begin();
			// Conversation.instance().begin();
			FacesContext facesContext = new FacesContextBuilder()
					.getFacesContext(request, response, request.getSession());
			this.elc = facesContext.getELContext();

			this.elFactory = facesContext.getApplication()
					.getExpressionFactory();

			ValueExpression mexp = elFactory.createValueExpression(elc,
					"#{quizSession}", QuizSessionBean.class);
			this.quizSessionBean = (QuizSessionBean) mexp.getValue(elc);
			ses.setAttribute("quizSession", quizSessionBean);

			JSONObject jsonResult = quizSessionBean.getJSONResult();

			facesContext.release();
			Transaction.instance().commit();

			return jsonResult;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}