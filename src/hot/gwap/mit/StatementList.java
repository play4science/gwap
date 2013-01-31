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
