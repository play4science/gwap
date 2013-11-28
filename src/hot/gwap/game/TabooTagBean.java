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

package gwap.game;

import gwap.ResourceBean;
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
 * @author Christoph Wieser
 */

@Name("tabooTagBean")
@Scope(ScopeType.PAGE)
public class TabooTagBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Create  public void init()    { log.info("Creating"); }
	@Destroy public void destroy() { log.info("Destroying"); }

	@Logger                  private Log log;
	@In                      private EntityManager entityManager;
	@In                      private LocaleSelector localeSelector;
	@In                      private ResourceBean resourceBean;
	@Out                     private List<TagFrequency> tabooTags;
	
	private int limit= 7;
	
	@Factory("tabooTags")
	public void updateTabooTags() {
		Long threshold = 2L;
		Query query = entityManager.createNamedQuery("tagging.tagFrequencyByResourceAndLanguage");
		query.setParameter("resource", resourceBean.getResource());
		query.setParameter("language", localeSelector.getLanguage());
		query.setParameter("threshold", threshold);
		query.setMaxResults(limit);
		tabooTags = query.getResultList();
	}
}
