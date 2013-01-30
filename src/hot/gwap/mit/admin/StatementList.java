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
