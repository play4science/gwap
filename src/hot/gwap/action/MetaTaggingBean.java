/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.action;

import gwap.model.Tag;
import gwap.model.action.MetaTagging;
import gwap.model.resource.Resource;
import gwap.wrapper.MatchingTag;

import java.util.Date;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;


/**
 * @author Christoph Wieser
 */

@Name("metaTaggingBean")
@Scope(ScopeType.PAGE)
public class MetaTaggingBean extends AbstractTaggingBean<MetaTagging> {
	private static final long serialVersionUID = 1L;
	
	@In(create=true)   private MetaTagging metaTagging;
	@In(required=true) private Tag tagResource;
	
	@Observer(value="checkForMatchingTags", create=false)
	public void checkForMatchingTags() {
		super.checkForMatchingTags();
	}
	
	public MatchingTag createTagging(Resource resource) {
		MatchingTag matchingTag = null;
		Tag findOrCreateTag = findOrCreateTag();
		if (findOrCreateTag != null) {			
			matchingTag = new MatchingTag(recommendedTag.getName());
			recommendedTags.add(matchingTag);
			
			metaTagging.setCreated(new Date());
			metaTagging.setPerson(person);
			metaTagging.setTag(findOrCreateTag);
			metaTagging.setResource(resource);
			metaTagging.setTagResource(tagResource);
			metaTagging.setGameRound(gameRound);
			entityManager.persist(metaTagging);
		} else {
            log.info("Tag '#0' was not added.", recommendedTag.getName());
        }
		return matchingTag;
	}
	
	public MetaTagging getTagging() {
		return metaTagging;
	}
	
}
