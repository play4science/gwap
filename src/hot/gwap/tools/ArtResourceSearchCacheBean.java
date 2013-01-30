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

package gwap.tools;

import gwap.model.resource.ArtResource;
import gwap.search.QueryBean;

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
	@In                       protected CustomSourceBean customSourceBean;
	@In                       private EntityManager entityManager;

	private SolrDocumentList results;
	
	private void updateCandidates() {
		SolrQuery solrQuery = customSourceBean.getCustomSearch();
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
		Long id = Long.parseLong(results.get(selected).getFieldValue("id").toString());
		return entityManager.find(ArtResource.class, id);
	}

}
