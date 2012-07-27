/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.model.resource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

@NamedQueries({
	@NamedQuery(name="token.byValue", query="from Token where value = :value")
})

/**
 * A token is a sequence of characters which describes a word. A token
 * can have several properties specific to its type.
 * 
 * @author Fabian Kneißl
 */
@Entity
public class Token implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue
	private Long id;

	@OneToMany(mappedBy="token")
	private List<StatementToken> statementTokens = new ArrayList<StatementToken>();
	
	@Column(unique=true)
	private String value;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<StatementToken> getStatementTokens() {
		return statementTokens;
	}

	public void setStatementTokens(List<StatementToken> statementTokens) {
		this.statementTokens = statementTokens;
	}

	@Override
	public String toString() {
		return value;
	}

	public boolean isPunktuation() {
		if (value != null && value.length() == 1) {
			char c = value.charAt(0);
			String punctuationChars = ".,?!:;`´'";
			for (int i = 0; i < punctuationChars.length(); i++) {
				if (c == punctuationChars.charAt(i))
					return true;
			}
		}
		return false;
	}
}
