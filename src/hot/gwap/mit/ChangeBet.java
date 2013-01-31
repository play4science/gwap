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

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.Log;

/**
 * @author Andre Reichstaller
 */
@Name("mitChangeBet")
@Scope(ScopeType.PAGE)
public class ChangeBet implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Logger					Log log;
	@In						private EntityManager entityManager;
	@In                     private FacesMessages facesMessages;
	
	@In(required = false)
	private Bet selectedBet;
	
	private Integer points;
	
	
	public void changeBet(Bet bet){
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(bet.getCreated());
		calendar.add(Calendar.HOUR, 24);

		if (Math.abs(points - bet.getCurrentMatch()) <= 10)
			facesMessages.addFromResourceBundle(Severity.ERROR, "bet.change.differenceTooLow");
		else if (!calendar.before(GregorianCalendar.getInstance()))
			facesMessages.addFromResourceBundle(Severity.ERROR, "bet.change.timeDifferenceTooShort");
		else {
			log.info("Changed bet #0 to #1 by #2", bet, points, bet.getPerson());
			Bet newBet = new Bet();
			//initialize
			newBet.setCreated(new Date());
			newBet.setPerson(bet.getPerson());
			newBet.setGameRound(bet.getGameRound());
			//rest
			newBet.setLocation(bet.getLocation());
			newBet.setResource(bet.getResource());
			newBet.setNotEvaluated(bet.isNotEvaluated());
	
			newBet.setPoints(points);
			entityManager.persist(newBet);
			
			bet = entityManager.find(Bet.class, bet.getId());
			bet.setNotEvaluated(true);
			bet.setRevisedBet(newBet);
			
			entityManager.flush();
			Events.instance().raiseEvent("mit.betList.update");
		}
	}
	
	public Integer getPoints() {
		if (selectedBet != null)
			points = selectedBet.getPoints();
		else
			points = 0;
		return points;
	}

	public void setPoints(Integer points) {
		this.points = points;
	}
	
}
	

	
	
