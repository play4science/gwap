/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.elearn;

import gwap.model.GameRound;
import gwap.wrapper.BackstageAnswer;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * @author Fabian Kneißl
 */
@Name("elearnResults")
@AutoCreate
@Scope(ScopeType.APPLICATION)
public class Results implements Serializable {

	@In  protected EntityManager entityManager;
	
	public List<BackstageAnswer> getTopCorrectAnswers(GameRound gameRound) {
		Query q = entityManager.createNamedQuery("tagging.topCorrectAnswersGeneral");
		q.setParameter("resourceId", gameRound.getResources().get(0).getId());
		q.setMaxResults(5);
		List<BackstageAnswer> list = q.getResultList();
		return list;
	}
	
	public List<BackstageAnswer> getTopWrongAnswers(GameRound gameRound) {
		Query q = entityManager.createNamedQuery("tagging.topWrongAnswersGeneral");
		q.setParameter("resourceId", gameRound.getResources().get(0).getId());
		q.setMaxResults(5);
		List<BackstageAnswer> list = q.getResultList();
		return list;
	}
}
