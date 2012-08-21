/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.model;

import gwap.tools.TagSemantics;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * Attention: when adding attributes, modify the named query 'combination.randomCombinedTagsByResourceAndLanguage'
 * 
 * @author Florian Störkle
 */
@Entity
@Name("combinedTag")
@Scope(ScopeType.EVENT)
public class CombinedTag implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id @GeneratedValue
	private Long id;
	
	private String value;

	@ManyToOne
	private Tag firstTag;
	@ManyToOne
	private Tag secondTag;
	
	public CombinedTag() {
		
	}
	
	public CombinedTag(final Tag first, final Tag second) {
		this.firstTag = first;
		this.secondTag = second;
		
		buildValue();
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getValue() {
		buildValue();
		
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Tag getFirstTag() {
		return firstTag;
	}
	
	public void setFirstTag(Tag firstTag) {
		this.firstTag = firstTag;
	}
	
	public Tag getSecondTag() {
		return secondTag;
	}
	
	public void setSecondTag(Tag secondTag) {
		this.secondTag = secondTag;
	}
	
	private void buildValue() {
		value = (firstTag == null ? "" : firstTag.getName()) + " + " + (secondTag == null ? "" : secondTag.getName());
	}

	/**
	 * Returns true if and only if either both objects o1 and o2 are null, or
	 * both objects o1 and o2 are not null.
	 * 
	 * @param o1	the first Object to test
	 * @param o2	the second Object to test
	 * @return		true if both objects are null, or both are not null,
	 * 				false otherwise
	 */
	private boolean bothNullOrNotNull(Object o1, Object o2) {
		return (o1 == null && o2 == null) || (o1 != null && o2 != null); 
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj == null || !(obj instanceof CombinedTag)) {
			return false;
		}

		if (obj == this) {
			return true;
		}
		
		final CombinedTag other = (CombinedTag)obj;  
		
		if (!bothNullOrNotNull(other.firstTag, firstTag) || !bothNullOrNotNull(other.secondTag, secondTag)) {
			return false;
		}
		
		if ((other.firstTag != null && !TagSemantics.equals(other.firstTag.getName(), firstTag.getName()))
		 || (other.secondTag != null && !TagSemantics.equals(other.secondTag.getName(), secondTag.getName()))) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public String toString() {
		buildValue();
		
		return "(" + value + ")@id=" + id;
	}
}
