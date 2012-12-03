/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
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
