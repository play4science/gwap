/*
 * This file is part of gwap
 *
 * Copyright (C) 2013
 * Project play4science
 * Lehr- und Forschungseinheit für Programmier- und Modellierungssprachen
 * Ludwig-Maximilians-Universität München
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package gwap;

import gwap.model.resource.ArtResource;
import gwap.tools.ArtResourceCacheBean;
import gwap.tools.CustomSourceBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

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
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Redirect;
import org.jboss.seam.log.Log;

@Name("resourceBean")
@Scope(ScopeType.CONVERSATION)
public class ResourceBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Create  public void init()    { log.info("Creating");   }
	@Destroy public void destroy() { log.info("Destroying"); }

	@Logger                  private Log log;
	@In                      private FacesMessages facesMessages;
	@In                      private EntityManager entityManager;
	@In(create=true)         private CustomSourceBean customSourceBean;
	@In(create=true)         private ArtResourceCacheBean artResourceSearchCacheBean;
	@In(create=true)         private ArtResourceCacheBean artResourceDatabaseCacheBean;
	@In(required=false)
	@Out(required=false)     private ArtResource resource;
	
	private Long resourceId;
	
	/**
	 *  Choose resources depending on the context
	 *  
	 *  Make sure, that a resource cannot be played more than once by a player!
	 */
	@Factory("resource")
	@Observer("updateResource")
	public void updateResource() {
		String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
		if (viewId.equals("/home.xhtml") || viewId.startsWith("/custom/")) {
			dailyResource();
			if (resource == null) // Fallback option if no images with teasers exist
				updateRandomResource();
		} else if (viewId.equals("/tagging.xhtml")) {
			if (resourceId == null) {
				updateLeastTaggedResource();
			} else {
				updateResourceById(resourceId);
				resourceId = null;
			}
		} else if (viewId.equals("/taggingGame.xhtml") || viewId.equals("/tabooTaggingGame.xhtml")) {
			updateAtLeastTaggedResource();
		} else if (viewId.equals("/tagATag.xhtml")) {
			updateAtLeastTaggedResource();
		} else if (viewId.equals("/combino.xhtml")) {
			updateAtLeastTaggedForCombinoResource();
		} else if (viewId.equals("/virtualTagging.xhtml")) {
			if (resourceId == null) {
				updateLeastTaggedResourceWithTeaser();
			} else {
				updateResourceById(resourceId);
				resourceId = null;
			}
		} else { 
			updateRandomResource();
		}
		
		// Finally, if no resource is available
		if (resource == null) {
			facesMessages.addFromResourceBundle("game.notEnoughPlayersOnline");
			redirect("/home.xhtml");
			log.info("No resource available");
		} else {
			log.info("#0 selected", resource);
		}
	}
	
	
	private void redirect(String viewId) {
		// Redirect
		Conversation.instance().endBeforeRedirect();
		Redirect redirect = Redirect.instance();
		redirect.setViewId(viewId);
		redirect.execute();
	}
	
	private void updateResourceById(Long resourceId) {
		log.info("Updating resource by id from DB");
		Query query = entityManager.createNamedQuery("artResource.byId");
		query.setParameter("id", resourceId);
		resource = (ArtResource) query.getSingleResult();
	}
	
	/**
	 * Find a random resource for the front page, that has a description.
	 * (Hence, search for a random description and take its resource.) 
	 */
	public void dailyResource() {
		log.info("Updating daily resource from DB");
		
		// Resources with Descriptions
		Query query = customSourceBean.query("artResource.withTeaser");
		@SuppressWarnings("unchecked")
		ArrayList<ArtResource> results = (ArrayList<ArtResource>) query.getResultList();
		
		// Get Day of year
		Calendar calendar = new GregorianCalendar();
		int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
		int year = calendar.get(Calendar.YEAR);

		if (results.size() > 0)
			resource = results.get((dayOfYear * year) % results.size());
	}
	
	public void updateLeastTaggedResource() {
		log.info("Updating Random Resource having no or only a few taggings" + (customSourceBean.getCustomized() ? " customized" : "") + " from DB");
		resource = artResourceDatabaseCacheBean.getArtResource("least");
	}
	
	public void updateLeastTaggedResourceWithTeaser() {
		log.info("Updating Random Resource having no or only a few taggings and a teaser" + (customSourceBean.getCustomized() ? " customized" : "")+ " from DB");
		
		resource = artResourceDatabaseCacheBean.getArtResource("leastWithTeaser");
	}
	
	public void updateAtLeastTaggedResource() {
		if (customSourceBean.isSearchSource()) {
			log.info("Updating Random Resource having at least a few taggings from search");
			resource = artResourceSearchCacheBean.getArtResource(null);
		} else {
			log.info("Updating Random Resource having at least a few taggings from DB");
			resource = artResourceDatabaseCacheBean.getArtResource("atLeast");
		}
	}
	
	public void updateAtLeastTaggedForCombinoResource() {
		if (customSourceBean.isSearchSource()) {
			log.info("Updating Random Resource having at least a few taggings for combination from search");
			resource = artResourceSearchCacheBean.getArtResource(null);
		} else {
			log.info("Updating Random Resource having at least a few taggings for combination from DB");
			resource = artResourceDatabaseCacheBean.getArtResource("atLeastForCombino");
		}
	}
	
	public void updateRandomResource() {
		log.info("Updating Random Resource from DB");
		
		// Resources with Desciptions
		try {
			Query query = customSourceBean.query("artResource.random");
			query.setMaxResults(1);
			this.resource = (ArtResource) query.getSingleResult();
		} catch(Exception e) {
			facesMessages.add("#{messages['general.noResource']}");
		}
	}
	
	public Long getResourceId() {
		return resourceId;
	}
	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}
	
}
