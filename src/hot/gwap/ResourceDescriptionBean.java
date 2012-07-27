/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap;

import gwap.model.resource.ArtResource;

import java.io.Serializable;

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
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.log.Log;

@Name("resourceDescriptionBean")
@Scope(ScopeType.STATELESS)
public class ResourceDescriptionBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Create	 public void init()    { log.info("Creating"); }
	@Destroy public void destroy() { log.info("Destroying"); }
	
	@Logger                  private Log log;
	@In                      private EntityManager entityManager;
	@In                      private LocaleSelector localeSelector;
	@In(create=true) @Out    private ArtResource resource;
	@SuppressWarnings("unused")
	@Out(required=false)     private String artResourceImageTeaser;
	@Out(required=false)     private String artResourceImageTitle;

	@Factory("artResourceImageTeaser")
	public void updateArtResourceImageTeaser() {
		log.info("Updating ArtResource Description");
		
		Query query = entityManager.createNamedQuery("artResource.teaserByLanguage");
		query.setParameter("resource", resource);
		query.setParameter("language", localeSelector.getLanguage());
		
		artResourceImageTeaser = null;
		try {
			artResourceImageTeaser = (String) query.getSingleResult();
		} catch(NoResultException exception) {
			log.info("Could not find image teaser");
		}
	}
	
	@Factory("artResourceImageTitle")
	public void updateArtResourceTitle() {
		log.info("Updating ArtResource Title");
		
		artResourceImageTitle = resource.getDefaultTitle();
			
		String artistName = resource.getArtistName();
		if (artistName != null) {
			artResourceImageTitle += " (" + artistName + ")";
		}
	}
}