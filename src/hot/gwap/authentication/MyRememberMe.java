/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.authentication;

import java.util.Random;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.security.RememberMe;

@Name("org.jboss.seam.security.rememberMe")
@Scope(ScopeType.SESSION)
@Install(precedence = Install.APPLICATION, classDependencies = "javax.faces.context.FacesContext")
@BypassInterceptors

public class MyRememberMe extends RememberMe {
	private static final long serialVersionUID = 1L;

	protected String generateTokenValue()
	   {
	      StringBuilder stringBuilder = new StringBuilder();
	      Random random = new Random();
	      stringBuilder.append(random.nextLong());
	      return stringBuilder.toString();
	   }
}