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

package gwap.test;

import static org.testng.Assert.assertEquals;
import gwap.mit.PokerScoring;
import gwap.model.resource.Location;

import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

/**
 * @author reichstaller
 */
public class PokerScoringTest extends SeamTest{
	
	
	@Test
	public void testScoringFuzzyMatching() throws Exception {
		
		new ComponentTest() {
			@Override
			protected void testComponents() throws Exception {
				EntityManager em = (EntityManager) Component.getInstance("entityManager");
				PokerScoring ps = (PokerScoring) Component.getInstance("mitPokerScoring");
				
				Location l1 = em.find(Location.class, 8230L);
				Location l2 = em.find(Location.class, 8234L);
				Location l3 = em.find(Location.class, 8300L);
				Location l4 = em.find(Location.class, 8291L);
				
				Location l5 = em.find(Location.class, 2L);
				Location l6 = em.find(Location.class, 8296L);
				
				List<Location> locationHierachyTreeL1 = ps.getLocationHierachyTree(l1);
				List<Location> locationHierachyTreeL2 = ps.getLocationHierachyTree(l2);
				List<Location> locationHierachyTreeL3 = ps.getLocationHierachyTree(l3);
				List<Location> locationHierachyTreeL5 = ps.getLocationHierachyTree(l5);
				List<Location> locationHierachyTreeL4 = ps.getLocationHierachyTree(l4);

				//Test auf Gleichheit
				assertEquals(ps.getFuzzyMatchingValue(l1, l1), 1.0d, 0.0001d);
				//Test auf Nachbarn
				assertEquals(ps.getFuzzyMatchingValue(l2, l1), 0.8d, 0.0001d);
				assertEquals(ps.getFuzzyMatchingValue(l3, l4), 0.8d, 0.0001d);
				//Test auf tiefere Ebene
				assertEquals(ps.getFuzzyMatchingValue(l3, l1), 0.5d, 0.0001d);
				//Test auf hoehere Ebene
				assertEquals(ps.getFuzzyMatchingValue(l1, l3), 0.8d, 0.0001d);
				assertEquals(ps.getFuzzyMatchingValue(l5, l6), 0.8d, 0.0001d);
				//Test auf benachbarte tiefere Ebene
				assertEquals(ps.getFuzzyMatchingValue(l4, l1), 0.3d, 0.0001d);
				//Test auf benachbarte hoehere Ebene
				assertEquals(ps.getFuzzyMatchingValue(l1, l4), 0.6d, 0.0001d);
				
			}
		}.run();
	}
	
	

}
