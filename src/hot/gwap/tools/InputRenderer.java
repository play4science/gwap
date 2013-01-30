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
