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

import gwap.model.Tag;
import gwap.model.action.MetaTagging;
import gwap.model.resource.Resource;
import gwap.wrapper.MatchingTag;

import java.util.Date;

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
