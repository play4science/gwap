/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.tools;

import gwap.model.resource.ArtResource;
import gwap.search.QueryBean;
import gwap.search.SolrSearchBean;

import javax.persistence.EntityManager;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;

/**
 * @author kneissl, wieser
 */
@Name("artResourceSearchCacheBean")
public class ArtResourceSearchCacheBean implements ArtResourceCacheBean {

	@Logger
	private Log log;

	@In(create=true)         protected SolrServer solrServer;
	@In                       protected QueryBean queryBean;
	@In                       protected SolrSearchBean solrSearchBean;
	@In                       private EntityManager entityManager;

	private SolrDocumentList results;
	
	private void updateCandidates() {
		SolrQuery solrQuery = solrSearchBean.generateQuery();
		try {
			QueryResponse response = solrServer.query(solrQuery, METHOD.POST);
			results = response.getResults();
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Resources should only be retrieved, if a tag was assigned several times.
	 * For the sake of simplicity, the SOLR database contains only terms fulfilling this precondition.
	 * That's why name is unused in this method but necessary to implement the interface ArtResourceCacheBean
	 * @param name originally specified the minimal number of tags for a resource to be taken into account for queries. 
	 */
	@Override
	public ArtResource getArtResource(String name) {
		if (results == null)
			updateCandidates();
		
		int selected = (int) (Math.random()*results.size());
		Long id = (Long) results.get(selected).getFieldValue("id");
		return entityManager.find(ArtResource.class, id);
	}

}
