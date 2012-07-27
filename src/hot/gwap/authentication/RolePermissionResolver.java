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
