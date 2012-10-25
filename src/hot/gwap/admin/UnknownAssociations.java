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
import gwap.wrapper.TagWithCount;
import gwap.wrapper.UnknownAssociation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.Log;


/**
 * @author kneissl
 */
@Name("unknownAssociations")
@Scope(ScopeType.PAGE)
public class UnknownAssociations implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Logger                  private Log log;
	@In                      private FacesMessages facesMessages;
	@In                      private EntityManager entityManager;
	
	@RequestParameter        private Long termId;
	@RequestParameter        private Long tagId;

	private List<UnknownAssociation> resultList;
	
	public void reject() {
		log.info("Rejecting association #0 for term #1", tagId, termId);
		Term term = entityManager.find(Term.class, termId);
		Tag association = entityManager.find(Tag.class, tagId);
		if (term.getConfirmedTags().contains(association)) {
			facesMessages.addFromResourceBundle(Severity.ERROR, "admin.term.associationAddError");
			return;
		}
		term.getRejectedTags().add(association);
	}
	
	public void confirm() {
		log.info("Confirming association #0 for term #1", tagId, termId);
		Term term = entityManager.find(Term.class, termId);
		Tag association = entityManager.find(Tag.class, tagId);
		if (term.getRejectedTags().contains(association)) {
			facesMessages.addFromResourceBundle(Severity.ERROR, "admin.term.associationAddError");
			return;
		}
		term.getConfirmedTags().add(association);
	}
	
	public List<UnknownAssociation> getResultList() {
		if (resultList == null)
			createList();
		return resultList;
	}

	private void createList() {
		log.info("Loading unknown associations list");
		Query q = entityManager.createNamedQuery("tagging.unknownAnswers");
		@SuppressWarnings("unchecked")
		List<Object[]> termTagCount = q.getResultList();
		resultList = new ArrayList<UnknownAssociation>();
		UnknownAssociation ua = null;
		for (Object[] row : termTagCount) {
			Term term = entityManager.find(Term.class, row[0]);
			if (ua == null || ua.getTerm() == null || !ua.getTerm().getId().equals(term.getId())) {
				if (ua != null)
					resultList.add(ua);
				ua = new UnknownAssociation();
				ua.setTerm(term);
				ua.setAssociations(new ArrayList<TagWithCount>());
			}
			Tag association = entityManager.find(Tag.class, row[1]);
			ua.getAssociations().add(new TagWithCount(association, (Long) row[2]));
		}
		if (ua != null)
			resultList.add(ua);
	}
	
	public boolean hasEntries() {
		Query q = entityManager.createNamedQuery("tagging.unknownAnswersCount");
		if (((Number)q.getSingleResult()).longValue() > 0)
			return true;
		else
			return false;
	}
}
