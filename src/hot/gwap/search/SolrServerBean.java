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

package gwap.search;

import java.io.IOException;
import java.io.Serializable;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

/**
 * @author Fabian Kneißl
 */
@Name("solrServerBean")
@Scope(ScopeType.STATELESS)
public class SolrServerBean implements Serializable {

	public class PreEmptiveBasicAuthenticator implements HttpRequestInterceptor {

		private final UsernamePasswordCredentials credentials;

		public PreEmptiveBasicAuthenticator(String user, String pass) {
		    credentials = new UsernamePasswordCredentials(user, pass);
		}
		
		@Override
		public void process(HttpRequest request, HttpContext context)
				throws HttpException, IOException {
			request.addHeader(BasicScheme.authenticate(credentials,"US-ASCII",false));
		}

	}

	private static final long serialVersionUID = 1L;

	@In(create=true)
	private String platform;
	
	@Logger
	private Log log;
	
	@Out(scope=ScopeType.APPLICATION)
	private SolrServer solrServer;
	
	private String solrPassword;
	private String solrUsername;

	@SuppressWarnings("deprecation")
	@Factory("solrServer")
	public void connectToSolrServer() {
		log.info("Connecting to solr server");
		String url = "http://localhost:8080/solr/" + platform;
		solrServer = null;
		solrServer = new HttpSolrServer(url);
		HttpSolrServer server = (HttpSolrServer) solrServer;
		if (solrUsername != null && solrPassword != null)
			((AbstractHttpClient)server.getHttpClient()).addRequestInterceptor(new PreEmptiveBasicAuthenticator(solrUsername, solrPassword));
		server.setSoTimeout(1000); // socket read timeout
		server.setConnectionTimeout(100);
		server.setDefaultMaxConnectionsPerHost(100);
		server.setMaxTotalConnections(100);
//			server.setFollowRedirects(false); // defaults to false
		// allowCompression defaults to false.
		// Server side must support gzip or deflate for this to have any effect.
		server.setAllowCompression(true);
		server.setMaxRetries(1); // defaults to 0. > 1 not recommended.
//			server.setParser(new XMLResponseParser()); // binary parser is used by default
	}

	public String getSolrPassword() {
		return solrPassword;
	}

	public void setSolrPassword(String solrPassword) {
		this.solrPassword = solrPassword;
	}

	public String getSolrUsername() {
		return solrUsername;
	}

	public void setSolrUsername(String solrUsername) {
		this.solrUsername = solrUsername;
	}

}
