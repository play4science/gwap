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

import gwap.model.StatementsTeaser;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Identity;

/**
 * @author Fabian Kneißl
 */
@Name("mitStatementsTeaserBean")
@Scope(ScopeType.PAGE)
public class StatementsTeaserBean implements Serializable {
	
	private static final long serialVersionUID = -7017107606338056199L;

	@In
	private EntityManager entityManager;
	
	@Logger
	private Log log;
	
	@Out
	private StatementsTeaser statementsTeaser;
	
	@RequestParameter
	private Long statementsTeaserId;

	
	@Factory("statementsTeaser")
	public StatementsTeaser getLatestStatementsTeaser() {
		try {
			// For admins, allow access by ID
			if (statementsTeaserId != null && Identity.instance().hasPermission("metropolitalia","view-admin-interface"))
				statementsTeaser = entityManager.find(StatementsTeaser.class, statementsTeaserId);
			else
				statementsTeaser = (StatementsTeaser) entityManager.createNamedQuery("statementsTeaser.latestByPublicationDate")
					.setMaxResults(1).getSingleResult();
			return statementsTeaser;
		} catch (NoResultException e) {
			log.info("Could not find a StatementsTeaser that is currently published");
			return null;
		}
	}

	public StatementsTeaser getStatementsTeaser() {
		return statementsTeaser;
	}

	public Long getStatementsTeaserId() {
		return statementsTeaserId;
	}

	public void setStatementsTeaserId(Long statementsTeaserId) {
		this.statementsTeaserId = statementsTeaserId;
	}

}
