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
import gwap.model.action.Bet;
import gwap.model.resource.Location;
import gwap.model.resource.Statement;
import gwap.tools.StatementHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

/**
 * @author Fabian Kneißl
 */
@Name("mitNewStatement")
@Scope(ScopeType.PAGE)
public class NewStatement implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Logger					Log log;
	
	@In						protected EntityManager entityManager;
	@In(create=true)		protected Person person;
	@In						protected FacesMessages facesMessages;
	@In						protected Coins mitCoins;
	@In(required=false)@Out(required=false) protected Long locationId;
	
	@Out
	protected List<Location> breadcrumbLocations = new ArrayList<Location>();
	protected String text;
	protected String standardText;
	protected Integer points;
	protected Statement statement;

	public String createStatement() {
		if (statement == null) {
			if (text != null && text.length() > 2) {
				if (standardText != null && standardText.length() > 2) {
					text = text.trim();
					standardText = standardText.trim();
					Query query = entityManager.createNamedQuery("statement.byText").setParameter("text", text);
					if (query.getResultList().size() > 0) {
						statement = (Statement) query.getResultList().get(0);
						log.info("Statement already exists: '#0' (id: #1)", text, statement.getId());
					} else {
						log.info("Creating statement '#0'", text);
						statement = new Statement();
						statement.setText(text);
						statement.setCreator(person);
						statement.setEnabled(true);
						statement.setCreateDate(new Date());
						entityManager.persist(statement);
						StatementHelper.createStatementTokens(statement, text, entityManager);
						StatementHelper.createStatementStandardTokens(statement, standardText, entityManager);
						entityManager.flush();
						log.info("#0 created", statement);
					}
				} else
					facesMessages.addToControlFromResourceBundle("standardText", "game.newstatement.standardTextTooShort");
			} else
				facesMessages.addToControlFromResourceBundle("text", "game.newstatement.tooShort");
		}
		// Now, assign a location
		if (statement != null) {
			if (assignLocation()) {
				return "/newstatementcreated.xhtml";
			} else
				facesMessages.addToControlFromResourceBundle("text", "game.newstatement.selectLocation");
		}
		return null;
	}
	
	public boolean assignLocation() {
		log.info("Trying to assign locationId #0 to statement #1", locationId, statement);
		if (locationId == null || locationId <= 0)
			return false;
		Location location = entityManager.find(Location.class, locationId);
		if (location == null)
			return false;
		Bet bet = new Bet();
		bet.setCreated(new Date());
		bet.setLocation(location);
		bet.setResource(statement);
		bet.setPerson(person);
		bet.setPoints(points);
		entityManager.persist(bet);
		log.info("Assigned location #0 to statement #1", location, statement);
		return true;
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
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getStandardText() {
		return standardText;
	}
	public void setStandardText(String standardText) {
		this.standardText = standardText;
	}
	public Integer getPoints() {
		return points;
	}
	public void setPoints(Integer points) {
		this.points = points;
	}
	
}
