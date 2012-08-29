/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.search;

import gwap.model.resource.ArtResource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.solr.client.solrj.SolrQuery;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.faces.Redirect;
import org.jboss.seam.log.Log;

/**
 * @author Christoph Wieser & Fabian Kneißl
 */
@Name("detailedSearch")
@Scope(ScopeType.CONVERSATION)
public class DetailedSearch extends SolrSearchBean {

	private static final long serialVersionUID = -5805789047740851265L;
	
	@Logger
	private Log log;

	@In @Out                 protected QueryBean queryBean;

	private static final Pattern p = Pattern.compile("\"[^\"]+\"|[^\" ]+");
	
	@Out(required=false)
	protected ArtResource resource;
	
	@Override
	public SolrQuery generateQuery() {
		StringBuilder query = new StringBuilder();
		
		//FIXME: alle Lucene Special chars escapen
		
		String language = localeSelector.getLanguage();
		query.append(parseField(queryBean.getTags(), "tag_"+language));
		query.append(parseField(queryBean.getArtist(), "artist"));
		query.append(parseField(queryBean.getTitle(), "title"));
		query.append(parseField(queryBean.getLocation(), "location_institution"));
		query.append(parseField(queryBean.getYear(), "datecreated"));
		
		log.info("Detailed search: #0", query.toString());
		
		SolrQuery solrQuery = new SolrQuery(query.toString());
		solrQuery.setParam("defType", "edismax");
		return solrQuery;
	}

	@Override
	public void search() {
		dirty = true;
		Conversation.instance().endBeforeRedirect();
		Redirect redirect = Redirect.instance();
		redirect.setViewId("/detailedSearchResults.xhtml");
		queryBean.setNotEmptyParameters(redirect);
		redirect.setConversationPropagationEnabled(false);
		redirect.execute();
	}
	
	private String parseField(String field, String solrField) {
		String string = "";
		if (field != null && field.length() > 0) {
			Matcher matcher = p.matcher(field);
			while (matcher.find()) {
				string += solrField+":" + matcher.group() + " ";
			}
		}
		return string;
	}
}
