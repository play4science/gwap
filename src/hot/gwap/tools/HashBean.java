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

package gwap.tools;

import java.util.Date;

/**
 * @author Fabian Kneißl
 */
public class HashBean {
	private final String path;
	private final String hash;
	private final Date date;
	
	public HashBean(final String path, final String hash) {
		this.path = path;
		this.hash = hash;
		this.date = new Date();
	}
	public String getPath() {
		return path;
	}
	public String getHash() {
		return hash;
	}
	public Date getDate() {
		return date;
	}
	public String toString() {
		return path + " (" + date + ")";
	}
}
