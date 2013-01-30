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

package gwap.tools;

import gwap.model.resource.Location;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * @author Fabian Kneissl
 */
@Name("locationHelper")
@Scope(ScopeType.STATELESS)
public class LocationHelper implements Serializable {

	private static final long serialVersionUID = -5521387265129679630L;

	@In
	private EntityManager entityManager;
	
	public String getCascadedLocationName(Location location) {
		@SuppressWarnings("unchecked")
		List<Location> parents = entityManager.createNamedQuery("locationHierarchy.parentLocationsBySublocationId")
			.setParameter("sublocationId", location.getId())
			.getResultList();
		Collections.sort(parents, new Comparator<Location>() {
			@Override
			public int compare(Location l1, Location l2) {
				return l1.getType().getLevel() - l2.getType().getLevel();
			}
		});
		
		StringBuilder output = new StringBuilder();
		for (Location l : parents) {
			output.append(l.getName() + ">");
		}
		output.append(location.getName());
		return output.toString();
	}
}
