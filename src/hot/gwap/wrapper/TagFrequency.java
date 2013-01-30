/*
 * This file is part of gwap
 *
 * Copyright (C) 2013
 * Project play4science
 * Lehr- und Forschungseinheit für Programmier- und Modellierungssprachen
 * Ludwig-Maximilians-Universität München
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
