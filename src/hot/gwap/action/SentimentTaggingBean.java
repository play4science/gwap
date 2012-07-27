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

import javax.faces.context.FacesContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.LocaleSelector;


/**
 * @author Christoph Wieser
 */

@Name("sentimentTaggingBean")
@Scope(ScopeType.PAGE)
public class SentimentTaggingBean extends AbstractTaggingBean<MetaTagging> {
	private static final long serialVersionUID = 1L;
	
	@In(create=true)             private MetaTagging metaTagging;
	@In                          private LocaleSelector localeSelector;
	
	/*
	 * Observer checkForMatchingTags in TaggingBean
	 * @see gwap.action.AbstractTaggingBean#createTagging(gwap.model.resource.Resource)
	 */
	
	public MatchingTag createTagging(Resource resource) {
		MatchingTag matchingTag = null;
		Tag findOrCreateTag = findOrCreateTag();
		if (findOrCreateTag != null) {			
			matchingTag = new MatchingTag(recommendedTag.getName());
			recommendedTags.add(matchingTag);
			
			String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();

			metaTagging.setCreated(new Date());
			metaTagging.setPerson(person);
			metaTagging.setTagResource(findOrCreateTag);
			metaTagging.setGameRound(gameRound);
			metaTagging.setResource(resource);
			if (localeSelector.getLanguage().equals("de")) {
				metaTagging.setTag(findOrCreateTag("Gefühl"));
			} else if (localeSelector.getLanguage().equals("en")) {
				metaTagging.setTag(findOrCreateTag("feeling"));
			} else if (localeSelector.getLanguage().equals("fr")) {
				metaTagging.setTag(findOrCreateTag("sentiment"));
			}
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
