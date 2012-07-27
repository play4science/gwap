/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.tools;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

@Name("layoutBean")
@Scope(ScopeType.SESSION)
public class LayoutBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Create	 public void init()    { log.info("Creating"); }
	@Destroy public void destroy() { log.info("Destroying"); }
	
	@Logger                  private Log log;
	
	private String windowHeight;
	private String windowWidth;
	
	public String getWindowHeight() {
		return windowHeight;
	}

	public void setWindowHeight(String windowHeight) {
		this.windowHeight = windowHeight;
	}

	public String getWindowWidth() {
		return windowWidth;
	}

	//	@RaiseEvent("windowSize.widthUpdate")
	public void setWindowWidth(String windowWidth) {
		//log.info("Updating Window Size");
		this.windowWidth = windowWidth;
	}

	public String toString() {
		return "(" + windowWidth + ", " + windowHeight + ")";
	}
}
