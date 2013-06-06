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

package gwap.action;

import gwap.elearn.TerminaMatching;
import gwap.model.Tag;
import gwap.model.resource.Resource;
import gwap.model.resource.Term;
import gwap.wrapper.MatchingTag;

import java.util.Date;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("terminaTaggingBean")
@Scope(ScopeType.PAGE)
public class TerminaTaggingBean extends TaggingBean {
	private static final long serialVersionUID = 1L;

	@Override
	public MatchingTag createTagging(Resource resource) {
		MatchingTag matchingTag = null;
		Tag findOrCreateTag = findOrCreateTag();
		if (findOrCreateTag != null) {
			matchingTag = new MatchingTag(recommendedTag.getName());
			recommendedTags.add(matchingTag);
			tagging.setCreated(new Date());
			tagging.setPerson(person);
			tagging.setTag(findOrCreateTag);
			tagging.setResource(resource);
			tagging.setGameRound(gameRound);
			entityManager.persist(tagging);
			// Scoring
			Term term = (Term) resource;
			matchingTag.setIndirectMatch(true); // = unknown association
			if (TerminaMatching.isAssociationInList(tagging.getTag().getName(), term.getConfirmedTags())) {
				matchingTag.setDirectMatch(true);
				matchingTag.setScore(1);
				tagging.setScore(1);
			} else if (TerminaMatching.isAssociationInList(tagging.getTag().getName(), term.getRejectedTags())) {
				matchingTag.setIndirectMatch(false);
				matchingTag.setScore(-1);
				tagging.setScore(-1);
			}
		} else {
			log.info("Tag '#0' was not added.", recommendedTag.getName());
		}
		return matchingTag;
	}
	
}
