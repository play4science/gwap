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

package gwap.model.action;

import gwap.model.resource.Statement;
import gwap.model.resource.StatementToken;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.hibernate.validator.NotEmpty;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * Used for annotating statements with a custom text. It can be used to 
 * annotate the whole statement (then, all StatementTokens are referenced) or
 * to only some parts of it (then, these StatementTokens are referenced).
 * 
 * @author Fabian Kneißl
 */
@NamedQueries({
	@NamedQuery(name="statementAnnotation.byStatementAndPerson",
			query="select a from StatementAnnotation a where a.statement = :statement and a.person = :person order by a.created desc"),
	@NamedQuery(name="statementAnnotation.scoreSumByPerson",
			query="select sum(a.score) from StatementAnnotation a where a.person = :person"),
	@NamedQuery(name="statementAnnotation.countNotEmptyByStatement",
			query="select count(a.id) from StatementAnnotation a " +
					"where a.statement = :statement and size(a.statementTokens) > 0"),
	@NamedQuery(name="statementAnnotation.similarAnnotations",
			query="select a.id from StatementAnnotation a join a.statementTokens t, " +
					"StatementAnnotation a2 join a2.statementTokens t2 " +
					"where a.statement = :statement and a2 = :statementAnnotation and t.id=t2.id " +
					"group by a.id having count(*) = :minMatchingTokens"),
	@NamedQuery(name="statementAnnotation.byToken",
			query="select t.id, count(*) from StatementAnnotation a join a.statementTokens t where a.statement.id = :statementId group by t.id"),
	@NamedQuery(name="statementAnnotation.countByStatement",
			query="select count(a.id) from StatementAnnotation a where a.statement.id = :statementId and a.statementTokens is not empty"),
	@NamedQuery(name="statementAnnotation.byStatement",
			query="from StatementAnnotation where statement = :statement")
})
@Entity
@Name("statementAnnotation")
@Scope(ScopeType.EVENT)
public class StatementAnnotation extends Action {

	private static final long serialVersionUID = 1L;
	
	public static final String LOCATED = "located";
	public static final String PREDEFINED = "predefined";
	public static final String OTHER = "other";
	
	@NotEmpty
	@Lob			private String text;
	
	@ManyToOne		private Statement statement;
	
	@ManyToMany		private List<StatementToken> statementTokens = new ArrayList<StatementToken>();
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Statement getStatement() {
		return statement;
	}

	public void setStatement(Statement statement) {
		this.statement = statement;
	}

	public List<StatementToken> getStatementTokens() {
		return statementTokens;
	}

	public void setStatementTokens(List<StatementToken> statementTokens) {
		this.statementTokens = statementTokens;
	}
	
	
}
