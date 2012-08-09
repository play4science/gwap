/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.search;

import gwap.model.resource.ArtResource;
import gwap.widget.SolrSearchBean;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.solr.client.solrj.SolrQuery;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

/**
 * @author Christoph Wieser & Fabian Kneißl
 */
@Name("detailedSearch")
@Scope(ScopeType.CONVERSATION)
public class DetailedSearch extends SolrSearchBean {

	@Logger
	private Log log;
	
	private String tags;
	private String artist;
	private String title;
	private String location;
	private String year;
	private static final Pattern p = Pattern.compile("\"[^\"]+\"|[^\" ]+");
	
	@Out(required=false)
	protected ArtResource resource;
	
	@Override
	protected SolrQuery generateQuery() {
		StringBuilder query = new StringBuilder();
		
		//FIXME: alle Lucene Special chars escapen
		
		String language = localeSelector.getLanguage();
		query.append(parseField(tags, "tag_"+language));
		query.append(parseField(artist, "artist"));
		query.append(parseField(title, "title"));
		query.append(parseField(location, "location"));
		query.append(parseField(location, "institution"));
		query.append(parseField(year, "datecreated"));
		
		log.info("Detailed search: #0", query.toString());
		
		SolrQuery solrQuery = new SolrQuery(query.toString());
		solrQuery.setParam("defType", "edismax");
		return solrQuery;
	}

	public String updateSearch() {
		dirty = true;
		return "/detailedSearchResults.xhtml";
	}
	
	@Override
	public void search() {
		super.search();
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
	
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
}
