/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
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
