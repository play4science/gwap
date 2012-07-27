package gwap.admin;

import gwap.model.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("adminRoleList")
public class RoleList extends EntityQuery<Role> {

	private static final String EJBQL = "select role from Role role";

	private static final String[] RESTRICTIONS = {"lower(role.role) like lower(concat(#{adminRoleList.role.role},'%'))",};

	private Role role = new Role();

	public RoleList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Role getRole() {
		return role;
	}
}
