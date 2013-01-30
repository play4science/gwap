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

import gwap.model.Source;

import java.io.Serializable;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

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
 * Define custom layouts for e.g. Kunsthalle Karlsruhe which have a specific image source
 * 
 * @author Fabian Kneißl
 */
@Name("customSourceBean")
@Scope(ScopeType.SESSION)
public class CustomSourceBean implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@In
	EntityManager entityManager;
	
	@Logger
	Log log;
	
	@In(required=false)@Out(required=false)
	Source customSource;
	
	SolrQuery customSearch;
	
	public void setSource(String sourceName) {
		Query query = entityManager.createNamedQuery("source.byName");
		query.setParameter("name", sourceName);
		try {
			customSource = (Source) query.getSingleResult();
			log.info("Custom source is now #0 (#1)", customSource.getId(), customSource.getName());
		} catch (NoResultException e) {
			customSource = null;
			log.info("Custom source could not be set to #0", sourceName);
		}
		log.info("custom source:" + sourceName);
	}
	
	public void reset() {
		customSource = null;
		customSearch = null;
		log.info("reset custom source");
		try {
			String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
			Conversation.instance().endBeforeRedirect();
			Redirect redirect = Redirect.instance();
			redirect.setViewId(viewId);
			redirect.setConversationPropagationEnabled(false);
			redirect.execute();
		} catch (Throwable t) { }
	}
	
	public boolean getCustomized() {
		return customSource != null;
	}

	public Query query(String namedQuery) {
		Query query;
		if (getCustomized()) {
			query = entityManager.createNamedQuery(namedQuery + "Custom");
			query.setParameter("source", customSource);
		} else {
			query = entityManager.createNamedQuery(namedQuery);
		}
		return query;
	}

	public Source getCustomSource() {
		return customSource;
	}
	
	/**
	 * Source is SOLR and not database
	 */
	public boolean isSearchSource() {
		return customSearch != null;
	}

	public SolrQuery getCustomSearch() {
		return customSearch;
	}

	public void setCustomSearch(SolrQuery customSearch) {
		this.customSearch = customSearch;
	}
	
	
}
