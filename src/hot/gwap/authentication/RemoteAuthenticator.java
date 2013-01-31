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

package gwap.authentication;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;

/**
 * Connects to an external webpage in order to check whether a username
 * and password combination exists on the remote side.
 * 
 * @author Fabian Kneissl
 */
@AutoCreate
@Name("remoteAuthenticator")
public class RemoteAuthenticator {
	
	@Logger
	Log log;
	
	public boolean backstageUserExists(String username, String password) {
		try {
			username = URLEncoder.encode(username, "UTF-8");
			password = URLEncoder.encode(password, "UTF-8");
			String url = "http://backstage.pms.ifi.lmu.de:8080/user/probe?username=" + username + "&password="+password;
			return urlIsWithoutError(url);
		} catch (Exception e) {
			log.error("Error during URL-encoding username / password", e);
			return false;
		}
	}
	
	public boolean urlIsWithoutError(String url) {
		try {
			URL realURL = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) realURL.openConnection();
			return connection.getResponseCode() == HttpURLConnection.HTTP_OK;
		} catch (Exception e) {
			log.error("Error during querying external site", e);
			return false;
		}
	}

}
