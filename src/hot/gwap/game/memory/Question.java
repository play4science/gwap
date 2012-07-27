/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.game.memory;

import gwap.model.Tag;

/**
 * @author steinmayr
 */
public class Question {
	private Tag question;
	private int answer=0;	
	
	public Question(Tag question)
	{
		this.question=question;	
	}

	public int getAnswer() {
		return answer;
	}

	public void setAnswer(int answer) {
		this.answer = answer;
	}

	public Tag getQuestion() {
		return question;
	}
	
	

}
