package gwap.test;

import org.testng.annotations.Test;


public class RegisterTest extends CustomSeamTest {
	
	@Test
	public void testAll() throws Exception {
		new MyComponentTest() {
			@Override
			protected void testComponents() throws Exception {
				Long personId = createPerson();
				assert personId > 0;
			}
		}.run();
	}
}
