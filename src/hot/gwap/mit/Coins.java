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

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

/**
 * @author Fabian Kneißl
 */
@Name("mitCoins")
@Scope(ScopeType.SESSION)
public class Coins implements Serializable {

	private static final long serialVersionUID = 1L;

	@Logger				private Log log;
	@In					private EntityManager entityManager;
	@In(required=false) private Person person;
	@In					private PokerScoring mitPokerScoring;
	
	public Integer getCurrentFunds() {
		if (person == null)
			return 0;
		else {
			return mitPokerScoring.getPersonScoreSum(person);
		}
	}
	
	public boolean canEnterStatement() {
		//return getCurrentFunds() >= 20;
		return true;
	}
}
