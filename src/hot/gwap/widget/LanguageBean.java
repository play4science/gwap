/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.widget;

import gwap.game.GameSessionBean;
import gwap.game.GameSessionBeanNew;
import gwap.model.Person;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.international.LocaleSelector;

@Name("languageBean")
@Scope(ScopeType.STATELESS)
public class LanguageBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@In                      private LocaleSelector localeSelector;
	@In                      private EntityManager entityManager;
	@In(required=false)@Out(required=false)
	                         private Person person;
	@In(required=false)      private GameSessionBean gameSessionBean;
	@In(required=false)      private GameSessionBeanNew gameSessionBeanNew;
	
	@RequestParameter		private String language;

	public void setLanguage() {
		setLanguage(language);
	}
	
	public void setLanguage(String language) {
		localeSelector.setLanguage(language);
		if (person != null) {
			person = entityManager.find(Person.class, person.getId());
			person.setLanguage(language);
		}
	}
	
	public boolean isLanguageChangeDisabled() {
		return gameSessionBean != null || gameSessionBeanNew != null;
	}
	
	public String getClass(String language) {
		return localeSelector.getLanguage().equals(language) ? "active" : "inactive";
	}
}
