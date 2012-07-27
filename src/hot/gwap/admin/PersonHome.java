package gwap.admin;

import gwap.model.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("adminPersonHome")
public class PersonHome extends EntityHome<Person> {

	public void setPersonId(Long id) {
		setId(id);
	}

	public Long getPersonId() {
		return (Long) getId();
	}

	@Override
	protected Person createInstance() {
		Person person = new Person();
		return person;
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

	public Person getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
