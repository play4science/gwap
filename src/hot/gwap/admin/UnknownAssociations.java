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
