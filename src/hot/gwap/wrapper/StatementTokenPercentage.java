/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-12, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
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
