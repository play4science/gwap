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

package gwap.tools;

import gwap.widget.PaginationControl;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Out;

/**
 * @author Fabian Kneißl
 */
public abstract class AbstractPaginatedList {
	
	@In(create=true) @Out    
	protected PaginationControl paginationControl;

	protected Integer resultNumber = 0;

	public abstract void updateList();
	
	public Integer getPageNumber() {
		return paginationControl.getPageNumber();
	}
	
	public void setPageNumber(Integer pageNumber) {
		if (pageNumber != paginationControl.getPageNumber()) {
			paginationControl.setPageNumber(pageNumber);
			updateList();
		}
	}
	
	public Integer getResultNumber() {
		return resultNumber;
	}
	
	public void setResultNumber(Integer resultNumber) {
		this.resultNumber = resultNumber;
	}
}
