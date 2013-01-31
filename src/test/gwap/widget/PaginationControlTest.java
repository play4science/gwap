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

package gwap.widget;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import gwap.widget.PaginationControl;

import org.testng.annotations.Test;

/**
 * @author Fabian Kneißl
 */
public class PaginationControlTest {

	@Test
	public void none() {
		PaginationControl pc = new PaginationControl();
		pc.setResultsPerPage(10);
		pc.setNumResults(9);
		assertEquals(pc.getDisplayPagination(), false);
		assertEquals(pc.getNumPages().intValue(), 1);
		assertEquals(pc.getPageNumber().intValue(), 1);
		assertNull(pc.getDisplayedPageNumbers());
	}
	@Test
	public void small() {
		PaginationControl pc = new PaginationControl();
		pc.setResultsPerPage(10);
		pc.setNumResults(24);
		assertEquals(pc.getDisplayPagination(), true);
		assertEquals(pc.getNumPages().intValue(), 3);
		assertEquals(pc.getPageNumber().intValue(), 1);
		assertEquals(pc.getDisplayedPageNumbers().size(), 3);
		assertEquals(pc.getDisplayedPageNumbers().get(0).intValue(), 1);
		assertEquals(pc.getDisplayedPageNumbers().get(1).intValue(), 2);
		assertEquals(pc.getDisplayedPageNumbers().get(2).intValue(), 3);
	}

	@Test
	public void large() {
		PaginationControl pc = new PaginationControl();
		pc.setResultsPerPage(10);
		pc.setNumResults(124);
		assertEquals(pc.getDisplayPagination(), true);
		assertEquals(pc.getNumPages().intValue(), 13);
		assertEquals(pc.getPageNumber().intValue(), 1);
		assertEquals(pc.getDisplayedPageNumbers().size(), 10);
		assertEquals(pc.getDisplayedPageNumbers().get(0).intValue(), 1);
		
		pc.setPageNumber(5);
		assertEquals(pc.getDisplayedPageNumbers().size(), 10);
		assertEquals(pc.getDisplayedPageNumbers().get(0).intValue(), 1);
		
		pc.setPageNumber(8);
		assertEquals(pc.getDisplayedPageNumbers().size(), 10);
		assertEquals(pc.getDisplayedPageNumbers().get(0).intValue(), 3);
		assertEquals(pc.getDisplayedPageNumbers().get(9).intValue(), 12);
		
		pc.setPageNumber(13);
		assertEquals(pc.getDisplayedPageNumbers().size(), 10);
		assertEquals(pc.getDisplayedPageNumbers().get(0).intValue(), 4);
		assertEquals(pc.getDisplayedPageNumbers().get(9).intValue(), 13);
	}
}
