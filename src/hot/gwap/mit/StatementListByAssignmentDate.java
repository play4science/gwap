/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
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
