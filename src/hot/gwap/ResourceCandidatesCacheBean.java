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

package gwap;

import gwap.tools.CustomSourceBean;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

/**
 * Caches a list of resources which are can be shown to the user
 * during one session
 * 
 * @author Fabian Kneißl
 */
@Name("resourceCandidatesCacheBean")
@Scope(ScopeType.SESSION)
@Deprecated
public class ResourceCandidatesCacheBean implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Create                  public void init()    { log.info("Creating");  loadAtLeastTaggedResources(); }
	@Logger                  private Log log;
	@In(create=true)         private CustomSourceBean customSourceBean;
	
	
	private List<Long> atLeastTaggedResourcesCandidates;
	
	private Long minTaggings = 10L; // select resources for the game having at least 20 taggings;
	
	private int maxCachedResources = 100;
	
	/**
	 * Pre-compute resource candidates. Computations between game rounds to determine
	 * a new resource would last too long. 
	 */
	@SuppressWarnings("unchecked")
	public void loadAtLeastTaggedResources() {
		log.info("Loading atLeastTaggedResources");
		Query query = customSourceBean.query("artResource.atLeastTaggedResourceId");
		query.setMaxResults(maxCachedResources);
		query.setParameter("minTaggings", minTaggings);
		atLeastTaggedResourcesCandidates = (List<Long>) query.getResultList();
	}
	
	public Long randomResourceId() {
		// Choose pre-computed candidate id randomly.
		Random random = new Random();
		if (atLeastTaggedResourcesCandidates.isEmpty())
			loadAtLeastTaggedResources();
		
		int candidateListSize = atLeastTaggedResourcesCandidates.size();
		int chosenIndex = random.nextInt(candidateListSize);
		
		log.info("Choosing random resource #0 of #1", chosenIndex, candidateListSize);
		
		//FIXME: what happens if there are no resources left? 
		
		// remove consumed id from candidate list
		Long resourceId = atLeastTaggedResourcesCandidates.remove(chosenIndex);
		return resourceId;
	}
}
