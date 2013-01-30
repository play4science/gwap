/*
 * This file is part of gwap
 *
 * Copyright (C) 2013
 * Project play4science
 * Lehr- und Forschungseinheit für Programmier- und Modellierungssprachen
 * Ludwig-Maximilians-Universität München
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package gwap.action;

import gwap.model.Tag;
import gwap.model.action.VirtualTagging;
import gwap.model.resource.Resource;
import gwap.wrapper.MatchingTag;

import java.util.Date;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("virtualTaggingBean")
@Scope(ScopeType.PAGE)
public class VirtualTaggingBean extends AbstractTaggingBean<VirtualTagging> {
	private static final long serialVersionUID = 1L;
	
	@In(create=true)         private VirtualTagging virtualTagging;
	
	public void showPreviousTaggings() {
		@SuppressWarnings("unchecked")
		List<Tag> virtualTaggingList = entityManager.createNamedQuery("virtualTagging.tagsByResourceAndLanguage")
			.setParameter("resource", resource)
			.setParameter("language", localeSelector.getLanguage())
			.getResultList();
		for (Tag tag : virtualTaggingList) {
			recommendedTags.add(new MatchingTag(tag.getName()));
		}
	}
	
	public MatchingTag createTagging(Resource resource) {
		MatchingTag matchingTag = null;
		Tag findOrCreateTag = findOrCreateTag();
		if (findOrCreateTag != null) {			
			matchingTag = new MatchingTag(recommendedTag.getName());
			recommendedTags.add(matchingTag);
			virtualTagging.setCreated(new Date());
			virtualTagging.setPerson(person);
			virtualTagging.setTag(findOrCreateTag);
			virtualTagging.setResource(resource);
			entityManager.persist(virtualTagging);
		} else {
            log.info("Tag '#0' was not added.", recommendedTag.getName());
        }
		return matchingTag;
	}
	
	public VirtualTagging getTagging() {
		return virtualTagging;
	}
		
}
