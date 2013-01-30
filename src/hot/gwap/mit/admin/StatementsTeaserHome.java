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

package gwap.mit.admin;

import gwap.model.StatementWithGeoPoint;
import gwap.model.StatementsTeaser;
import gwap.model.resource.GeoPoint;
import gwap.model.resource.Statement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.log.Log;

/**
 * @author Fabian Kneißl
 */
@Name("mitAdminStatementsTeaserHome")
public class StatementsTeaserHome extends EntityHome<StatementsTeaser> {
	
	@Logger                 private Log log;
	@In						private FacesMessages facesMessages;
	@RequestParameter		Long statementsTeaserId;
	
	private Long locationId;
	
	private List<StatementWithGeoPoint> currentStatements;
	
//	@DataModelSelection("locationAssignments")
//	@Out(required=false)	private Bet bet;
//	@DataModel("locationAssignments")
//							private List<Bet> locationAssignments;

	private List<Statement> allStatements;

	
	@Override
	public Object getId() {
		if (statementsTeaserId == null)
			return super.getId();
		else
			return statementsTeaserId;
	}

	@Override @Begin(join=true)
	public void create() {
		super.create();
		currentStatements = new ArrayList<StatementWithGeoPoint>();
		if (statementsTeaserId == null) {
			setInstance(new StatementsTeaser());
			addStatement();
		}
		for (StatementWithGeoPoint s : getInstance().getStatementList()) {
			StatementWithGeoPoint clone = new StatementWithGeoPoint();
			clone.setId(s.getId());
			clone.setGeoPoint(s.getGeoPoint());
			clone.setStatementsTeaser(getInstance());
			clone.setStatement(new Statement());
			clone.getStatement().setId(s.getStatement().getId());
			currentStatements.add(clone);
		}
	}
	
	@Override
	public String update() {
		if (!isStatementListValid())
			return null;
		fixStatementList();
		return super.update();
	}
	
	@Override
	public String persist() {
		if (!isStatementListValid())
			return null;
		fixStatementList();
		return super.persist();
	}
	
	private boolean isStatementListValid() {
		for (StatementWithGeoPoint s : currentStatements) {
			if (s.getStatement() == null || s.getStatement().getId() == null || s.getStatement().getId() <= 0) {
				facesMessages.addFromResourceBundle("admin.statementsTeaser.errorNoStatement");
				return false;
			}
			if (s.getGeoPoint() == null || s.getGeoPoint().getLatitude() == null || s.getGeoPoint().getLongitude() == null) {
				facesMessages.addFromResourceBundle("admin.statementsTeaser.errorNoGeoPoint");
				return false;
			}
		}
		return true;
	}

	private void fixStatementList() {
		if (isManaged()) {
			@SuppressWarnings("unchecked")
			List<StatementWithGeoPoint> actualList = getEntityManager().createNamedQuery("statementWithGeoPoint.byStatementsTeaser")
				.setParameter("statementsTeaser", getInstance())
				.getResultList();
			List<StatementWithGeoPoint> notFoundStatements = new ArrayList<StatementWithGeoPoint>();
			notFoundStatements.addAll(currentStatements);
			for (StatementWithGeoPoint s : actualList) {
				for (Iterator<StatementWithGeoPoint> iterator = notFoundStatements.iterator(); iterator.hasNext();) {
					StatementWithGeoPoint s2 = iterator.next();
					if (s.getId().equals(s2.getId())) {
						s.getGeoPoint().setLatitude(s2.getGeoPoint().getLatitude());
						s.getGeoPoint().setLongitude(s2.getGeoPoint().getLongitude());
						s.setStatement(getEntityManager().find(Statement.class, s2.getStatement().getId()));
						notFoundStatements.remove(s2);
						break;
					}
				}
			}
			for (StatementWithGeoPoint s : notFoundStatements) {
				addStatementToInstance(s);
			}
		} else {
			for (StatementWithGeoPoint s : currentStatements) {
				addStatementToInstance(s);
			}
		}
	}
	
	private void addStatementToInstance(StatementWithGeoPoint s) {
		s.setStatement(getEntityManager().find(Statement.class, s.getStatement().getId()));
		s.setStatementsTeaser(getInstance());
		getEntityManager().persist(s);
		getInstance().getStatementList().add(s);
	}
	
	public List<Statement> allStatements() {
		if (allStatements == null)
			allStatements = getEntityManager().createNamedQuery("statement.allEnabledSorted").getResultList();
		return allStatements;
	}
	
	public void addStatement() {
		StatementWithGeoPoint statementWithGeoPoint = new StatementWithGeoPoint();
		statementWithGeoPoint.setStatement(new Statement());
		statementWithGeoPoint.setGeoPoint(new GeoPoint());
		currentStatements.add(statementWithGeoPoint);
	}
	
	// Does not work
//	public void removeStatement(Long id) {
//		StatementWithGeoPoint s = getEntityManager().find(StatementWithGeoPoint.class, id);
//		for (int i = 0; i < currentStatements.size(); i++) {
//			if (id.equals(currentStatements.get(i).getId())) {
//				currentStatements.remove(i);
//				break;
//			}
//		}
//		if (s != null)
//			getEntityManager().remove(s);
//	}

	public List<StatementWithGeoPoint> getCurrentStatements() {
		return currentStatements;
	}

	public void setCurrentStatements(List<StatementWithGeoPoint> currentStatements) {
		this.currentStatements = currentStatements;
	}

	public Long getLocationId() {
		return locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}
	
	
}
