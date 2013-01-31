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
import gwap.model.resource.ArtResourceTeaser;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

@Name("artResourceTeaserBean")
@Scope(ScopeType.PAGE)
public class ArtResourceTeaserBean implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Logger                  private Log log;
	@In                      private FacesMessages facesMessages;
	@In                      private EntityManager entityManager;
	@In                      private Person person;
	
	@Out(required=false)
	private ArtResource resource;
	
	@DataModelSelection
	@Out(required=false)
	private ArtResourceTeaser artResourceTeaser;
	
	@RequestParameter
	private Long resourceId;
	
	private Long resourceIdNoRequestParameter;
	
	private String chosenLanguage;
	
	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	
	@DataModel
	private List<ArtResourceTeaser> getTeaserList() {
		if (resource != null)
			return resource.getTeasers();
		else
			return null;
	}
	
	public void chooseResourceWithoutTeaser() {
		Query query = entityManager.createNamedQuery("artResource.randomWithoutTeaser");
		query.setMaxResults(1);
		try {
			resource = (ArtResource) query.getSingleResult();
			chosenLanguage = "de";
			newTeaser();
			resourceIdNoRequestParameter = resource.getId();
		} catch (NoResultException e) {
			facesMessages.addFromResourceBundle("admin.resourceTeaser.noResourceFound");
			resource = null;
		}
	}
	
	public void chooseNotTranslatedResource(String language) {
		Query query = entityManager.createNamedQuery("artResource.randomWithoutTeaserInLanguage");
		query.setParameter("language", language);
		query.setMaxResults(1);
		try {
			resource = (ArtResource) query.getSingleResult();
			chosenLanguage = language;
			newTeaser();
			resourceIdNoRequestParameter = resource.getId();
		} catch (NoResultException e) {
			facesMessages.addFromResourceBundle("admin.resourceTeaser.noResourceFound");
			resource = null;
		}
	}
	
	public void chooseResource(Long resourceId) {
		if (resourceId != null) {
			resource = entityManager.find(ArtResource.class, resourceId);
			if (resource != null) {
				chosenLanguage = null;
				newTeaser();
				resourceIdNoRequestParameter = resource.getId();
			} else {
				facesMessages.addFromResourceBundle("admin.resourceTeaser.noResourceFound");
			}
		}
	}
	
	public void chooseResource() {
		// Workaround: If resourceId is given as RequestParameter, it is not set in this bean
		if (resourceIdNoRequestParameter != null && resourceIdNoRequestParameter > 0)
			chooseResource(resourceIdNoRequestParameter);
		else
			chooseResource(resourceId);
	}
	
	public void modifyTeaser() {
		if (artResourceTeaser != null && resource != null) {
			if (artResourceTeaser.getId() == null) {
				Query q = entityManager.createNamedQuery("artResourceTeaser.byLanguageAndResource");
				q.setParameter("language", artResourceTeaser.getLanguage());
				q.setParameter("resource", resource);
				q.setMaxResults(1);
				if (q.getResultList().isEmpty()) {
					log.info("Adding teaser #0 to resource #1", artResourceTeaser, resource);
					artResourceTeaser.setResource(resource);
					artResourceTeaser.setCreateDate(new Date());
					artResourceTeaser.setCreator(person);
					entityManager.persist(artResourceTeaser);
					resource.getTeasers().add(artResourceTeaser);
					facesMessages.addFromResourceBundle("admin.resourceTeaser.teaserAdded");
				} else {
					log.info("Did not add teaser #0 to resource #1 because one with the same language already exists", artResourceTeaser, resource);
					facesMessages.addFromResourceBundle("admin.resourceTeaser.teaserAlreadyExists");
				}
			} else {
				log.info("Updating teaser #0", artResourceTeaser);
				
				ArtResourceTeaser oldTeaser = entityManager.find(ArtResourceTeaser.class, artResourceTeaser.getId());
				oldTeaser.setInternalNote("updated at "+dateFormat.format(new Date()) + ", resource_id="+oldTeaser.getResource().getId());
				oldTeaser.setResource(null);
				resource.getTeasers().remove(oldTeaser);
				
				ArtResourceTeaser newTeaser = new ArtResourceTeaser();
				newTeaser.setResource(resource);
				newTeaser.setCreateDate(new Date());
				newTeaser.setCreator(person);
				newTeaser.setLanguage(artResourceTeaser.getLanguage());
				newTeaser.setDescription(artResourceTeaser.getDescription());
				entityManager.persist(newTeaser);
				resource.getTeasers().add(newTeaser);
				
				artResourceTeaser = newTeaser;
				facesMessages.addFromResourceBundle("admin.resourceTeaser.teaserUpdated");
			}
			updateResourceTeasers();
		}
	}
	
	private void updateResourceTeasers() {
		entityManager.flush();
		Query q = entityManager.createNamedQuery("artResource.byIdWithTeasers");
		q.setParameter("id", resource.getId()).setMaxResults(1);
		try {
			resource = (ArtResource) q.getSingleResult();
		} catch (NoResultException e) {
			resource = entityManager.find(ArtResource.class, resource.getId());
		}
		newTeaser();
	}

	public void editTeaser(Long id) {
	}
	
	public void newTeaser() {
		artResourceTeaser = new ArtResourceTeaser();
		if (chosenLanguage != null) {
			artResourceTeaser.setLanguage(chosenLanguage);
		} else {
			// Guess language
			if (getTeaserList() != null && getTeaserList().size() > 0) {
				List<String> languages = new ArrayList<String>();
				languages.add("de"); languages.add("en"); languages.add("fr");
				for (ArtResourceTeaser teaser : getTeaserList()) {
					languages.remove(teaser.getLanguage());
				}
				if (languages.size() > 0)
					artResourceTeaser.setLanguage(languages.get(0));
			}
		}
	}
	
	public void deleteTeaser(Long id) {
		log.info("Deleting teaser #0", artResourceTeaser);
		artResourceTeaser = entityManager.find(ArtResourceTeaser.class, id);
		artResourceTeaser.setInternalNote("deleted at "+dateFormat.format(new Date())+ ", resource_id="+artResourceTeaser.getResource().getId());
		artResourceTeaser.setResource(null);
		resource.getTeasers().remove(artResourceTeaser);
		updateResourceTeasers();
		newTeaser();
	}
	
	public Map<String, String> getLanguages() {
		Map<String, String> languages = new TreeMap<String, String>();
		languages.put("Deutsch", "de");
		languages.put("English", "en");
		languages.put("Francais", "fr");
		return languages;
	}
	
	public ArtResource getResource() {
		return resource;
	}

	public void setArtResourceTeaser(ArtResourceTeaser artResourceTeaser) {
		this.artResourceTeaser = artResourceTeaser;
	}
	
	public ArtResourceTeaser getArtResourceTeaser() {
		return artResourceTeaser;
	}
	
	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}
	
	public Long getResourceId() {
		return resourceId;
	}

	public Long getResourceIdNoRequestParameter() {
		return resourceIdNoRequestParameter;
	}

	public void setResourceIdNoRequestParameter(Long resourceIdNoRequestParameter) {
		this.resourceIdNoRequestParameter = resourceIdNoRequestParameter;
	}
	
}
