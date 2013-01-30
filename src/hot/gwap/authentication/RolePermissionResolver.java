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

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.Serializable;
import java.util.Set;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.permission.PermissionResolver;


/**
 * This PermissionResolver gives everyone with role 'admin' all permissions.
 * 
 * @author 'Fabian Kneißl'
 */
@Name("rolePermissionResolver")
@Scope(APPLICATION)
@BypassInterceptors
@Install(precedence=BUILT_IN)
@Startup
public class RolePermissionResolver implements PermissionResolver, Serializable {

	@Override
	public boolean hasPermission(Object target, String action) {
		Identity identity = Identity.instance();
		return identity.isLoggedIn() && identity.hasRole("admin");
	}

	@Override
	public void filterSetByAction(Set<Object> targets, String action) {
		// TODO Auto-generated method stub
		
	}

}
