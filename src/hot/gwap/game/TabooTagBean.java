/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.game;

import gwap.model.resource.ArtResource;
import gwap.wrapper.TagFrequency;

import java.io.Serializable;
import java.util.List;

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
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.log.Log;

/**
 * @author Christoph Wieser
 */

@Name("tabooTagBean")
@Scope(ScopeType.PAGE)
public class TabooTagBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Create  public void init()    { log.info("Creating"); }
	@Destroy public void destroy() { log.info("Destroying"); }

	@Logger                  private Log log;
	@In                      private EntityManager entityManager;
	@In                      private LocaleSelector localeSelector;
	@In(create=true) @Out    private ArtResource resource;
	@Out                     private List<TagFrequency> tabooTags;
	
	private int limit= 10;
	
	@Factory("tabooTags")
	public void updateTabooTags() {
		Long threshold = 2L;
		Query query = entityManager.createNamedQuery("tagging.tagFrequencyByResourceAndLanguage");
		query.setParameter("resource", resource);
		query.setParameter("language", localeSelector.getLanguage());
		query.setParameter("threshold", threshold);
		query.setMaxResults(limit);
		tabooTags = query.getResultList();
	}
}
