/*
 * This file is part of gwap, an open platform for games with a purpose
 *
 * Copyright (C) 2013
 * Project play4science
 * Lehr- und Forschungseinheit für Programmier- und Modellierungssprachen
 * Ludwig-Maximilians-Universität München
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
