/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.search;

import java.io.Serializable;
import java.net.MalformedURLException;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
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
		try {
			solrServer = new CommonsHttpSolrServer(url);
			CommonsHttpSolrServer server = (CommonsHttpSolrServer) solrServer;
			server.getHttpClient().getParams().setAuthenticationPreemptive(true);
			AuthScope scope = new AuthScope(AuthScope.ANY_HOST,AuthScope.ANY_PORT,null, null);
			if (solrUsername != null && solrPassword != null)
				server.getHttpClient().getState().setCredentials(scope, (Credentials) new UsernamePasswordCredentials(solrUsername, solrPassword));
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
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
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
