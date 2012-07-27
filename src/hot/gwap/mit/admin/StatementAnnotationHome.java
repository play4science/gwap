/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.mit.admin;

import gwap.model.Person;
import gwap.model.action.StatementAnnotation;
import gwap.model.resource.Statement;

import java.util.Date;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.framework.EntityHome;

/**
 * @author Fabian Kneißl
 */
@Name("mitAdminStatementAnnotationHome")
public class StatementAnnotationHome extends EntityHome<StatementAnnotation> {
	
	@RequestParameter	Long statementId;
	@RequestParameter	Long statementAnnotationId;
	
	@In					Person person;
	
	@Override
	public Object getId() {
		if (statementAnnotationId == null)
			return super.getId();
		else
			return statementAnnotationId;
	}

	@Override @Begin(join=true)
	public void create() {
		super.create();
		if (statementAnnotationId == null && statementId != null) {
			setInstance(new StatementAnnotation());
			getInstance().setStatement(getEntityManager().find(Statement.class, statementId));
		}
	}
	
	@Override
	public String persist() {
		getInstance().setPerson(person);
		getInstance().setCreated(new Date());
		getInstance().getStatement().getStatementAnnotations().add(getInstance());
		return super.persist();
	}
}