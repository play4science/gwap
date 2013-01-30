/*
 * This file is part of gwap
 *
 * Copyright (C) 2013
 * Project play4science
 * Lehr- und Forschungseinheit für Programmier- und Modellierungssprachen
 * Ludwig-Maximilians-Universität München
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
