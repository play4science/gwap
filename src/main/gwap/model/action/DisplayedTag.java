/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.model.action;

import gwap.model.GameRound;
import gwap.model.Tag;
import gwap.model.resource.Resource;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * A DisplayedTag represents a tag displayed to a player during a Combino game
 * session that was not selected for a combination.
 * 
 * @author Florian Störkle
 */
@Entity
@Name("displayedTag")
@Scope(ScopeType.EVENT)
public class DisplayedTag extends Action implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ManyToOne
	private Tag tag;
	
	@ManyToOne
	private Resource resource;
	
	public DisplayedTag() {}
	
	public DisplayedTag(final Tag tag, final Resource resource, final GameRound gameRound) {
		this.tag = tag;
		this.resource = resource;
		setGameRound(gameRound);
	}

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}
}
