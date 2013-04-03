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

package gwap.rest;

import java.io.Serializable;

import org.jboss.seam.annotations.Logger;
import org.jboss.seam.log.Log;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author Fabian Kneißl
 */
public abstract class RestService implements Serializable {

	@Logger
	protected Log log;
	
	protected JSONParser jsonParser = new JSONParser();

	protected JSONObject parse(String string) {
		try {
			return (JSONObject) jsonParser.parse(string);
		} catch (ParseException e) {
			log.warn("Could not parse JSON payload", e);
			throw new RuntimeException(e.getMessage());
		}
	}
}
