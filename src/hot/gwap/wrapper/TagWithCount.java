/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.wrapper;

import gwap.model.Tag;

/**
 * Represents a tag together with a number count.
 * 
 * @author Fabian Kneissl
 */
public class TagWithCount {
	private Tag tag;
	private Long count;
	public TagWithCount(Tag tag, Long count) {
		this.tag = tag;
		this.count = count;
	}
	public Tag getTag() {
		return tag;
	}
	public void setTag(Tag tag) {
		this.tag = tag;
	}
	public Long getCount() {
		return count;
	}
	public void setCount(Long count) {
		this.count = count;
	}
}