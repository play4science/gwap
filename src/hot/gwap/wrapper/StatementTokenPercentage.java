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

package gwap.wrapper;


import gwap.model.resource.StatementToken;

/**
 * @author reichstaller
 */
public class StatementTokenPercentage extends Percentage {

	private StatementToken token;
	
	public StatementTokenPercentage() { }
	
	public StatementTokenPercentage(StatementToken token, Number sum, Number total) {
		super(sum, total);
		this.token = token;
	}

	public StatementToken getStatementToken() {
		return token;
	}
	
	public String getStatementTokenString() {
		return token.toString();
	}

	public void setStatementToken(StatementToken token) {
		this.token = token;
	}
	
	public String getMarkerColor() {
		if (getPercentage() != null) {
			if (getPercentage() >= 80)
				return "#3f44e9";
			else if (getPercentage() >= 60)
				return "#444ad1";
			else if (getPercentage() >= 40)
				return "#6871d8";
			else if (getPercentage() >= 20)
				return "#6067b3";
		}
		return "white";
	}
	
	
}
