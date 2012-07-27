/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.widget;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * @author Fabian Kneißl
 */
@Name("paginationControl")
@Scope(ScopeType.CONVERSATION)
public class PaginationControl implements Serializable {
	
	private Integer pageNumber = 1;
	private Integer numPages = 1;
	private Integer resultsPerPage = 10;
	
	public Integer getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}
	public Integer getNumPages() {
		return numPages;
	}
	public void setNumPages(Integer numPages) {
		this.numPages = numPages;
	}
	public Integer getResultsPerPage() {
		return resultsPerPage;
	}
	public void setResultsPerPage(Integer resultsPerPage) {
		this.resultsPerPage = resultsPerPage;
	}
	public boolean getDisplayPagination() {
		return numPages > 1;
	}
	public void setNumResults(long numResults) {
		numPages = (int)numResults / resultsPerPage;
		if (numResults % resultsPerPage > 0)
			numPages++;
	}
	public Integer getFirstResult() {
		return (pageNumber-1) * resultsPerPage;
	}
	public boolean getPreviousExists() {
		return numPages > 1 && pageNumber > 1;
	}
	public boolean getNextExists() {
		return pageNumber < numPages;
	}
	/** Only display max 10 pages, determine which pages to show
	 */
	public List<Integer> getDisplayedPageNumbers() {
		int numDisplayedPages = 10;
		if (numPages > 1) {
			ArrayList<Integer> list = new ArrayList<Integer>();
			int start = 1;
			if (pageNumber > numDisplayedPages/2 && numPages > numDisplayedPages)
				start = Math.min(pageNumber - numDisplayedPages/2, numPages - numDisplayedPages + 1);
			int end = Math.min(start + numDisplayedPages - 1, numPages);
			for (int i = start; i <= end; i++)
				list.add(i);
			return list;
		} else
			return null;
	}
	
}
