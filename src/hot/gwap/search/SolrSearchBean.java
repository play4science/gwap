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

package gwap.search;

import gwap.model.Person;
import gwap.model.SearchQuery;
import gwap.model.resource.ArtResource;
import gwap.tools.CustomSourceBean;
import gwap.widget.PaginationControl;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrException;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.faces.Redirect;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.log.Log;

@Name("solrSearchBean")
@Scope(ScopeType.CONVERSATION)
public class SolrSearchBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private static final long leastResultsForCustomGame = 100L;
	
	protected int RESULTS_PER_PAGE = 5;
	
	@Logger                  protected Log log;
	@Create                  public void init()    { log.info("Creating");   }
	@Destroy                 public void destroy() { log.info("Destroying"); }
	@In                      protected LocaleSelector localeSelector;
	@In                      protected EntityManager entityManager;
	@In(required=false)      protected Person person;
	@Out(required=false)     protected ArtResource resource;
	@In(create=true)         protected String platform;
	@In(create=true)         protected CustomSourceBean customSourceBean;
	
	@In(create=true)         protected SolrServer solrServer;
	
	@In(create=true) @Out    protected PaginationControl paginationControl;
	
	@In @Out                 protected QueryBean queryBean;
	
	@RequestParameter        protected Integer resultNumber;
	
	protected SolrDocumentList results;

	protected boolean dirty = true;
	
	/**
	 * Override this method to change the query behaviour
	 */
	public SolrQuery generateQuery() {
		if (isQueryEmpty())
			return null;
		String language = localeSelector.getLanguage();
		
		SolrQuery solrQuery = new SolrQuery(queryBean.getQueryString());
		solrQuery.setParam("defType", "dismax");
		String fields = "tag";
		if (language != null && language.length() == 2)
			fields += "_" + language;
		fields += " title^2.0 artist^2.0 teaser^0.2 location institution datecreated";
		solrQuery.setParam("qf", fields);
		return solrQuery;
	}
	
	public void submitQuery() {
		log.info("Updating Results");
		
		results = null;
		SolrQuery solrQuery = generateQuery();
		if (solrQuery == null)
			return;
		paginationControl.setResultsPerPage(RESULTS_PER_PAGE);
		solrQuery.setRows(paginationControl.getResultsPerPage());
		solrQuery.setStart(paginationControl.getFirstResult());
		try {
			QueryResponse response = solrServer.query(solrQuery, METHOD.POST);
			results = response.getResults();
			paginationControl.setNumResults(results.getNumFound());
			dirty = false;
			log.info("Got #0 results for query '#1'", results.getNumFound(), solrQuery.getQuery());
		} catch (SolrException e) {
			log.info("Could not complete query", e);
		} catch (SolrServerException e) {
			log.info("Could not complete query", e);
		}
	}
	
	public void show() {
		if (dirty)
			submitQuery();
		if (results != null && resultNumber != null && resultNumber >= 0 && resultNumber < results.size()) {
			long resourceId = Long.parseLong((String) results.get(resultNumber).getFieldValue("id"));
			resource = entityManager.find(ArtResource.class, resourceId);
		}
		log.info("Show solr result #0 on page #1 for query '#2' (#3)", resultNumber, paginationControl.getPageNumber(), queryBean.getQueryString(), resource);
	}
	public void search() {
		if (!isQueryEmpty()) {
			String queryString = queryBean.getQueryString();
			// End a current PageFlow if a conversation is active
			Conversation.instance().endBeforeRedirect();
			Redirect redirect = Redirect.instance();
			redirect.setViewId("/solrSearchResults.xhtml");
			queryBean.setNotEmptyParameters(redirect);
			redirect.setConversationPropagationEnabled(false);
			// Save Search Query
			SearchQuery searchQuery = new SearchQuery();
			searchQuery.setCreated(new Date());
			searchQuery.setQuery(queryString);
			searchQuery.setPlatform(platform);
			if (queryString.length() > 255)
				searchQuery.setQuery(queryString.substring(0, 250) + "[...]");
			searchQuery.setPerson(person);
			Map<String, String> requestHeaderMap = FacesContext.getCurrentInstance().getExternalContext().getRequestHeaderMap();
			String userAgent = requestHeaderMap.get("User-Agent");
			searchQuery.setUserAgent(userAgent);
			if (userAgent != null && userAgent.length() > 255)
				searchQuery.setUserAgent(userAgent.substring(0, 250) + "[...]");
			entityManager.persist(searchQuery);
			redirect.execute();
		}
	}
	protected boolean isQueryEmpty() {
		return queryBean.getQueryString() == null || queryBean.getQueryString().length() == 0;
	}
	public Integer getPageNumber() {
		return paginationControl.getPageNumber();
	}
	@RequestParameter
	public void setPageNumber(Integer pageNumber) {
		if (pageNumber != null && !pageNumber.equals(paginationControl.getPageNumber())) {
			paginationControl.setPageNumber(pageNumber);
			dirty = true;
		}
	}
	public SolrDocumentList getResults() {
		if (dirty)
			submitQuery();
		return results;
	}
	public boolean isEmptyResult() {
		if (dirty)
			submitQuery();
		return results == null || results.isEmpty();
	}
	public Integer getResultNumber() {
		return resultNumber;
	}
	public void setResultNumber(Integer resultNumber) {
		this.resultNumber = resultNumber;
	}
	public boolean customGameAllowed() {
		return results != null && results.getNumFound() >= leastResultsForCustomGame;
	}
	
	public String useQueryAsSource() {
		if (!customGameAllowed())
			throw new UnsupportedOperationException("Not enough results for a game");
		SolrQuery query = generateQuery();
		customSourceBean.setCustomSearch(query);
		log.info("Using Query #0 as Source, e.g. for games.", query.toString());

		// End a current PageFlow if a conversation is active
		Conversation.instance().endBeforeRedirect();
		Redirect redirect = Redirect.instance();
		redirect.setViewId("/taggingGame.xhtml");
		queryBean.setNotEmptyParameters(redirect);
		redirect.setConversationPropagationEnabled(false);
		
		return "/taggingGame.xhtml";
	}
	
}
