/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.wrapper;

import java.util.List;

/**
 * @author Mislav Boras
 */
public class BackstageQuestion {
	private String question;
	private List<BackstageAnswer> list;
	
	public BackstageQuestion(String question, List<BackstageAnswer> list) {
		this.question = question;
		this.list = list;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public List<BackstageAnswer> getList() {
		return list;
	}
	public void setList(List<BackstageAnswer> list) {
		this.list = list;
	}
}
