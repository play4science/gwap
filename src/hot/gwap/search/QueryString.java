/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.search;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;

/**
 * @author Fabian Kneißl
 */
@Name("queryStringBean")
@Scope(ScopeType.PAGE)
public class QueryString implements Serializable {
	
	private static final long serialVersionUID = -2139435394363943555L;
	
	@In(create=true)@Out
	private String queryString;
	
	@RequestParameter("queryString") protected String queryStringParameter;

	@Factory("queryString")
	public String getQueryString() {
		if (queryString == null)
			queryString = "";
		if (queryStringParameter != null)
			queryString = queryStringParameter;
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

}
