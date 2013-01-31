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

package gwap.test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import gwap.model.resource.Location;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.mock.SeamTest;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Fabian Kneißl
 */
public class LocationTest extends SeamTest {

	@Test
	public void testLocationNeighbor() throws Exception {
		new FacesRequest() {
			@Override
			protected void invokeApplication() throws Exception {
				EntityManager em = (EntityManager) Component.getInstance("entityManager");
                
				Location l1 = new Location();
				l1.setName("Location 1");
				em.persist(l1);
				Assert.assertNotNull(l1.getId());
				
				Location l2 = new Location();
				l2.setName("Location 2");
				em.persist(l2);
				
				l1.getNeighbors().add(l2);
				
				Location l3 = (Location) em.createQuery("from Location l where l = :l1")
						.setParameter("l1", l1).getSingleResult();
				assertNotNull(l3);
				assertEquals(l3.getNeighbors().size(), 1);
				assertEquals(l3.getNeighbors().get(0).getId(), l2.getId());
			}
		}.run();
	}

}
