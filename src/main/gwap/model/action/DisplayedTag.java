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
