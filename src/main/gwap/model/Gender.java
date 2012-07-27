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
public enum Gender {
	FEMALE ("person.gender.female"),
	MALE ("person.gender.male");
	
	private String key;
	
	Gender(String key) {
		this.key = key;
	}
	public String getKey() {
		return key;
	}
}