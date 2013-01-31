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
		gameRound.getResources().add(statement);
		locationId = null;
	}
	
	@Override
	protected void loadNewResource() {
		statement = mitStatementBean.updateStatement();		
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
