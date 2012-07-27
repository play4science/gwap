/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.mit.admin;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

/**
 * @author Fabian Kneißl
 */
@Name("mitAdminStatementsTeaserList")
public class StatementsTeaserList extends EntityQuery<StatementsTeaserList> {

	public StatementsTeaserList() {
		setEjbql("select s from StatementsTeaser s");
	}
	
}
