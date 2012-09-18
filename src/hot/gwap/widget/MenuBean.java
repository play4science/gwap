/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
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
