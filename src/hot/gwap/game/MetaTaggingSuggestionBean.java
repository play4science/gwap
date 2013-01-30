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

import gwap.model.Tag;
import gwap.wrapper.TagFrequency;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.log.Log;

/**
 * @author wieser
 */

@Name("metaTaggingSuggestionBean")
@Scope(ScopeType.PAGE)
public class MetaTaggingSuggestionBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Create  public void init()    { log.info("Creating"); }
	@Destroy public void destroy() { log.info("Destroying"); }

	@Logger                  private Log log;
	@In                      private EntityManager entityManager;
	@In                      private LocaleSelector localeSelector;
	@In(create=true) @Out    private Tag tagResource;
	@Out                     private List<TagFrequency> metaTaggingSuggestions;
	
	private int limit= 20;
	
	@Factory("metaTaggingSuggestions") 
	public void updateMetaTaggingSuggestion() {
		
		// get metatags of for resource and tag
		Long threshold = 0L;
		Query query1 = entityManager.createNamedQuery("metatagging.tagFrequencyByMetaTagAndLanguageAndResource");
		query1.setParameter("language", localeSelector.getLanguage());
		query1.setParameter("threshold", threshold);
		query1.setParameter("tagResource", tagResource);
		query1.setMaxResults(limit);
		@SuppressWarnings("unchecked")
		List<TagFrequency> resultList1 = query1.getResultList();
		int numberOfResults = resultList1.size();
		
		// fill up with popular meta tags
		if ( numberOfResults < limit ) {
			// get metatags for tag
			Query query2 = entityManager.createNamedQuery("metatagging.tagFrequencyByMetaTagAndLanguage");
			query2.setParameter("language", localeSelector.getLanguage());
			query2.setParameter("threshold", threshold);
			query2.setMaxResults(limit - numberOfResults);
			@SuppressWarnings("unchecked")
			List<TagFrequency> resultList2 = query2.getResultList();
			
			// insert without producing duplicates
			for (TagFrequency tagFrequency2 : resultList2) {
				boolean contained = false;
				for (TagFrequency tagFrequency1 : resultList1) {
					if ( tagFrequency1.getName().equals(tagFrequency2.getName()) ) {
						contained = true;
						break;
					}
				}
				if ( !contained ) {
					resultList1.add(tagFrequency2);
				}
			}
		}

		metaTaggingSuggestions = resultList1;
	}
	
}
