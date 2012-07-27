/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.model.action;

import gwap.model.Tag;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Scope;

/**
 * When the user makes a spell correction, the old and new tag is saved here.
 * The new tag is saved in the Tagging table.
 * 
 * @author Fabian Kneißl
 */
@Entity
@Scope(ScopeType.EVENT)
public class TaggingCorrection extends Action {

	private static final long serialVersionUID = 1L;

	@ManyToOne	private Tag originalTag;
	@ManyToOne	private Tag correctedTag;
	private Boolean accepted;

	public Tag getOriginalTag() {
		return originalTag;
	}

	public void setOriginalTag(Tag originalTag) {
		this.originalTag = originalTag;
	}

	public Tag getCorrectedTag() {
		return correctedTag;
	}

	public void setCorrectedTag(Tag correctedTag) {
		this.correctedTag = correctedTag;
	}

	public Boolean getAccepted() {
		return accepted;
	}

	public void setAccepted(Boolean accepted) {
		this.accepted = accepted;
	}
	
}