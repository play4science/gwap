/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.widget;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import gwap.widget.PaginationControl;

import org.testng.annotations.Test;

/**
 * @author kneissl
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
