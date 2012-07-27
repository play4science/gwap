/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
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
