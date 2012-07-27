/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.tools;

import java.util.Date;

/**
 * @author Fabian Kneißl
 */
public class HashBean {
	private final String path;
	private final String hash;
	private final Date date;
	
	public HashBean(final String path, final String hash) {
		this.path = path;
		this.hash = hash;
		this.date = new Date();
	}
	public String getPath() {
		return path;
	}
	public String getHash() {
		return hash;
	}
	public Date getDate() {
		return date;
	}
	public String toString() {
		return path + " (" + date + ")";
	}
}