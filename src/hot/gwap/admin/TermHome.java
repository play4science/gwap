/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.admin;

import gwap.model.Tag;
import gwap.model.resource.Term;

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
import org.jboss.seam.log.Log;

/**
 * @author Fabian Kneißl
 */
@Name("termHome")
public class TermHome extends EntityHome<Term> {

	private static final long serialVersionUID = 8849610213839186469L;

	@RequestParameter		Long termId;
	@RequestParameter       Long confirmedTagId;
	@In                     private FacesMessages facesMessages;
	@In                     private EntityManager entityManager;
	@In                     private LocaleSelector localeSelector;
	@Logger                 private Log log;
	
	private String newConfirmedTag;
	
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
			getInstance().getConfirmedTags().add(findOrCreateTag(newConfirmedTag));
			facesMessages.add("Confirmed Term #{newConfirmedTag} wurde erfolgreich hinzugefügt!");
			newConfirmedTag = "";
		}
		else
			facesMessages.addToControl("confirmedTagsTable", "Bitte geben Sie einen Confirmed Term an!");
	}
	
	public void deleteConfirmedTerm(Long confirmedTagId) {
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
	
	@Override
	public String persist() {
		List<Tag> enteredConfirmedTags = getInstance().getConfirmedTags();
		getInstance().setConfirmedTags(new ArrayList<Tag>());

		getInstance().setTag(findOrCreateTag(getInstance().getTag().getName()));
		
		for (Tag tag : enteredConfirmedTags) {
			if (tag.getName() != null && tag.getName().length() > 0) {
				Tag t = findOrCreateTag(tag.getName());
				getInstance().getConfirmedTags().add(t);
			}
		}

		String result = super.persist();
		if (result.equals("persisted")) {
			facesMessages.add("Term #0 wurde erfolgreich geändert!", getInstance().getTag().getName());
			return "/admin/termList.xhtml";
		} else
			return result;
	}
	
	@Override
	public String update() {
		//FIXME Handle adding / deleting of terms intelligently
//		List<Term> enteredConfirmedTerms = getInstance().getConfirmedTerms();
//		getInstance().setConfirmedTerms(new ArrayList<Term>());

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

	public String getNewConfirmedTag() {
		return newConfirmedTag;
	}

	public void setNewConfirmedTag(String newConfirmedTerm) {
		this.newConfirmedTag = newConfirmedTerm;
	}
	
}