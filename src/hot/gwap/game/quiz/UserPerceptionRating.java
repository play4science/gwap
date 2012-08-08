/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.game.quiz;

import gwap.model.PerceptionPair;
import gwap.model.action.PerceptionRating;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

@Name("recommendedPerceptionPair")
@Scope(ScopeType.PAGE)
public class UserPerceptionRating implements Serializable {

	private static final long serialVersionUID = 1L;
	private int resourceId;

	@Logger
	private Log log;
	private PerceptionPair[] pPairs ;
	private PerceptionRating perceptionRating;
	private int questionNumber;
	private Long fillOutTimeMs;

	public int getResourceId() {
		return resourceId;
	}

	
	public PerceptionPair[] getPairs(){
		return pPairs;
	}
	
	public void setPairs(PerceptionPair[] pPairs){
		this.pPairs = pPairs;
	}

	public void setPerceptionRating(PerceptionRating perceptionRating) {
		this.perceptionRating = perceptionRating;
		
	}

	public PerceptionRating getPerceptionRating() {
		return perceptionRating;
	}

	public void setQuestionNumber(int questionNumber) {
		this.questionNumber = questionNumber;
		
	}
	
	public void setQuestionNumber(Object questionNumber) {
		this.questionNumber = ((Long)questionNumber).intValue();
		
	}

	public int getQuestionNumber() {
		return this.questionNumber;
	}


	public void setFillOutTimeMs(Long fillOutTimeMs) {
		this.fillOutTimeMs = fillOutTimeMs;
		
	}


	public Long getFillOutTimeMs() {
		return fillOutTimeMs;
	}
}
