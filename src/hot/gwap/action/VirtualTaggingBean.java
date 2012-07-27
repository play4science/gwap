/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
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
