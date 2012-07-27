/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.model.resource;

import javax.persistence.Entity;

/**
 * An ordered many-to-many relationship between Statements and Tokens. The
 * order is specified by the <code>sequenceNumber</code> attribute.
 * 
 * @author Fabian Kneißl
 */
@Entity
public class StatementStandardToken extends AbstractStatementToken {
	
	private static final long serialVersionUID = 1L;
	
	
}
