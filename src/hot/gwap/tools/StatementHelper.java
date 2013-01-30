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

package gwap.tools;

import gwap.mit.TextHelper;
import gwap.model.resource.AbstractStatementToken;
import gwap.model.resource.Statement;
import gwap.model.resource.StatementStandardToken;
import gwap.model.resource.StatementToken;
import gwap.model.resource.Token;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.jboss.seam.annotations.Logger;
import org.jboss.seam.log.Log;

/**
 * @author Fabian Kneißl
 */
public class StatementHelper {
	
	@Logger
	private static Log log;
	
	public static void createStatementTokens(Statement statement, String text, EntityManager entityManager) {
		createStatementTokens(statement, text, entityManager, StatementToken.class);
	}

	public static void createStatementStandardTokens(Statement statement, String text, EntityManager entityManager) {
		createStatementTokens(statement, text, entityManager, StatementStandardToken.class);
	}
	
	private static void createStatementTokens(Statement statement, String text, EntityManager entityManager, Class<? extends AbstractStatementToken> statementTokenClass) {
		try {
			statementTokenClass.newInstance(); // test that this works :)
			
			Query q = entityManager.createNamedQuery("token.byValue");
			List<String> tokens = TextHelper.splitIntoTokens(text);
			for (int i = 0; i < tokens.size(); i++) {
				String s = tokens.get(i);
				// Search for token
				q.setParameter("value", s);
				Token token;
				try {
					token = (Token) q.getSingleResult();
				} catch (NoResultException e) {
					token = new Token();
					token.setValue(s);
					entityManager.persist(token);
				}
				// Add token to Statement
				AbstractStatementToken statementToken = statementTokenClass.newInstance();
				statementToken.setStatement(statement);
				statementToken.setToken(token);
				statementToken.setSequenceNumber(i);
				entityManager.persist(statementToken);
			}
			entityManager.flush();
			entityManager.refresh(statement);
			if (statementTokenClass == StatementToken.class)
				statement.setText(text);
		} catch (InstantiationException e) {
			log.error("StatementToken could not be created", e);
		} catch (IllegalAccessException e) {
			log.error("StatementToken could not be created", e);
		}
	}

	public static <T extends AbstractStatementToken> String joinTokens(List<T> tokens) {
		Collections.sort(tokens);
		StringBuilder text = new StringBuilder();
		for (T trt : tokens) {
			String token = trt.getToken().getValue();
			// insert space if necessary
			if (!trt.getToken().isPunktuation() && text.length() > 0 && text.charAt(text.length()-1) != ' ')
				text.append(" ");
			text.append(token);				
		}
		return text.toString();
	}
}
