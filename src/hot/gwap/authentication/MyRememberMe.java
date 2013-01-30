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
