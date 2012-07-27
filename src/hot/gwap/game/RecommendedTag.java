/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.game;

import java.io.Serializable;

import org.hibernate.validator.Length;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

@Name("recommendedTag")
@Scope(ScopeType.PAGE)
public class RecommendedTag implements Serializable {

	private static final long serialVersionUID = 1L;

	@Create	 public void init()    { log.info("Creating"); }
	@Destroy public void destroy() { log.info("Destroying"); }
	
	@Logger                  private Log log;
	@Length(min=1, max=50)   private String name;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
