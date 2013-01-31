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

import gwap.model.Tag;
import gwap.model.resource.Term;
import gwap.wrapper.BackstageAnswer;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.Log;

/**
 * @author Fabian Kneißl
 */
@Name("termHome")
public class TermHome extends EntityHome<Term> {

	private static final long serialVersionUID = 8849610213839186469L;

	@RequestParameter		Long termId;
	@In                     private FacesMessages facesMessages;
	@In                     private EntityManager entityManager;
	@In                     private LocaleSelector localeSelector;
	@Logger                 private Log log;
	
	private String newConfirmedTag;
	private String newRejectedTag;
	
	@Override @Begin(join=true)
	public void create() {
		super.create();
		if (termId == null) {
			for (int i = 0; i < 5; i++) {
				getInstance().getConfirmedTags().add(new Tag());
			}
			getInstance().setTag(new Tag());
			getInstance().setRating(1);
			getInstance().setEnabled(true);
		}
	}
	
	@Override
	public Object getId() {
		if (termId == null)
			return super.getId();
		else
			return termId;
	}

	public void addConfirmedTag() {
		if (newConfirmedTag != null && newConfirmedTag.length() > 0) {
			Tag tag = findOrCreateTag(newConfirmedTag);
			if (getInstance().getRejectedTags().contains(tag)) {
				facesMessages.addFromResourceBundle(Severity.ERROR, "admin.term.associationAddError");
				return;
			}
			getInstance().getConfirmedTags().add(tag);
			facesMessages.add("Bestätigte Assoziation #{newConfirmedTag} wurde erfolgreich hinzugefügt!");
			newConfirmedTag = "";
		}
		else
			facesMessages.addToControl("confirmedTagsTable", "Bitte geben Sie eine bestätigte Assoziation an!");
	}
	
	public void deleteConfirmedTag(Long confirmedTagId) {
		if (confirmedTagId != null) {
			List<Tag> confirmedTags = getInstance().getConfirmedTags();
			for (int i = 0; i < confirmedTags.size(); i++) {
				if (confirmedTags.get(i).getId() == confirmedTagId) {
					confirmedTags.remove(i);
					break;
				}
			}
		}
	}
	
	public void addRejectedTag() {
		if (newRejectedTag != null && newRejectedTag.length() > 0) {
			Tag tag = findOrCreateTag(newRejectedTag);
			if (getInstance().getConfirmedTags().contains(tag)) {
				facesMessages.addFromResourceBundle(Severity.ERROR, "admin.term.associationAddError");
				return;
			}
			getInstance().getRejectedTags().add(tag);
			facesMessages.add("Falsche Assoziation #{newRejectedTag} wurde erfolgreich hinzugefügt!");
			newRejectedTag = "";
		}
		else
			facesMessages.addToControl("RejectedTagsTable", "Bitte geben Sie eine falsche Assoziation an!");
	}
	
	public void deleteRejectedTag(Long rejectedTagId) {
		if (rejectedTagId != null) {
			List<Tag> rejectedTags = getInstance().getRejectedTags();
			for (int i = 0; i < rejectedTags.size(); i++) {
				if (rejectedTags.get(i).getId() == rejectedTagId) {
					rejectedTags.remove(i);
					break;
				}
			}
		}
	}
	
	private boolean associationsValid() {
		// Check if a tag is both in confirmed tags and in rejected tags
		for (Tag confirmed : getInstance().getConfirmedTags()) {
			if (getInstance().getRejectedTags().contains(confirmed)) {
				facesMessages.add(Severity.ERROR, "Eine Assoziation kann nicht sowohl bestätigt als auch falsch sein! Bitte aus einem der beiden Felder entfernen");
				return false;
			}
		}
		return true;
	}
	
	private void updateAssociations() {
		// Fix confirmed tags list
		List<Tag> enteredConfirmedTags = getInstance().getConfirmedTags();
		getInstance().setConfirmedTags(new ArrayList<Tag>());
		for (Tag tag : enteredConfirmedTags) {
			if (tag.getName() != null && tag.getName().length() > 0) {
				Tag t = findOrCreateTag(tag.getName());
				getInstance().getConfirmedTags().add(t);
			}
		}

		// Fix rejected tags list
		List<Tag> enteredRejectedTags = getInstance().getRejectedTags();
		getInstance().setRejectedTags(new ArrayList<Tag>());
		for (Tag tag : enteredRejectedTags) {
			if (tag.getName() != null && tag.getName().length() > 0) {
				Tag t = findOrCreateTag(tag.getName());
				getInstance().getRejectedTags().add(t);
			}
		}
	}
	
	@Override
	public String persist() {
		getInstance().setTag(findOrCreateTag(getInstance().getTag().getName()));
		
		if (!associationsValid())
			return null;
		
		updateAssociations();
		
		String result = super.persist();
		if (result.equals("persisted")) {
			facesMessages.add("Term #0 wurde erfolgreich geändert!", getInstance().getTag().getName());
			return "/admin/termList.xhtml";
		} else
			return result;
	}
	
	@Override
	public String update() {
		if (!associationsValid())
			return null;

		String result = super.update();
		if (result.equals("updated")) {
			facesMessages.add("Term #0 wurde erfolgreich geändert!", getInstance().getTag().getName());
			return "/admin/termList.xhtml";
		} else
			return result;
	}
	
	private Tag findOrCreateTag(String name) {
		Query q = entityManager.createNamedQuery("tag.tagByNameAndLanguage");
		q.setParameter("name", name);
		q.setParameter("language", localeSelector.getLanguage());
		Tag tag;
		try {
			tag = (Tag) q.getSingleResult();
		} catch (NoResultException e) {
			tag = new Tag();
			tag.setName(name);
			tag.setLanguage(localeSelector.getLanguage());
			entityManager.persist(tag);
		}
		return tag;
	}
	
	public List<BackstageAnswer> getTopCorrectAnswers() {
		Query q = entityManager.createNamedQuery("tagging.topCorrectAnswersGeneral");
		q.setParameter("resourceId", getId());
		return q.getResultList();
	}
	
	public List<BackstageAnswer> getTopUnknownAnswers() {
		Query q = entityManager.createNamedQuery("tagging.topUnknownAnswersGeneral");
		q.setParameter("resourceId", getId());
		return q.getResultList();
	}
	
	public List<BackstageAnswer> getTopWrongAnswers() {
		Query q = entityManager.createNamedQuery("tagging.topWrongAnswersGeneral");
		q.setParameter("resourceId", getId());
		return q.getResultList();
	}

	public String getNewConfirmedTag() {
		return newConfirmedTag;
	}

	public void setNewConfirmedTag(String newConfirmedTerm) {
		this.newConfirmedTag = newConfirmedTerm;
	}

	public String getNewRejectedTag() {
		return newRejectedTag;
	}

	public void setNewRejectedTag(String newRejectedTag) {
		this.newRejectedTag = newRejectedTag;
	}
	
}
