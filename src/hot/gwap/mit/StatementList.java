/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.mit;

import gwap.model.Person;
import gwap.model.resource.Statement;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.log.Log;

/**
 * @author Fabian Kneißl
 */
@Name("mitStatementList")
@Scope(ScopeType.PAGE)
public class StatementList implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Logger			Log log;
	@In				EntityManager entityManager;
	@In				Person person;
	
	private List<Statement> statementList;
	
	@Unwrap
	public List<Statement> getStatementList() {
		if (statementList == null) {
			long start = System.currentTimeMillis();
			Query q = entityManager.createNamedQuery("statement.byCreator").setParameter("person", person);
			statementList = q.getResultList();
			for (Statement statement : statementList) {
				statement.getStatementTokens().size();
			}
			log.info("Created statementList with #0 statements in #1ms", statementList.size(), System.currentTimeMillis()-start);
		}
		return statementList;
	}
}
