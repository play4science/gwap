/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.widget;

import gwap.model.Person;

import java.io.Serializable;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Renderer;
import org.jboss.seam.log.Log;


@Name("feedbackForm")
@Scope(ScopeType.PAGE)
public class FeedbackForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Logger                  private Log log;
	@In(required=false)		 private Person person;
	@In(create=true)         private Renderer renderer;
	@In                      private FacesMessages facesMessages;
	
	private String name;
	private String email;
	private String comment;
	
	private String subject;
	private String referer;
	
	@Create
	public void onCreate() {
		if (person != null) {
			name = person.getName();
			email = person.getEmail();
		}
		subject = (String) Component.getInstance("platform");
		Map<String, String> requestHeaderMap = FacesContext.getCurrentInstance().getExternalContext().getRequestHeaderMap();
		referer = requestHeaderMap.get("referer");
	}

	public String send() {
		if (comment == null || comment.length() < 5) {
			facesMessages.addToControlFromResourceBundle("comment", "feedback.commentIsEmpty");
			return null;
		}
		log.info("Sending feedback from #0 (email #1), subject #2: #3", name, email, subject, comment);
		try {
			renderer.render("/email/feedbackForm.xhtml");
			facesMessages.add("#{messages['feedback.emailSentSuccessfully']}");
			return "home";
		} catch (Exception e) {
			facesMessages.add("#{messages['general.emailSendingFailed']}");
			log.info("Email sending failed: " + e.getMessage());
			return null;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getSubject() {
		return subject;
	}

	public String getReferer() {
		return referer;
	}
}

