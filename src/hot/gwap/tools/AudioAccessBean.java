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

import gwap.model.resource.AudioResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.servlet.ContextualHttpServletRequest;

/**
 * Responsible for storing and retrieving hashes for images.
 * 
 * @author Fabian Kneißl
 */
@Name("audioAccessBean")
@Scope(ScopeType.APPLICATION)
@BypassInterceptors
@Install
public class AudioAccessBean extends AbstractAccessBean {
	
	private static AudioAccessBean instance;

	@Override
	protected String hashToUrl(String hash) {
		String basePath = (String) Component.getInstance("internalBasePath");
		return basePath+"seam/resource/audio/"+hash+".mp3";
	}

	public static AudioAccessBean getInstance() {
		if (instance == null)
			instance = (AudioAccessBean) Component.getInstance("audioAccessBean");
		return instance;
	}
	
	@Override
	public void getResource(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException, IOException {
		new ContextualHttpServletRequest(request) {
			@Override
			public void process() throws ServletException, IOException {
				String hash = request.getRequestURI();
				try {
					hash = hash.substring(hash.lastIndexOf("/")+1, hash.lastIndexOf(".mp3"));
					AudioAccessBean accessBean = AudioAccessBean.getInstance();
					
					String path = accessBean.getPath(hash);
					response.setContentType("audio/mpeg");
					response.setHeader("Content-Disposition", "inline; filename=\"audio.mp3\"");
//					response.setHeader("Cache-Control", "no-store"); // browser should use no cache
//					response.setHeader("Pragma", "no-cache");  // do not save on proxy server
					response.setDateHeader("Expires", URL_LIFETIME_SECS);

					log.info("Get resource: #0", path);
					InputStream in = new FileInputStream(path); 	
			    	OutputStream out = response.getOutputStream();
			    	int nrBytes = writeTo(in, out);
			    	out.flush();
			    	response.setHeader("Content-Length", ""+nrBytes); 
			    	out.close();
				} catch (Exception e) {
					log.info("getResource(#0): Could not load audio: #1", hash, e.getMessage());
					response.sendError(404);
				}
			}
		}.run();
	}
	
	@Override
	public String getResourcePath() {
		return "/audio";
	}
	
	public static void setResourceUrl(AudioResource audioResource) {
		if (audioResource != null)
			audioResource.setUrl(AudioAccessBean.getInstance().createUrl(audioResource.getSource().getUrl() + audioResource.getPath()));
	}
}
