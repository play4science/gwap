/*
s * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.admin;

import gwap.model.Topic;
import gwap.model.resource.Resource;
import gwap.model.resource.Term;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
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

import com.google.common.base.Strings;

/**
 * @author Mislav Boras
 */
@Name("topicHome")
public class TopicHome extends EntityHome<Topic> {

	private static final long serialVersionUID = 8849345213839186469L;

	@RequestParameter
	Long topicId;
	@In
	private FacesMessages facesMessages;
	@In
	private EntityManager entityManager;
	@In
	private LocaleSelector localeSelector;
	@Logger
	private Log log;
	private List<Resource> allResources = null;
	private List<Resource> selectedResources = null;
	private List<String> list = new ArrayList<String>();


	public List<Resource> getSelectedResources() {
		return selectedResources;
	}

	public void setSelectedResources(List<Resource> selectedResources) {
		this.selectedResources = selectedResources;
	}


	public List<Term> getList() {
		List<Term> termList = new ArrayList<Term>();
		selectedResources = getInstance().getResources();

		if (selectedResources != null) {

			for (Resource r : selectedResources) {
				Term t = (Term) r;
			    termList.add(t);
			}
		}

		return termList;
	}

	public void setList(List<String> rightSideValues) {
		this.list = rightSideValues;
	}

	@Override
	@Begin(join = true)
	public void create() {
		super.create();
	}

	@Override
	public Object getId() {
		if (topicId == null)
			return super.getId();
		else
			return topicId;
	}
	
	public void setAllResources(List<Resource> allResources) {
		this.allResources = allResources;
	}
	
	@Override
	public String persist() {
		if (Strings.isNullOrEmpty(getInstance().getName())) {
			facesMessages.addToControl("topicName", "#{messages['topic.name']} #{messages['validator.notNull']}");
			return null;
		}
		String persist = super.persist();
		addResources();
		return persist;
	};

	@Override
	public String update() {
		String update = super.update();
		addResources();
		return update;
	};
	
	
	public List<Resource> getAllResources() {  
		if (allResources == null) {
			Query q = entityManager.createNamedQuery("term.allTerms");
			List<Term> allTerms = q.getResultList();
			allResources = new ArrayList<Resource>();
			for (Term term : allTerms) {
				allResources.add(term);
			}
		}
		return allResources;
	}

	public void addResources() {
		selectedResources = null;
		selectedResources = new ArrayList<Resource>();
		if (list != null) {
			for (Resource r : allResources) {
				for (String id : list) {
					if (r.toString().equals(id)) {
						selectedResources.add(r);
					}
				}
			}

			for (String s : list) {
				// log.info(s);
				System.out.println(s);
			}
		}
		
		getInstance().setResources(selectedResources);
	}
}
