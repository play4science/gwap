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

import gwap.model.action.LocationAssignment;
import gwap.model.resource.Statement;

import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.log.Log;

/**
 * @author Fabian Kneißl
 */
@Name("mitStatementListByAssignmentDate")
public class StatementListByAssignmentDate extends EntityQuery<LocationAssignment> {

	private static final long serialVersionUID = 1L;
	
	@Logger
	private Log log;

	private int maximumResults = 10;
	
	private Statement selectedStatement;

	public StatementListByAssignmentDate() {
		setEjbql("select la from LocationAssignment la join la.resource s where s.class = Statement and la.notEvaluated = false order by la.created desc");
		setMaxResults(maximumResults);
	}
	
	public int getMaximumResults() {
		return maximumResults;
	}

	public void setMaximumResults(int maximumResults) {
		this.maximumResults = maximumResults;
		setMaxResults(maximumResults);
	}
	
	public void showDetail(Long locationAssignmentId) {
		for (LocationAssignment la : getResultList()) {
			if (la.getId().equals(locationAssignmentId)) {
				selectedStatement = (Statement) la.getResource();
				log.info("showDetail(locationAssignmentId = #0) = #1", locationAssignmentId, selectedStatement);
			}
		}
	}
	
	public Statement getSelectedStatement() {
		return selectedStatement;
	}

	public void setSelectedStatement(Statement selectedStatement) {
		this.selectedStatement = selectedStatement;
	}
	
}
