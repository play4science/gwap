/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
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
