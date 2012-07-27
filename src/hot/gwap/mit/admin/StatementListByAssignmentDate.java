/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.mit.admin;

import gwap.model.resource.Statement;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

/**
 * @author Fabian Kneißl
 */
@Name("mitAdminStatementListByAssignmentDate")
public class StatementListByAssignmentDate extends EntityQuery<Statement> {

	private static final long serialVersionUID = 1L;

	private int maximumResults = 10;

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
	
}
