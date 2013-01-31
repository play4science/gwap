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

import gwap.model.action.Bet;
import gwap.widget.HighscoreBean;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;

/**
 * @author André Reichstaller
 */

@Name("mitHighscore")
public class Highscore {
	
	@In EntityManager entityManager;
	@Logger private Log log;
	private List<Bet> bestBets;
	private List<gwap.model.Highscore> bestUsers;

	public List<Bet> getBestBets() {
		if (bestBets == null) {
			Query q = entityManager.createNamedQuery("bet.byScore");
			q.setMaxResults(10);
			bestBets = q.getResultList();
		}
			
		return bestBets;
		
	}
	
	public List<gwap.model.Highscore> getBestUsers() {
		if (bestUsers == null) {
			Query q = entityManager.createNamedQuery("highscore.mit.byAllPersons");
			q.setMaxResults(10);
			q.setParameter("gametype", "mitRecognize");
			bestUsers = q.getResultList();
			HighscoreBean.modifyHighscore(bestUsers, entityManager);
		}
		
		return bestUsers;
	}
	


}
