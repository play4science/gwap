/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.tools;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.sun.faces.renderkit.html_basic.TextRenderer;

/**
 * This Renderer passes through the specified attributes to the html output.
 * 
 * @author Fabian Kneissl
 */
public class InputRenderer extends TextRenderer {
	
	private static final String[] PASS_THROUGH_ATTRIBUTES = { "placeholder" }; 
	
	@Override
	protected void getEndTextToRender(FacesContext facesContext, UIComponent uiComponent, String currentValue) throws IOException {
		ResponseWriter responseWriter = facesContext.getResponseWriter();
		
		for (String attribute : PASS_THROUGH_ATTRIBUTES) {
			Object value = uiComponent.getAttributes().get(attribute);
			if (value != null)
				responseWriter.writeAttribute(attribute, value, attribute);
		}
		
		super.getEndTextToRender(facesContext, uiComponent, currentValue);
	}

}
