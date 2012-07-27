package gwap.test;

import org.testng.annotations.Test;


public class LabelingGameTest extends CustomSeamTest {
	
	@Test
	public void testAll() throws Exception {
		new MyComponentTest() {
			@Override
			@SuppressWarnings("unused")
			protected void testComponents() throws Exception {
				Long person1Id = createPerson();
				Long person2Id = createPerson();
				Long person3Id = createPerson();
				
				
			}
		}.run();
	}
}
