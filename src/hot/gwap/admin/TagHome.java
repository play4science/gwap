package gwap.admin;

import gwap.model.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("adminTagHome")
public class TagHome extends EntityHome<Tag> {

	public void setTagId(Long id) {
		setId(id);
	}

	public Long getTagId() {
		return (Long) getId();
	}

	@Override
	protected Tag createInstance() {
		Tag tag = new Tag();
		return tag;
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

	public Tag getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
