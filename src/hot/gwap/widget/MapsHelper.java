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

package gwap.widget;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

@Name("mapsHelper")
@Scope(ScopeType.STATELESS)
public class MapsHelper implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Logger                      private Log log;

	// Security measure! Include your method call in this list!
	private static String[] allowedSubmitFunctions = new String[] { "mitRecognize.assignLocation", "mitPoker.assignLocation", "mitNewStatement.createStatement" };

	public Object submitFunction(String classAndMethod) {
		boolean allowed = false;
		for (int i = 0; i < allowedSubmitFunctions.length; i++) {
			if (allowedSubmitFunctions[i].equals(classAndMethod)) {
				allowed = true;
				break;
			}
		}
		
		if (allowed) {
			log.info("Trying to execute submitFunction " + classAndMethod);

			String[] split = classAndMethod.split("\\.");
			String className = split[0];
			String methodName = split[1];
			
			try {
				Object obj = Component.getInstance(className);
				Method method = obj.getClass().getMethod(methodName);
				Object returnValue = method.invoke(obj);
				log.info("Method invoked successfully");
				return returnValue;
			} catch (Throwable t) {
				log.error("Method could not be invoked: "+classAndMethod, t);
			}
		} else {
			log.info("Did not allow submitFunction #0", classAndMethod);
		}
		return null;
	}
}
