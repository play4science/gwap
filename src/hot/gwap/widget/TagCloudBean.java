/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.widget;

import gwap.model.Tag;
import gwap.model.resource.ArtResource;
import gwap.wrapper.TagFrequency;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.log.Log;

@Name("tagCloudBean")
@Scope(ScopeType.PAGE)
public class TagCloudBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final int MAX_NORMALIZED_FREQUENCY = 5;
	
	@Logger	                 private Log log;
	@In	                     private EntityManager entityManager;
	@In(create=true) @Out    private ArtResource resource;
	@In                      private LocaleSelector localeSelector;
	@DataModel				 private List<TagFrequency> tagCloudAll;
	@DataModel               private List<TagFrequency> tagCloud;
	@DataModelSelection(value="tagCloud")      private TagFrequency tagFrequency;
	private String cachedResourceId;
	
	private Long threshold = 2L;
	
	public List<TagFrequency> getTagCloud(ArtResource resource, Long threshold, int maxQueryResults)
	{
		Query query = entityManager.createNamedQuery("tagging.tagFrequencyByResourceAndLanguage");
		query.setParameter("resource", resource);
		query.setParameter("language", localeSelector.getLanguage());
		query.setParameter("threshold", threshold);
		query.setMaxResults(maxQueryResults);
		List<TagFrequency> res = query.getResultList();
		

		//Get tags from Descriptions
		query = entityManager.createNamedQuery("tagging.tagFrequencyByResourceAndLanguageFromTagRatings");
		query.setParameter("resource", resource);
		query.setParameter("language", localeSelector.getLanguage());
		query.setParameter("threshold", threshold.doubleValue());		
		query.setMaxResults(maxQueryResults);
		List<TagFrequency> res2 = query.getResultList();
		
		//Merge		
		for (TagFrequency tagFrequency2 : res2) {
			TagFrequency found = null;
			for (TagFrequency tagFrequency : res) {
				if (tagFrequency.getName().equals(tagFrequency2.getName())) {
					found = tagFrequency;
					break;
				}
			}
			if (found != null) {
				found.setSize(found.getSize()+tagFrequency2.getSize());
			} else {
				res.add(tagFrequency2);
			}
		}
		
		Collections.sort(res);		
		
		// get maxSize
		
		Long maxSize=1L;
		for (TagFrequency tagFrequency : res) {
			if (tagFrequency.getSize() > maxSize) {
				maxSize = tagFrequency.getSize();
			}
		}
		// normalize tagSizes by maxSize
		for (TagFrequency tagFrequency : res) {
			Long normalizedSize = MAX_NORMALIZED_FREQUENCY * tagFrequency.getSize() / maxSize;
			tagFrequency.setSize(normalizedSize);
		}

		return res;
	}
	
	public List<TagFrequency> getTagCloudForSearchResultList(String resourceId) {
		if (!resourceId.equals(cachedResourceId)) {
			ArtResource resource = entityManager.find(ArtResource.class, Long.parseLong(resourceId));
			tagCloud = getTagCloudWithVirtualTags(resource, 20);
			cachedResourceId = resourceId;
		}
		return tagCloud;
	}
	
	public List<TagFrequency> getTagCloudWithVirtualTags(ArtResource resource, int maxQueryResults) {
		Long millis = System.currentTimeMillis();
		
		List<TagFrequency> tagCloud=getTagCloud(resource, threshold, maxQueryResults);

		List<Tag> virtualTags = entityManager.createNamedQuery("virtualTagging.tagsByResourceAndLanguage")
			.setParameter("resource", resource)
			.setParameter("language", localeSelector.getLanguage())
			.getResultList();
		for (Tag tag : virtualTags) {
			tagCloud.add(0, new TagFrequency(tag.getName(), new Long(MAX_NORMALIZED_FREQUENCY)));
		}

		if (tagCloud.size() > maxQueryResults)
			tagCloud=tagCloud.subList(0, maxQueryResults);

		Collections.shuffle(tagCloud);

		if (maxQueryResults == Integer.MAX_VALUE)
			log.info("Updated Tag Cloud for all tags in #0ms", (System.currentTimeMillis()-millis));
		else
			log.info("Updated Tag Cloud for maximum #1 tags in #0ms", (System.currentTimeMillis()-millis), maxQueryResults);
		return tagCloud;
	}
	
	@Factory("tagCloudAll")
	public void updateTagCloudAll(){
		tagCloudAll = getTagCloudWithVirtualTags(resource, Integer.MAX_VALUE);
	}
	
	
	@Factory("tagCloud") 
	public void updateTagCloud() {
		if (tagCloudAll == null)
			updateTagCloudAll();
		tagCloud = tagCloudAll.subList(0, Math.min(20, tagCloudAll.size()));		
	}
	
}
