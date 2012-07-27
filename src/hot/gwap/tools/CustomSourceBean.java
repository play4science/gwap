/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.tools;

import gwap.model.Source;

import java.io.Serializable;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.faces.Redirect;
import org.jboss.seam.log.Log;

/**
 * Define custom layouts for e.g. Kunsthalle Karlsruhe which have a specific image source
 * 
 * @author Fabian Kneißl
 */
@Name("customSourceBean")
@Scope(ScopeType.SESSION)
public class CustomSourceBean implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@In
	EntityManager entityManager;
	
	@Logger
	Log log;
	
	@In(required=false)@Out(required=false)
	Source customSource;
	
	public void setSource(String sourceName) {
		Query query = entityManager.createNamedQuery("source.byName");
		query.setParameter("name", sourceName);
		try {
			customSource = (Source) query.getSingleResult();
			log.info("Custom source is now #0 (#1)", customSource.getId(), customSource.getName());
		} catch (NoResultException e) {
			customSource = null;
			log.info("Custom source could not be set to #0", sourceName);
		}
		log.info("custom source:" + sourceName);
	}
	
	public void reset() {
		customSource = null;
		log.info("reset custom source");
		try {
			String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
			Conversation.instance().endBeforeRedirect();
			Redirect redirect = Redirect.instance();
			redirect.setViewId(viewId);
			redirect.setConversationPropagationEnabled(false);
			redirect.execute();
		} catch (Throwable t) { }
	}
	
	public boolean getCustomized() {
		return customSource != null;
	}

	public Query query(String namedQuery) {
		Query query;
		if (getCustomized()) {
			query = entityManager.createNamedQuery(namedQuery + "Custom");
			query.setParameter("source", customSource);
		} else {
			query = entityManager.createNamedQuery(namedQuery);
		}
		return query;
	}

	public Source getCustomSource() {
		return customSource;
	}
	
}
