/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.game.quiz.action;

import gwap.game.quiz.UserPerceptionRating;
import gwap.model.action.PerceptionPair;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;



@Name("perceptionBean")
@Scope(ScopeType.APPLICATION)
public class PerceptionBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Create
	public void init() {
		log.info("Creating");
	}

	@Destroy
	public void destroy() {
		log.info("Destroying");
	}

	@Logger
	private Log log;
	@In
	private EntityManager entityManager;
	private UserPerceptionRating userRecommendedRating;

	public void addUserPerceptionRating(UserPerceptionRating userRecommendedRating) {
		this.userRecommendedRating = userRecommendedRating;
		createTagging();
	}
	



	/**
	 * 
	 * 
	 * @param perceptionPairName
	 * @return
	 */
	public void findOrCreatePerceptionPair() {
		// String language = localeSelector.getLanguage();

		
		PerceptionPair[] p = userRecommendedRating.getPairs();
		for (int i=0;i<5;++i){
			p[i].setPerceptionRating(userRecommendedRating.getPerceptionRating());
			entityManager.persist(p[i]);
		}
			

	}

	private void createTagging() {
			
		entityManager.persist(userRecommendedRating.getPerceptionRating());
		findOrCreatePerceptionPair();
		entityManager.flush();
		
	}




}
