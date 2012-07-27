/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.mit;

import gwap.game.AbstractGameSessionBean;
import gwap.model.action.Action;
import gwap.model.action.LocationAssignment;
import gwap.model.resource.Location;
import gwap.model.resource.Statement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;

/**
 * @author Fabian Kneißl
 */
@Name("mitStatementLabelerBean")
@Scope(ScopeType.CONVERSATION)
public class StatementLabelerBean extends AbstractGameSessionBean {
	private static final long serialVersionUID = 1L;

	// TODO: create audio statement
	@In(create=true)		private Statement statement;
	@In						private StatementBean mitStatementBean;
	
	@Out
	private List<Location> breadcrumbLocations = new ArrayList<Location>();
	
	private Long locationId;
	
	@Override
	public void startGameSession() {
		startGameSession("mitStatementLabeler");
	}
	
	@Override
	public void startRound() {
		super.startRound();
		statement = mitStatementBean.updateStatement();
		gameRound.getResources().add(statement);
		locationId = null;
	}
	
	public void assignLocation() {
		if (locationId == null)
			return;
		Location location = entityManager.find(Location.class, locationId);
		if (location == null)
			return;
		LocationAssignment sla = new LocationAssignment();
		sla.setCreated(new Date());
		sla.setLocation(location);
		sla.setPerson(person);
		sla.setResource(statement);
		sla.setGameRound(gameRound);
		entityManager.persist(sla);
		gameRound.getActions().add(sla);
		log.info("Assigned location #0 to statement #1", location, statement);
	}
	
	public List<Action> getAssignedLocations() {
		return gameRound.getActions();
	}

	public Long getLocationId() {
		return locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}
	
	public List<Location> addToBreadcrumbLocation(Long locationId) {
		Location l = entityManager.find(Location.class, locationId);
		if (l != null)
			breadcrumbLocations.add(l);
		return breadcrumbLocations;
	}
	
	public List<Location> navigateToBreadcrumbLocation(Long locationId) {
		for (int i = 0; i < breadcrumbLocations.size(); i++) {
			if (breadcrumbLocations.get(i).getId().equals(locationId)) {
				for (int j = i+1; j < breadcrumbLocations.size(); j++) {
					breadcrumbLocations.remove(j);
				}
				break;
			}
		}
		return breadcrumbLocations;
	}
	
}
