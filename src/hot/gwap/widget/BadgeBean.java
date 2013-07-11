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

package gwap.widget;

import gwap.model.Badge;
import gwap.model.Person;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

/**
 * @author kneissl
 */
@Name("badgeBean")
@Scope(ScopeType.SESSION)
public class BadgeBean implements Serializable {
	
	@In(required=false) protected Person person;
	@In protected EntityManager entityManager;
	@In protected String platform;
	@Logger protected Log log;
	@In(create=true)							protected Map<String, String> messages;
	
	protected Badge bestOwnedBadge, nextBestBadge; // for caching
	protected List<Badge> personBadges; // for caching
	protected boolean nextBestBadgeCalculated;
	
	public Badge getBestOwnedBadge() {
		if (bestOwnedBadge == null) {
			List<Badge> badges = getPersonBadges();
			if (badges.size() == 0)
				bestOwnedBadge = initialBadge();
			else
				bestOwnedBadge = badges.get(0);
		}
		return bestOwnedBadge;
		
	}
	
	protected void resetCache() {
		bestOwnedBadge = null;
		nextBestBadge = null;
		personBadges = null;
		nextBestBadgeCalculated = false;
	}
	
	public Badge getNextBestBadge() {
		if (nextBestBadge == null && !nextBestBadgeCalculated) {
			if (getPersonBadges().size() == 0) {
				Query query = entityManager.createNamedQuery("badge.byPlatform");
				query.setParameter("platform", platform);
				query.setMaxResults(2);
				List<Badge> resultList = query.getResultList();
				if (resultList.size() > 1)
					nextBestBadge = resultList.get(1);
			} else {
				Query query = entityManager.createNamedQuery("badge.nextForPerson");
				query.setParameter("person", person);
				query.setParameter("platform", platform);
				query.setMaxResults(1);
				try {
					nextBestBadge = (Badge) query.getSingleResult();
				} catch (NoResultException e) {
					log.info("No next best badge exists for person #0", person);
				}
			}
			nextBestBadgeCalculated = true;
		}
		return nextBestBadge;
	}
	
	public String getDescriptionForNextBadge() {
		getNextBestBadge();
		if (nextBestBadge != null)
			return messages.get("badge."+nextBestBadge.getWorth()+".earn");
		else
			return "";
	}
	
	protected List<Badge> getPersonBadges() {
		if (personBadges == null) {
			Query query = entityManager.createNamedQuery("badge.bestForPerson");
			query.setParameter("person", person);
			personBadges = query.getResultList();
		}
		return personBadges;
	}
	
	private Badge initialBadge() {
		Query query = entityManager.createNamedQuery("badge.byPlatform");
		query.setParameter("platform", platform);
		query.setMaxResults(1);
		Badge badge = (Badge) query.getSingleResult();
		return badge;
	}
}
