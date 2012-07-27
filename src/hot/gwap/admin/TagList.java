package gwap.admin;

import gwap.model.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("adminTagList")
public class TagList extends EntityQuery<Tag> {

	private static final String EJBQL = "select tag from Tag tag";

	private static final String[] RESTRICTIONS = {
			"lower(tag.language) like lower(concat(#{adminTagList.tag.language},'%'))",
			"lower(tag.name) like lower(concat(#{adminTagList.tag.name},'%'))",};

	private Tag tag = new Tag();

	public TagList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Tag getTag() {
		return tag;
	}
}
