/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
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
 * @author kneissl
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
