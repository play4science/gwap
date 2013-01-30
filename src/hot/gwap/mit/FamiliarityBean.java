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

package gwap.mit;

import gwap.model.GameRound;
import gwap.model.Person;
import gwap.model.action.Familiarity;
import gwap.model.resource.Statement;

import java.util.Date;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

/**
 * @author kneissl
 */
@Name("mitFamiliarity")
@Scope(ScopeType.PAGE)
public class FamiliarityBean {
	
	@Logger     private Log log;
	@In         private EntityManager entityManager;
	@In         private Statement statement;
	@In         private Person person;
	@In(required=false)@Out     private GameRound gameRound;
	
	private Familiarity familiarity;

	public void unfamiliar() {
		if (familiarity != null || gameRound == null || gameRound.getEndDate() != null)
			return;
		log.info("#0 rated as familiar=#1 by #2", statement, false, person);
		familiarity = new Familiarity();
		familiarity.setCreated(new Date());
		familiarity.setPerson(person);
		familiarity.setGameRound(gameRound);
		familiarity.setFamiliar(false);
		familiarity.setResource(statement);
		entityManager.persist(familiarity);
		gameRound.getActions().add(familiarity);
	}
	
}
