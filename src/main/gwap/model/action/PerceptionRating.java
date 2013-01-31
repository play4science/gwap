/*
 * This file is part of gwap, an open platform for games with a purpose
 *
 * Copyright (C) 2013
 * Project play4science
 * Lehr- und Forschungseinheit für Programmier- und Modellierungssprachen
 * Ludwig-Maximilians-Universität München
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package gwap.model.action;

import gwap.model.resource.Resource;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * A PerceptionRating saves which user added a perception pair to "rate" a resource.
 * 
 * @author Jonas Hölzler
 */

@NamedQueries( { 
@NamedQuery(
			name="PerceptionRating.byResource",
			query="select t from PerceptionRating t")
})
@Entity
public class PerceptionRating extends Action {

	private static final long serialVersionUID = 1L;

	@ManyToOne	private Resource resource;
    private Long fillOutTimeMs;

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}
	
	public Long getFillOutTimeMs() {
		return fillOutTimeMs;
	}
	
	public void setFillOutTimeMs(Long fillOutTimeMs) {
		this.fillOutTimeMs = fillOutTimeMs;
		
	}

}
