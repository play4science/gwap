/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.admin;

import gwap.model.Person;
import gwap.model.resource.ArtResource;

import java.io.File;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

/**
 * Computes the 'enabled' field for ArtResources and sets the appropriate
 * value in the objects.
 * 
 * @author Fabian Kneißl
 */
@Name("adminArtResourceEnabled")
@Restrict("#{s:hasPermission('artigo', 'all')}")
public class ArtResourceEnabled implements Serializable {
	
	@Logger                  private Log log;
	@In                      private FacesMessages facesMessages;
	@In                      private EntityManager entityManager;
	
	private static int minYearsAuthorDead = 70;
	private static int minYearsArtResourceCreated = 130;  // FIXME: how long?
	
	public void calculateAll() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -minYearsAuthorDead);
		Date dateAuthorDead = cal.getTime();
		cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -minYearsArtResourceCreated);
		int dateArtResourceCreatedYear = cal.get(Calendar.YEAR)-1;
		
		Query query = entityManager.createNamedQuery("artResource.all");
		List<ArtResource> artResources = (List<ArtResource>)query.getResultList();
		log.info("Calculating enabled for #0 art resources...", artResources.size());
		int updated = 0;
		int updatedEnabled = 0;
		int totalEnabled = 0;
		for (ArtResource r : artResources) {
			// Check if file exists
			boolean enabled = new File(r.getSource().getUrl() + r.getPath()).canRead();
			logDisabled(enabled, "file does not exist", r.getId());

			// Skipped by staff
			if (enabled && (r.getSkip() != null && r.getSkip())) {
				enabled = false;
				logDisabled(enabled, "skipped by staff", r.getId());
			}

			if (enabled) {
				// Creator easement
				enabled = (r.getEasement() != null) && r.getEasement();
				logEnabled(enabled, "easement", r.getId());
				
				// Date check for author
				if (!enabled) {
					Person artist = r.getArtist();
					if (artist != null && artist.getDeath() != null) {
						enabled = artist.getDeath().before(dateAuthorDead);
						logEnabled(enabled, "author dead (#1)", r.getId(), artist.getDeath());
					}
				}
				// Date check for resource
				if (!enabled && r.getDateCreated() != null) {
					// Try to parse dateCreated
					int year = 0;
					try {
						year = Integer.parseInt(r.getDateCreated());
					} catch (NumberFormatException e) {
						String[] split = r.getDateCreated().split("[^0-9]");
						for (int i = 0; i < split.length; i++) {
							try {
								int yearTmp = Integer.parseInt(split[i]);
								if (yearTmp > year)
									year = yearTmp;
							} catch (NumberFormatException e1) {
								// do nothing
							}
						}
					}
					if (0 < year && year < dateArtResourceCreatedYear)
						enabled = true;
					logEnabled(enabled, "resource very old (#1)", r.getId(), r.getDateCreated());
				}
			}
			
			// Finally set 'enabled' field!
			if (r.getEnabled() == null || r.getEnabled() != enabled) {
				r.setEnabled(enabled);
				updated++;
				if (enabled)
					updatedEnabled++;
			}
			if (enabled)
				totalEnabled++;
		}
		String logMessage = String.format("%d resources processed: %d enabled (%d updated), %d disabled (%d updated)", 
				artResources.size(), totalEnabled, updatedEnabled, artResources.size()-totalEnabled, updated-updatedEnabled);
		facesMessages.add(logMessage);
		log.info(logMessage);
	}
	
	private void logEnabled(boolean enabled, String reason, Object... params) {
		if (enabled)
			log.info("ArtResource #0 enabled because of "+reason, params);
	}
	
	private void logDisabled(boolean enabled, String reason, Object... params) {
		if (!enabled)
			log.info("ArtResource #0 disabled because of "+reason, params);
	}
}
