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

package gwap.mit;

import gwap.model.Person;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

/**
 * Caches a list of audio resources which are can be shown to the user
 * during one session
 * 
 * @author Fabian Kneißl
 */
@Name("mitAudioResourceCandidatesCacheBean")
@Scope(ScopeType.SESSION)
public class AudioResourceCandidatesCacheBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Create                  public void init()    { log.info("Creating");  loadAtLeastTaggedResources(); }
	@Logger				private Log log;
	@In					private EntityManager entityManager;
	@In(create=true)    private Person person;
	
	private List<Long> atLeastTaggedResourcesCandidates;
	
	/**
	 * Pre-compute resource candidates. Computations between game rounds to determine
	 * a new resource would last too long. 
	 */
	@SuppressWarnings("unchecked")
	public void loadAtLeastTaggedResources() {
		log.info("Loading atLeastTaggedResources");
		//FIXME: improve
		Query query = entityManager.createNamedQuery("audioResource.randomEnabledId");
		query.setParameter("person", person);
		atLeastTaggedResourcesCandidates = (List<Long>) query.getResultList();
	}
	
	public Long randomResourceId() {
//		if (atLeastTaggedResourcesCandidates.isEmpty())
//			loadAtLeastTaggedResources();
		
		int candidateListSize = atLeastTaggedResourcesCandidates.size();
		if (candidateListSize == 0)
			return null;
				
		log.info("Choosing random resource out of #0", candidateListSize);
		Long resourceId = atLeastTaggedResourcesCandidates.remove(0);
		return resourceId;
	}

}
