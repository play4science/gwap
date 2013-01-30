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
