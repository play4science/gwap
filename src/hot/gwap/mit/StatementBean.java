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

import gwap.ResourceAcquisitionType;
import gwap.model.Person;
import gwap.model.action.Bet;
import gwap.model.action.PokerBet;
import gwap.model.resource.Statement;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Identity;

/**
 * @author Fabian Kneißl
 */
@Name("mitStatementBean")
@Scope(ScopeType.CONVERSATION)
public class StatementBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Logger				private Log log;
	@In					private EntityManager entityManager;
	@In                 private FacesMessages facesMessages;
	@In(required=false)
	@Out(required=false)private Statement statement;
	@In(required=false)	private Person person;
	
	@RequestParameter   private Long statementId;
	
	private ResourceAcquisitionType acquisitionType;
	
	@Factory("statement")
	public Statement updateStatement() {
		statement = null;
		String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
		if (statementId != null) {
			if (person != null && Identity.instance().hasPermission("metropolitalia","view-admin-interface")) {
				byId(statementId);
				return statement;
			}
		}
		if (statement == null && viewId.equals("/recognize.xhtml")) {
			if (statementId != null) {
				Identity.instance().checkPermission("metropolitalia", "view-admin-interface");
				byId(statementId);
				if (statement != null) {
					Query q = entityManager.createNamedQuery("locationAssignment.byResourceAndPerson");
					q.setParameter("resource", statement);
					q.setParameter("person", person);
					if (q.getResultList().size() > 0) {
						statement = null;
						facesMessages.addFromResourceBundle("statement.alreadyPlayed");
					}
				}
			}
			if (statement == null)
				sensibleForLocationAssignment();
		}
		if (statement == null && viewId.equals("/poker.xhtml")) {
			sensibleForPoker();
		}
		if (statement == null) {
			random();
		}
		return statement;
	}
	
	public Statement updateAtLeastAssignedStatement() {
		statement = null;
		atLeastAssigned();
		if (statement == null)
			updateStatement();
		return statement;
	}
	
	public void random() {
		log.info("Update random statement");
		Query query = entityManager.createNamedQuery("statement.randomEnabled");
		query.setMaxResults(1);
		try {
			statement = (Statement) query.getSingleResult();
			acquisitionType = ResourceAcquisitionType.RANDOM;
			log.info("Statement is #0", statement);
			logPredefinedStatementLocations();
		} catch (NoResultException e) {
			statement = null;
			log.info("Could not retrieve a statemement");
		}
	}
	
	private void sensibleForLocationAssignment() {
		log.info("Update sensible for locationassignment statement");
		Query query;
		if (person != null)
			query = entityManager.createNamedQuery("statement.nextSensibleForLocationAssignmentByPerson").setParameter("person", person);
		else
			query = entityManager.createNamedQuery("statement.nextSensibleForLocationAssignment");
		query.setMaxResults(5);
		try {
			List<Long> list = query.getResultList();
			if (list.size() == 0)
				throw new NoResultException();
			int rnd = new Random().nextInt(list.size());
			long statementId = (Long) list.get(rnd);
			statement = entityManager.find(Statement.class, statementId);
			acquisitionType = ResourceAcquisitionType.SENSIBLE_FOR_LOCATIONASSIGNMENT;
			log.info("Statement is #0", statement);
			logPredefinedStatementLocations();
		} catch (NoResultException e) {
			statement = null;
			log.info("Could not retrieve a statemement");
		}
	}
	
	private void sensibleForPoker() {
		log.info("Update sensible for poker statement");
		Query query;
		if (person != null)
			query = entityManager.createNamedQuery("statement.nextSensibleForPokerByPerson").setParameter("person", person);
		else
			query = entityManager.createNamedQuery("statement.nextSensibleForPoker");
		query.setMaxResults(5);
		try {
			List<Long> list = query.getResultList();
			if (list.size() == 0)
				throw new NoResultException();
			int rnd = new Random().nextInt(list.size());
			long statementId = (Long) list.get(rnd);
			statement = entityManager.find(Statement.class, statementId);
			acquisitionType = ResourceAcquisitionType.SENSIBLE_FOR_POKER;
			log.info("Statement is #0", statement);
			logPredefinedPokerBetLocations();
		} catch (NoResultException e) {
			statement = null;
			log.info("Could not retrieve a statemement");
		}
	}
	
	private void atLeastAssigned() {
		log.info("Update atLeastAssigned statement");
		Query query;
		if (person != null)
			query = entityManager.createNamedQuery("statement.atLeastAssignedByPerson").setParameter("person", person);
		else
			query = entityManager.createNamedQuery("statement.atLeastAssigned");
		query.setParameter("minAssignments", 3L);
		query.setMaxResults(5);
		try {
			List<Long> list = query.getResultList();
			if (list.size() == 0)
				throw new NoResultException();
			int rnd = new Random().nextInt(list.size());
			long statementId = (Long) list.get(rnd);
			statement = entityManager.find(Statement.class, statementId);
			acquisitionType = ResourceAcquisitionType.AT_LEAST;
			log.info("Statement is #0", statement);
			logPredefinedStatementLocations();
		} catch (NoResultException e) {
			statement = null;
			log.info("Could not retrieve a statemement");
		}
	}
	
	private Statement byId(Long id) {
		statement = entityManager.find(Statement.class, id);
		acquisitionType = ResourceAcquisitionType.BY_ID;
		log.info("Update statement by id: #0", statement);
		logPredefinedStatementLocations();
		return statement;
	}
	
	private void logPredefinedStatementLocations() {
		if (log.isInfoEnabled() && statement != null) {
			@SuppressWarnings("unchecked")
			List<Bet> bets = entityManager.createNamedQuery("bet.byResource")
					.setParameter("resource", statement)
					.getResultList();
			if (bets.size() == 0)
				log.info("Statement #0 is not assigned to a location", statement);
			for (Bet bet : bets)
				log.info("Statement #0 is assigned to location #1", bet.getResource(), bet.getLocation());
		}
	}
	
	private void logPredefinedPokerBetLocations() {
		if (log.isInfoEnabled() && statement != null) {
			@SuppressWarnings("unchecked")
			List<PokerBet> bets = entityManager.createNamedQuery("pokerBet.byResource")
					.setParameter("resource", statement)
					.getResultList();
			if (bets.size() == 0)
				log.info("Statement #0 is not assigned to a location", statement);
			for (PokerBet bet : bets)
				log.info("Statement #0 is assigned to location #1", bet.getResource(), bet.getLocation());
		}
	}

	public boolean getExistsSensibleForPokerForceUpdate() {
		acquisitionType = null;
		return getExistsSensibleForPoker();
	}
	
	public boolean getExistsSensibleForPoker() {
		if (acquisitionType == null) {
			acquisitionType = ResourceAcquisitionType.NONE;
			sensibleForPoker();
		}
		return ResourceAcquisitionType.SENSIBLE_FOR_POKER.equals(acquisitionType);
	}
	
	public ResourceAcquisitionType getAcquisitionType() {
		return acquisitionType;
	}
}
