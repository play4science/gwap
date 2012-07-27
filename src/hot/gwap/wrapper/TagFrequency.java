/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.wrapper;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

public class TagFrequency implements Serializable, Comparable<TagFrequency> {

	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue
	private Long id;
	
	private String name;
	private Long size;
	
	private Long tagId;

	public TagFrequency() {
	}
	
	public TagFrequency(String name, Long size) {
		this.name = name.toUpperCase();
		this.size = size;
	}

	public TagFrequency(String name, Double size) {
		this(name, size.longValue());
	}
	
	public TagFrequency(String name, Long size, Long tagId) {
		this(name, size);
		
		this.tagId = tagId;
	}
	
	public TagFrequency(String name, Double size, Long tagId) {
		this(name, size);
		
		this.tagId = tagId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}
	
	public Long getTagId() {
		return tagId;
	}

	public void setTagId(Long tagId) {
		this.tagId = tagId;
	}

	public String toString() {
		return "("+name+","+size+")";
	}
	
	public int compareTo(TagFrequency o2)
	{
		if (o2!=null)
		{
			long a = size;
            long b = o2.getSize();
            return a < b ? 1 : a == b ? 0 : -1;		
		}
		else
			return 1;
	}

}