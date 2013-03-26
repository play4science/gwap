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

package gwap.rest;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.log.Log;

/** 
 * Access to Artigo data via an XML REST interface
 * 
 * @author Fabian Kneißl
 */
@Name("restArtigoData")
@Scope(ScopeType.APPLICATION)
@Path("/artigodata")
@Restrict("#{s:hasPermission('artigo','rest-data')}")
public class ArtigoData implements Serializable {
	private static final long serialVersionUID = -5620728927856589759L;
	@Logger    private Log log;
	@In        private EntityManager entityManager;
	
	@GET
	@Path("/tags")
	@Produces("application/xml") //TODO: MediaType.APPLICATION_XML instead of "application/xml" 
	@SuppressWarnings("unchecked")
	public String getTagData(@QueryParam("dataset") String dataset,  @DefaultValue("2") @QueryParam("threshold") Long threshold,
			@QueryParam("language") String language) {
		log.info("Accessed /artigodata/tags?dataset=#0 to retrieve tag data as xml", dataset);
		try {
			Query q = entityManager.createNamedQuery("tagging.tagFrequencyBySource");
			if (language != null)
				q = entityManager.createNamedQuery("tagging.tagFrequencyBySourceAndLanguage").setParameter("language", language);
			q.setParameter("source", dataset);
			q.setParameter("threshold", Math.max(2L, threshold));
			StringBuilder xml = new StringBuilder();
			xml.append("<database dataset=\""+dataset+"\">\n");
			Object artResourceId = null;
			List<Object[]> resultList = q.getResultList();
			for (Object[] row : resultList) {
				if (row[0] == null)
					continue;
				if (!row[0].equals(artResourceId)) {
					if (artResourceId != null)
						xml.append("  </artwork>\n");
					xml.append("  <artwork id=\""+row[0]+"\">\n");
					artResourceId = row[0];
				}
				xml.append("    <tag name=\""+(row[1].toString().toLowerCase())+"\" language=\""+row[2]+"\" count=\""+row[3]+"\"/>\n");
			}
			if (artResourceId != null)
				xml.append("  </artwork>\n");
			xml.append("</database>");
			log.info("Finished rendering xml");
			return xml.toString();
		} catch (Exception e) {
			log.warn("Exception", e);
			return null;
		}
	}

}
