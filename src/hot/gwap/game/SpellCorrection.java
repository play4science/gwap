/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.game;

import gwap.action.TaggingBean;
import gwap.model.resource.Resource;
import gwap.tools.LevenshteinDistance;
import gwap.tools.TagSemantics;
import gwap.wrapper.MatchingTag;
import gwap.wrapper.TagFrequency;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.log.Log;

@Name("spellCorrection")
@Scope(ScopeType.CONVERSATION)
public class SpellCorrection implements Serializable {

	@Create                  public void init() { log.info("Creating"); }
	@Destroy                 public void destroy() { log.info("Destroying"); }
	
	@Logger                  private Log log;
	@In private EntityManager entityManager;
	@In private LocaleSelector localeSelector;
	@In(required=false) private TaggingBean taggingBean;
	@In(required=false) private List<TagFrequency> tabooTags;
	
	public void findSpellCorrectedTags(Resource resource, MatchingTag matchingTag) {
		if (!matchingTag.isTagCorrectionCompleted()) {
			String originalTag = matchingTag.getTag();
			int TagLength = originalTag.length();
			if (TagLength>=3){
			
				Query query1 = entityManager.createNamedQuery("tag.tagNamesByResource");
				query1.setParameter("resource", resource);
				query1.setParameter("language", localeSelector.getLanguage());			
				query1.setParameter("minOccurrence", 2L);				
				findAlternativeTags(matchingTag, query1.getResultList());					
			}
		}
	}
	
	private void findAlternativeTags(MatchingTag matchingTag, List<String> tagNames){
		
		String originalTag = matchingTag.getTag();
		int originalTagLength = originalTag.length();
		
		for (int i=0; i<tagNames.size(); i++){
			String tagName = tagNames.get(i);
			int lengthDiff = tagName.length()-originalTagLength;
			
			if (-2<=lengthDiff && lengthDiff<=2){
				int levenshteinDiff = LevenshteinDistance.computeLevenshteinDistance(originalTag.toLowerCase(), tagName.toLowerCase());
				if (levenshteinDiff<=1 ||(originalTag.length()>8 && levenshteinDiff<=2)){
					if (tabooTags == null || TagSemantics.containsNotNormalized2(tabooTags, tagName) == null) {
						matchingTag.addAlternativeTag(tagName);
						i=tagNames.size();
					}
				}		
			}
		}		
	}
	
}
