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

package gwap.elearn;

import gwap.model.GameRound;
import gwap.wrapper.BackstageAnswer;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * @author Fabian Kneißl
 */
@Name("elearnResults")
@AutoCreate
@Scope(ScopeType.APPLICATION)
public class Results implements Serializable {

	@In  protected EntityManager entityManager;
	
	public List<BackstageAnswer> getTopCorrectAnswers(GameRound gameRound) {
		if (isGameRoundValid(gameRound)) {
			Query q = entityManager.createNamedQuery("tagging.topCorrectAnswersGeneral");
			q.setParameter("resourceId", gameRound.getResources().get(0).getId());
			q.setMaxResults(5);
			List<BackstageAnswer> list = q.getResultList();
			return list;
		} else
			return null;
	}
	
	public List<BackstageAnswer> getTopUnknownAnswers(GameRound gameRound) {
		if (isGameRoundValid(gameRound)) {
			Query q = entityManager.createNamedQuery("tagging.topUnknownAnswersGeneral");
			q.setParameter("resourceId", gameRound.getResources().get(0).getId());
			q.setMaxResults(5);
			List<BackstageAnswer> list = q.getResultList();
			return list;
		} else
			return null;
	}
	
	public List<BackstageAnswer> getTopWrongAnswers(GameRound gameRound) {
		if (isGameRoundValid(gameRound)) {
			Query q = entityManager.createNamedQuery("tagging.topWrongAnswersGeneral");
			q.setParameter("resourceId", gameRound.getResources().get(0).getId());
			q.setMaxResults(5);
			List<BackstageAnswer> list = q.getResultList();
			return list;
		} else
			return null;
	}
	
	private boolean isGameRoundValid(GameRound gameRound) {
		return gameRound.getResources().size() > 0 && gameRound.getResources().get(0) != null;
	}

}
