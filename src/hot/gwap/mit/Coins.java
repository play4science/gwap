/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.mit;

import gwap.model.Person;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

/**
 * @author Fabian Kneißl
 */
@Name("mitCoins")
@Scope(ScopeType.SESSION)
public class Coins implements Serializable {

	private static final long serialVersionUID = 1L;

	@Logger				private Log log;
	@In					private EntityManager entityManager;
	@In(required=false) private Person person;
	@In					private PokerScoring mitPokerScoring;
	
	public Integer getCurrentFunds() {
		if (person == null)
			return 0;
		else {
			return mitPokerScoring.getPersonScoreSum(person);
		}
	}
	
	public boolean canEnterStatement() {
		//return getCurrentFunds() >= 20;
		return true;
	}
}
