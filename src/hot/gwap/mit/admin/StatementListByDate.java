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
@Name("mitAdminStatementListByDate")
public class StatementListByDate extends EntityQuery<Statement> {
	
	private static final long serialVersionUID = 1L;
	
	public StatementListByDate(){
		setEjbql("select s from Statement s where s.createDate != null order by s.createDate desc");		
	}
}
