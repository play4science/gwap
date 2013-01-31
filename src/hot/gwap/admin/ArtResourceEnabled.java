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
	
	private Date getDateAuthorDead() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -minYearsAuthorDead);
		return cal.getTime();
	}
	
	private int getDateArtResourceCreatedYear() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -minYearsArtResourceCreated);
		return cal.get(Calendar.YEAR)-1;
	}
	
	public void calculateAll() {
		Query query = entityManager.createNamedQuery("artResource.all");
		List<ArtResource> artResources = (List<ArtResource>)query.getResultList();
		log.info("Calculating enabled for #0 art resources...", artResources.size());
		int updated = 0;
		int updatedEnabled = 0;
		int totalEnabled = 0;
		for (ArtResource r : artResources) {
			Boolean before = r.getEnabled();
			updateArtResource(r);
			Boolean after = r.getEnabled();
			if (before != after) {
				updated++;
				if (after)
					updatedEnabled++;
			}
			if (after) {
				totalEnabled++;
			}
		}
		String logMessage = String.format("%d resources processed: %d enabled (%d updated), %d disabled (%d updated)", 
				artResources.size(), totalEnabled, updatedEnabled, artResources.size()-totalEnabled, updated-updatedEnabled);
		facesMessages.add(logMessage);
		log.info(logMessage);
	}
	
	public void updateArtResource(ArtResource r) {
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
					enabled = artist.getDeath().before(getDateAuthorDead());
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
				if (0 < year && year < getDateArtResourceCreatedYear())
					enabled = true;
				logEnabled(enabled, "resource very old (#1)", r.getId(), r.getDateCreated());
			}
		}
		r.setEnabled(enabled);
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
