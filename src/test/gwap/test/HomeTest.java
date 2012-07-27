package gwap.test;

import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

public class HomeTest extends SeamTest {

//	@Logger
//	private Log log;
	
	@Test
	public void testRandomResource() throws Exception {
		new ComponentTest() {
			@Override
			protected void testComponents() throws Exception {
//				 Pattern pattern = Pattern.compile("http://.*jpg");
//				 Matcher matcher = pattern.matcher(invokeMethod("#{home.getRandomResourceURL()}").toString());
//				 assert matcher.matches();
//				 
//				 assert invokeMethod("#{home.getDescription()}").toString().length() > 0;
//				 
//				 invokeMethod("#{tagCloud.updateTags()}");
//				 assert Integer.parseInt(invokeMethod("#{tagCloud.taggings.size}").toString()) > 0;
//
//				 invokeMethod("#{tagCloud.getTaggingsByTag()}");
			}
		}.run();
	}
}
