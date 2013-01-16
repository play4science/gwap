/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.mit;

import gwap.model.GameRound;
import gwap.model.Person;
import gwap.model.action.Familiarity;
import gwap.model.resource.Statement;

import java.util.Date;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

/**
 * @author kneissl
 */
@Name("mitFamiliarity")
@Scope(ScopeType.PAGE)
public class FamiliarityBean {
	
	@Logger     private Log log;
	@In         private EntityManager entityManager;
	@In         private Statement statement;
	@In         private Person person;
	@In(required=false)@Out     private GameRound gameRound;
	
	private Familiarity familiarity;

	public void unfamiliar() {
		if (familiarity != null || gameRound == null || gameRound.getEndDate() != null)
			return;
		log.info("#0 rated as familiar=#1 by #2", statement, false, person);
		familiarity = new Familiarity();
		familiarity.setCreated(new Date());
		familiarity.setPerson(person);
		familiarity.setGameRound(gameRound);
		familiarity.setFamiliar(false);
		familiarity.setResource(statement);
		entityManager.persist(familiarity);
		gameRound.getActions().add(familiarity);
	}
	
}
