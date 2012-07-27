/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
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
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.log.Log;

/**
 * @author Fabian Kneißl
 */
@Name("mitAdminStatementsTeaserHome")
public class StatementsTeaserHome extends EntityHome<StatementsTeaser> {
	
	@RequestParameter		Long statementsTeaserId;
	
	@Logger                 private Log log;
	
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
		fixStatementList();
		return super.update();
	}
	
	@Override
	public String persist() {
		fixStatementList();
		return super.persist();
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