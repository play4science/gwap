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

package gwap.model.resource;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.hibernate.validator.NotNull;

/**
 * An ordered many-to-many relationship between Statements and Tokens. The
 * order is specified by the <code>sequenceNumber</code> attribute.
 * 
 * @author Fabian Kneißl
 */
@MappedSuperclass
public class AbstractStatementToken implements Serializable, Comparable<AbstractStatementToken> {
	
	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue
	protected Long id;
	
	@ManyToOne  protected Statement statement;
	@ManyToOne  protected Token token;
	
	@NotNull
	protected Integer sequenceNumber;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Statement getStatement() {
		return statement;
	}

	public void setStatement(Statement statement) {
		this.statement = statement;
	}

	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}
	
	public Integer getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	@Override
	public int compareTo(AbstractStatementToken o) {
		return sequenceNumber.compareTo(o.sequenceNumber);
	}
	
}
