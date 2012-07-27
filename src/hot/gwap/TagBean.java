/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap;

import gwap.model.Tag;
import gwap.model.resource.Resource;

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
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Redirect;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.log.Log;

/**
 * @author Christoph Wieser
 * 
 */

@Name("tagBean")
@Scope(ScopeType.CONVERSATION)
public class TagBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Create  public void init()    { log.info("Creating");   }
	@Destroy public void destroy() { log.info("Destroying"); }

	@Logger                        private Log log;
	@In                            private FacesMessages facesMessages;
	@In                            private EntityManager entityManager;
	@In                            private LocaleSelector localeSelector;

	@In(required=true,create=true)           private Resource resource;
	@Out(required=true,scope=ScopeType.PAGE) private Tag tagResource;
	
	@Factory("tagResource")
	public void updateTag() {
		
		String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
		if (viewId.equals("/tagATag.xhtml")) {
			updateRandomTagForResource();
		}
			
		// Finally, if there is no tag available
		if (resource == null) {
			facesMessages.add("#messages['game.notEnoughPlayersOnline']");
			redirect("/home.xhtml");
		}
		
		log.info("Creating TagResource '#0'", tagResource.getName());
	}
	
	private void redirect(String viewId) {
		// Redirect
		Conversation.instance().endBeforeRedirect();
		Redirect redirect = Redirect.instance();
		redirect.setViewId(viewId);
		redirect.execute();
	}
	
	public void updateRandomTagForResource() {
		log.info("Updating Random Tag for Resource");
		
		try {
			Query query1 = entityManager.createNamedQuery("tag.byResource");
			query1.setParameter("resource", resource);
			query1.setParameter("language", localeSelector.getLanguage());			
			query1.setParameter("minOccurrence", 2L);
			query1.setMaxResults(1);			
			Long tagId = (Long) query1.getSingleResult();
			
			Query query2 = entityManager.createNamedQuery("tag.byId");
			query2.setParameter("id", tagId);
			this.tagResource = (Tag) query2.getSingleResult();
		} catch(Exception e) {
			facesMessages.add("#{messages['general.noResource']}");
		}
	}
}
