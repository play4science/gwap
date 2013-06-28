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
			query="select sum(la.score) from LocationAssignment la where (la.class = Bet and la.revisedBet is null or la.class=LocationAssignment) and la.person = :person"),
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
				"where la.resource = :resource and la.notEvaluated = false and lh.name = 'mit.scoring' and lh.sublocation = :location"),
	@NamedQuery(name="locationAssignment.countByPersonMinimumScore",
			query="select count(*) from LocationAssignment la where la.person = :person and la.score >= :minScore")
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
	
	/** 
	 * LocationAssignments can have the property notEvaluated set to true in
	 * order to exclude them from evaluation. This exclusion is for example
	 * useful in subclasses where the same LocationAssignment is specified.
	 */
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
