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

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.ResourceBundle;

/**
 * @author kneissl
 */
@Name("messagesHelper")
@Scope(ScopeType.STATELESS)
public class MessagesHelper implements Serializable {

	private static final long serialVersionUID = -7463282172257691646L;
	
	public String concat(String... strings) {
		StringBuilder result = new StringBuilder();
		for (String s : strings) {
			result.append(s);
		}
		return result.toString();
	}
	
	public String getConcatenated(String... strings) {
		return ResourceBundle.instance().getString(concat(strings));
	}

}
