/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.model.action;

import gwap.model.resource.Location;
import gwap.model.resource.Resource;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Scope;

@NamedQueries({
	@NamedQuery(name="locationAssignment.scoreSumByPerson",
			query="select sum(la.score) from LocationAssignment la where la.person = :person"),
	@NamedQuery(name="locationAssignment.countByResourceAndLocation",
			query="select count(*) from LocationAssignment la where la.resource = :resource and la.location = :location and la.notEvaluated = false"),
	@NamedQuery(name="locationAssignment.countByResource",
			query="select count(*) from LocationAssignment la where la.resource = :resource and la.notEvaluated = false"),
	@NamedQuery(name="locationAssignment.countAndLocationByResource",
			query="select count(*), l.id from LocationAssignment la join la.location l " +
					"where la.resource = :resource and la.notEvaluated = false " +
					"group by l.id order by count(*) desc"),
	@NamedQuery(name="locationAssignment.byResource",
			query="from LocationAssignment la where la.resource = :resource and la.notEvaluated = false"),
	@NamedQuery(name="locationAssignment.byResourceAndPerson",
			query="from LocationAssignment la where la.resource = :resource and la.notEvaluated = false and la.person = :person"),
	@NamedQuery(name="locationAssignment.scoringSumByResourceAndLocation",
		query="select sum(lh.correlation) from LocationAssignment la join la.location.hierarchies lh " +
				"where la.resource = :resource and la.notEvaluated = false and lh.name = 'mit.scoring' and lh.sublocation = :location")
})

/**
 * A location is assigned to a statement. It can have a certain type if,
 * e.g., it is set by administrators
 * 
 * @author Fabian Kneißl
 */
@Entity
//@Name("locationAssignment")
@Scope(ScopeType.EVENT)
public class LocationAssignment extends Action {
	
	private static final long serialVersionUID = 1L;

	@ManyToOne	protected Resource resource;
	@ManyToOne	protected Location location;
	
	private Boolean notEvaluated = false;
	
	public Resource getResource() {
		return resource;
	}
	public void setResource(Resource resource) {
		this.resource = resource;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public Boolean isNotEvaluated() {
		return notEvaluated;
	}
	public void setNotEvaluated(Boolean notEvaluated) {
		this.notEvaluated = notEvaluated;
	}
	
}
