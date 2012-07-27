/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.mit.admin;

import gwap.model.Person;
import gwap.model.action.Bet;
import gwap.model.action.LocationAssignment;
import gwap.model.resource.Location;
import gwap.model.resource.Statement;
import gwap.model.resource.StatementToken;
import gwap.tools.StatementHelper;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.persistence.PersistenceProvider;

/**
 * @author Fabian Kneißl
 */
@Name("mitAdminStatementHome")
public class StatementHome extends EntityHome<Statement> {
	
	@RequestParameter		Long statementId;
	@RequestParameter		Long assignmentId;
	
	@In						private FacesMessages facesMessages;
	
	private String text;
	
	private Long locationId;
	
	@DataModelSelection("locationAssignments")
	@Out(required=false)	private Bet bet;
	@DataModel("locationAssignments")
							private List<Bet> locationAssignments;
	
	@In						private Person person;
	
	@Override
	public Object getId() {
		if (statementId == null)
			return super.getId();
		else
			return statementId;
	}

	@Override @Begin(join=true)
	public void create() {
		super.create();
		if (getInstance() != null)
			text = getInstance().asText();
		if (statementId == null) {
			setInstance(new Statement());
			getInstance().setEnabled(true);
		}
	}
	
	/**
	 * see {@link EntityHome.update()}
	 */
	@Override @Transactional
	public String update() {
		if (text.isEmpty()) {
			facesMessages.addToControlFromResourceBundle("text", "javax.faces.component.UIInput.REQUIRED");
			return "";
		}
		joinTransaction();
		
		StatementHelper.createStatementTokens(getInstance(), text, getEntityManager());
		if (!getInstance().asText().equals(text)) {
			for (StatementToken statementToken : getInstance().getStatementTokens())
				getEntityManager().remove(statementToken);
			getInstance().setStatementTokens(new ArrayList<StatementToken>());
			StatementHelper.createStatementTokens(getInstance(), text, getEntityManager());
		}
		
		getEntityManager().flush();
		updatedMessage();
		raiseAfterTransactionSuccessEvent();
		return "updated";
	}
	
	/**
	 * see {@link EntityHome.persist()}
	 */
	@Override @Transactional
	public String persist() {
		getInstance().setCreator(person);
		getEntityManager().persist(getInstance());

		StatementHelper.createStatementTokens(getInstance(), text, getEntityManager());
		
		getEntityManager().flush();
		assignId(PersistenceProvider.instance().getId(getInstance(),
				getEntityManager()));
		createdMessage();
		raiseAfterTransactionSuccessEvent();
		return "persisted";
	}
	
	public void addLocation() {
		if (locationId != null && 
				(bet == null || bet.getId() == null ||
						bet.getLocation().getId() != locationId)) {
			if (!isManaged())
				persist();
			Location location = getEntityManager().find(Location.class, locationId);
			// Check if location is already added to getInstance()
			Query query = getEntityManager().createNamedQuery("bet.predefined");
			query.setParameter("location", location).setParameter("resource", getInstance());
			if (query.getResultList().size() == 0) {
				getLog().info("Adding Location #0 to Statement #1", location, getInstance());
				bet = new Bet();
				bet.setLocation(location);
				bet.setResource(getInstance());
				bet.setPerson(person);
				getEntityManager().persist(bet);
			} else
				getLog().info("Did not add Location #0 to Statement #1 because it is already assigned", location, getInstance());
		}
	}
	
	public void deleteAssignment() {
		if (assignmentId != null) {
			bet = getEntityManager().find(Bet.class, assignmentId);
			getInstance().getLocationAssignments().remove(bet);
			getEntityManager().remove(bet);
		}
	}
	
	public String getText() {
		if (text == null)
			text = getInstance().asText();
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public List<LocationAssignment> getLocationAssignments() {
		return getInstance().getLocationAssignments();
	}

	public Long getLocationId() {
		return locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}
	
	
}