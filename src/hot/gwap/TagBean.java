/*
 * This file is part of gwap, an open platform for games with a purpose
 *
 * Copyright (C) 2013
 * Project play4science
 * Lehr- und Forschungseinheit für Programmier- und Modellierungssprachen
 * Ludwig-Maximilians-Universität München
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
