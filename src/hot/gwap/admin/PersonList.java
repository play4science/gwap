package gwap.admin;

import gwap.model.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("adminPersonList")
public class PersonList extends EntityQuery<Person> {

	private static final String EJBQL = "select person from Person person";

	private static final String[] RESTRICTIONS = {
			"lower(person.email) like lower(concat(#{adminPersonList.person.email},'%'))",
			"lower(person.password) like lower(concat(#{adminPersonList.person.password},'%'))",
			"lower(person.username) like lower(concat(#{adminPersonList.person.username},'%'))",};

	private Person person = new Person();

	public PersonList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Person getPerson() {
		return person;
	}
}
