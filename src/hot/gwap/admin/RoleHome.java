package gwap.admin;

import gwap.model.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("adminRoleHome")
public class RoleHome extends EntityHome<Role> {

	public void setRoleId(Long id) {
		setId(id);
	}

	public Long getRoleId() {
		return (Long) getId();
	}

	@Override
	protected Role createInstance() {
		Role role = new Role();
		return role;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
	}

	public boolean isWired() {
		return true;
	}

	public Role getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
