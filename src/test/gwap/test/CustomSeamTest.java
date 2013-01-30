/*
 * This file is part of gwap
 *
 * Copyright (C) 2013
 * Project play4science
 * Lehr- und Forschungseinheit für Programmier- und Modellierungssprachen
 * Ludwig-Maximilians-Universität München
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
