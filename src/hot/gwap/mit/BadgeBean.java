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

import gwap.model.Badge;
import gwap.model.Person;
import gwap.widget.HighscoreBean;
import gwap.wrapper.HighscoreSet;

import java.util.List;

import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * @author kneissl
 */
@Name("mitBadgeBean")
@Scope(ScopeType.PAGE)
@AutoCreate
public class BadgeBean extends gwap.widget.BadgeBean {
	
	private static final long serialVersionUID = 1L;
	
	private static final int MIN_POINTS_LOCATION_ASSIGNMENT = 3;
	private static final int MIN_POINTS_BET = 40;
	
	@In private HighscoreBean highscoreBean;
	
	private Integer nrLocationAssignmentsForNextBadge, nrBetsForNextBadge;
	
	public void checkAndAward() {
		Badge b = getNextBestBadge();
		if (b.getWorth() <= 3) { // for location assignment
			if (getNrLocationAssignmentsForNextBadge() <= 0) {
				person = entityManager.find(Person.class, person.getId());
				person.getBadges().add(b);
				log.info("#0 reached next badge #1", person, b);
			}
		} else if (b.getWorth() == 4) {
			if (getNrBetsForNextBadge() <= 0) {
				person = entityManager.find(Person.class, person.getId());
				person.getBadges().add(b);
				log.info("#0 reached next badge #1", person, b);
			}
		} else if (b.getWorth() == 5) {
			List<HighscoreSet> highscores = highscoreBean.getHighscores();
			for (HighscoreSet highscoreSet : highscores) {
				if (highscoreSet.getGameType().getName().equals("mitRecognize")) {
					if (highscoreSet.getHighscoreAll().get(0).getPersonId().equals(person.getId())) {
						person.getBadges().add(b);
						log.info("#0 reached next badge #1", person, b);
					}
				}
			}
		}
	}
	
	public String getDescriptionForNextBadge() {
		getNextBestBadge();
		if (nextBestBadge != null) {
			String message = messages.get("badge."+nextBestBadge.getWorth()+".earn");
			if ((nextBestBadge.getWorth() == 2 || nextBestBadge.getWorth() == 3) &&
					getNrLocationAssignmentsForNextBadge() == 1
					|| nextBestBadge.getWorth() == 4 && getNrBetsForNextBadge() == 1)
				message = messages.get("badge."+nextBestBadge.getWorth()+".earn.singular");
			return message;
		} else
			return "";
	}
	
	public Integer getNrLocationAssignmentsForNextBadge() {
		Badge b = getNextBestBadge();
		if (b == null)
			return null;
		if (nrLocationAssignmentsForNextBadge == null) {
			int minLAsForNextBadge = b.getCondition().intValue();
			// Get number of successful location assignments
			Query query = entityManager.createNamedQuery("locationAssignment.countByPersonMinimumScore");
			query.setParameter("person", person);
			query.setParameter("minScore", MIN_POINTS_LOCATION_ASSIGNMENT);
			int nrLAs = ((Number) query.getSingleResult()).intValue();
			nrLocationAssignmentsForNextBadge = minLAsForNextBadge - nrLAs;
		}
		return nrLocationAssignmentsForNextBadge;
	}
	
	public Integer getNrBetsForNextBadge() {
		Badge b = getNextBestBadge();
		if (b == null)
			return null;
		if (nrBetsForNextBadge == null) {
			int minBetsForNextBadge = b.getCondition().intValue();
			// Get number of successful location assignments
			Query query = entityManager.createNamedQuery("bet.countByPersonMinimumScore");
			query.setParameter("person", person);
			query.setParameter("minScore", MIN_POINTS_BET);
			int nrBets = ((Number) query.getSingleResult()).intValue();
			nrBetsForNextBadge = minBetsForNextBadge - nrBets;
		}
		return nrBetsForNextBadge;
	}
	
}
