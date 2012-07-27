/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.mit.admin;

import gwap.model.resource.Statement;
import gwap.tools.StatementHelper;

import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

/**
 * @author Fabian Kneißl
 */
@Name("mitAdminStatementList")
public class StatementList extends EntityQuery<Statement>{
	
	public StatementList() {
		setEjbql("select s from Statement s order by s.text");
	}
	
	public void calculateTextForStatements() {
		List<Statement> statements = getEntityManager().createQuery("select s from Statement s").getResultList();
		for (Statement s : statements) {
			String text = StatementHelper.joinTokens(s.getStatementTokens());
			s.setText(text);
			getEntityManager().merge(s);
		}
		getEntityManager().flush();
	}
}
