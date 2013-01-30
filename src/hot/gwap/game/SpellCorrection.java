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
