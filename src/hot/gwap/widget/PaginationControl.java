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
