/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
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
import javax.persistence.NoResultException;
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
	public void updateResource() {
		String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
		if (viewId.equals("/home.xhtml")) {
			dailyResource(); 
		} else if (viewId.startsWith("/custom/")) {
			updateRandomResourceBySource();
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
		log.info("Updating resource by id");
		Query query = entityManager.createNamedQuery("artResource.byId");
		query.setParameter("id", resourceId);
		resource = (ArtResource) query.getSingleResult();
	}
	
	/**
	 * Find a random resource for the front page, that has a description.
	 * (Hence, search for a random description and take its resource.) 
	 */
	public void dailyResource() {
		log.info("Updating daily resource");
		
		// Resources with Descriptions
		Query query = customSourceBean.query("artResource.withTeaser");
		@SuppressWarnings("unchecked")
		ArrayList<ArtResource> results = (ArrayList<ArtResource>) query.getResultList();
		
		// Get Day of year
		Calendar calendar = new GregorianCalendar();
		int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
		int year = calendar.get(Calendar.YEAR);

		try {
			resource = results.get((dayOfYear * year) % results.size());
		} catch (NoResultException e) {
			facesMessages.add("#{messages['general.noResource']}");
		}
	}
	
	public void updateLeastTaggedResource() {
		log.info("Updating Random Resource having no or only a few taggings" + (customSourceBean.getCustomized() ? " customized" : ""));
		
		resource = artResourceDatabaseCacheBean.getArtResource("least");
	}
	
	public void updateLeastTaggedResourceWithTeaser() {
		log.info("Updating Random Resource having no or only a few taggings and a teaser" + (customSourceBean.getCustomized() ? " customized" : ""));
		
		resource = artResourceDatabaseCacheBean.getArtResource("leastWithTeaser");
	}
	
	public void updateAtLeastTaggedResource() {
		log.info("Updating Random Resource having at least a few taggings");
		resource = artResourceDatabaseCacheBean.getArtResource("atLeast");
	}
	
	public void updateAtLeastTaggedForCombinoResource() {
		log.info("Updating Random Resource having at least a few taggings for combination");
		resource = artResourceDatabaseCacheBean.getArtResource("atLeastForCombino");
	}
	
	public void updateRandomResource() {
		log.info("Updating Random Resource");
		
		// Resources with Desciptions
		try {
			Query query = entityManager.createNamedQuery("artResource.random");
			query.setMaxResults(1);
			this.resource = (ArtResource) query.getSingleResult();
		} catch(Exception e) {
			facesMessages.add("#{messages['general.noResource']}");
		}
	}
	
	public void updateRandomResourceBySource() {
		log.info("Updating Random Resource");
		
		// Resources with Desciptions
		try {
			Query query = customSourceBean.query("artResource.random");
			query.setParameter("source", customSourceBean.getCustomSource());
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
