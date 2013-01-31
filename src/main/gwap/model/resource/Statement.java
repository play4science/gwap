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

package gwap.model.resource;

import gwap.model.Person;
import gwap.model.action.StatementAnnotation;
import gwap.tools.StatementHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@NamedQueries({
	@NamedQuery(name="statement.all", query="select s from Statement s"),
	@NamedQuery(name="statement.allEnabledSorted", query="select s from Statement s where enabled=true order by s.text"),
	@NamedQuery(name="statement.randomEnabled", query="select s from Statement s where s.enabled = true order by random()"),
	@NamedQuery(name="statement.nextSensibleForLocationAssignment", 
			query="select s.id from Bet b join b.resource s where " +
					"s.class = Statement and s.enabled = true " +
					"group by s.id " +
					"order by random()"),
	@NamedQuery(name="statement.nextSensibleForLocationAssignmentByPerson", 
			query="select s.id from Bet b join b.resource s left outer join s.gameRounds gr where " +
					"s.class = Statement and s.enabled = true and s.creator != :person " +
					"and not exists (select b2.id from Bet b2 where b2.resource=s and b2.person = :person) " +
					"and not exists (select la2.id from LocationAssignment la2 where la2.resource=s and la2.person = :person) " +
					"and not exists (select la2.id from LocationAssignment la2 join la2.person p2 where la2.resource=s and p2.personConnected = :person) " +
					"group by s.id " +
					"order by random()"),
	@NamedQuery(name="statement.nextSensibleForPoker", 
			query="select s.id from Bet b join b.resource s where " +
					"s.class = Statement and s.enabled = true and b.points = 1 " +
					"group by s.id " +
					"order by random()"),
	@NamedQuery(name="statement.nextSensibleForPokerByPerson", 
			query="select s.id from Bet b join b.resource s left outer join s.gameRounds gr where " +
					"s.class = Statement and s.enabled = true and s.creator != :person and b.points = 1 " +
					"and not exists (select b2.id from Bet b2 where b2.resource=s and b2.person = :person) " +
					"and not exists (select la2.id from LocationAssignment la2 where la2.resource=s and la2.person = :person) " +
					"and not exists (select la2.id from LocationAssignment la2 join la2.person p2 where la2.resource=s and p2.personConnected = :person) " +
					"group by s.id " +
					"order by random()"),
	@NamedQuery(name="statement.atLeastAssigned", 
			query="select s.id from Bet b join b.resource s join s.locationAssignments la where " +
					"s.class = Statement and s.enabled = true " +
					"group by s.id " +
					"having count(la.id) > :minAssignments " +
					"order by random()"),
	@NamedQuery(name="statement.atLeastAssignedByPerson", 
			query="select s.id from Bet b join b.resource s join s.locationAssignments la left outer join s.gameRounds gr where " +
					"s.class = Statement and s.enabled = true and s.creator != :person " +
					"and not exists (select b2.id from Bet b2 where b2.resource=s and b2.person = :person) " +
					"and not exists (select la2.id from LocationAssignment la2 where la2.resource=s and la2.person = :person) " +
					"and not exists (select la2.id from LocationAssignment la2 join la2.person p2 where la2.resource=s and p2.personConnected = :person) " +
					"group by s.id " +
					"having count(la.id) > :minAssignments " +
					"order by random()"),
	@NamedQuery(name="statement.byCreator", query="select s from Statement s where s.creator=:person and s.enabled=true"),
	@NamedQuery(name="statement.countByCreator", query="select count(s.id) from Statement s where s.creator=:person and s.enabled=true"),
	@NamedQuery(name="statement.byCreateDate", query="select s from Statement s where s.createDate != null order by createDate"),
	@NamedQuery(name="statement.byText", query="from Statement where text = :text")
})

/**
 * Represents a textual resource which consists of several tokens
 * 
 * @author Fabian Kneißl
 */
@Entity
@Name("statement")
@Scope(ScopeType.CONVERSATION)
public class Statement extends Resource {
	
	private static final long serialVersionUID = 1L;
	
	@OneToMany(mappedBy="statement", cascade=CascadeType.REMOVE)
	private List<StatementToken> statementTokens = new ArrayList<StatementToken>();
	
	@OneToMany(mappedBy="statement", cascade=CascadeType.REMOVE)
	private List<StatementAnnotation> statementAnnotations = new ArrayList<StatementAnnotation>();
	
	// The golden "standard" language
	@OneToMany(mappedBy="statement", cascade=CascadeType.REMOVE)
	private List<StatementStandardToken> statementStandardTokens = new ArrayList<StatementStandardToken>();
	
	@ManyToOne
	private Person creator;
	
	@Lob
	private String text;
	
	private Date createDate; // dateCreated does not work because of clash with dateCreated in ArtResource
	
	public List<StatementToken> getStatementTokens() {
		return statementTokens;
	}

	public void setStatementTokens(List<StatementToken> statementTokens) {
		this.statementTokens = statementTokens;
	}
	
	public List<StatementStandardToken> getStatementStandardTokens() {
		return statementStandardTokens;
	}

	public void setStatementStandardTokens(List<StatementStandardToken> statementStandardTokens) {
		this.statementStandardTokens = statementStandardTokens;
	}

	public List<StatementAnnotation> getStatementAnnotations() {
		return statementAnnotations;
	}

	public void setStatementAnnotations(
			List<StatementAnnotation> statementAnnotations) {
		this.statementAnnotations = statementAnnotations;
	}

	public Person getCreator() {
		return creator;
	}

	public void setCreator(Person creator) {
		this.creator = creator;
	}

	public Date getCreateDate() {
		return createDate;
	}
	
	public String getCreateDateInGermanFormat(){
		SimpleDateFormat df = new SimpleDateFormat( "dd.MM.yyyy");
		return df.format(createDate);
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String asText() {
//		return StatementHelper.joinTokens(statementTokens);
		return text;
	}
	
	public String standardAsText() {
		return StatementHelper.joinTokens(statementStandardTokens);
	}
	
	public String toString() {
		return "Statement#"+getId();
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
}
