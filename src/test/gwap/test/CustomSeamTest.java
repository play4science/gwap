package gwap.test;

import org.jboss.seam.mock.SeamTest;

public abstract class CustomSeamTest extends SeamTest {
	
	public abstract class MyComponentTest extends ComponentTest {

		protected Long createPerson() {
			String username = "u"+Math.random();
			setValue("#{person.username}", username);
			setValue("#{register.password}", "password");
			setValue("#{register.passwordConfirmation}", "password");
			assert invokeMethod("#{register.createPerson}").equals("home");
			assert getValue("#{person.username}").equals(username);
			Long personId = Long.parseLong(getValue("#{person.id}").toString());
			assert personId > 0;
			return personId;
		}
		
	}

}
