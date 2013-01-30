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

package gwap.widget;

import java.io.Serializable;

import javax.faces.context.FacesContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("menuBean")
@Scope(ScopeType.STATELESS)
public class MenuBean implements Serializable {

	private static final long serialVersionUID = 1L;

	public String getCssClass(String string) {
		return FacesContext.getCurrentInstance().getViewRoot().getViewId().equals(string) ? "active" : "inactive";
	}

	public String getCssClassStartsWith(String string) {
		return FacesContext.getCurrentInstance().getViewRoot().getViewId().startsWith(string) ? "active" : "inactive";
	}
	
}
