/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.model;

/**
 * @author Fabian Kneißl
 */
public enum Education {
	NONE ("person.education.none"),
	APPRENTICESHIP ("person.education.apprenticeship"),
	ELEMENTARY_SCHOOL ("person.education.elementary_school"),
	HIGHSCHOOL ("person.education.highschool"), 
	COLLEGE ("person.education.college"), 
	UNIVERSITY ("person.education.university"), 
	DOCTOR ("person.education.doctor");
	
	private String key;
	
	Education(String key) {
		this.key = key;
	}
	public String getKey() {
		return key;
	}
}