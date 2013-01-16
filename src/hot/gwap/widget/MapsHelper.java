/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
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
