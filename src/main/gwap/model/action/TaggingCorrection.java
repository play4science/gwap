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
