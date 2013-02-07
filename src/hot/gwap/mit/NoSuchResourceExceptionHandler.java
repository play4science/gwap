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

package gwap.mit;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.faces.Redirect;
import org.jboss.seam.pageflow.Pageflow;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author Fabian Kneißl
 */
@Scope(ScopeType.EVENT)
@Name("mitNoSuchResourceExceptionHandler")
public class NoSuchResourceExceptionHandler implements ActionHandler {

	@In Poker mitPoker;
	
	@Override
	@Begin(join = true)
	public void execute(ExecutionContext executionContext) throws Exception {
		Throwable e = executionContext.getException();
		if (Conversation.instance().getViewId() != null) {
//			mitPoker.cancelGame();
			Pageflow.instance().reposition("scoring");
			Conversation.instance().setViewId(null);
		} else {
			Conversation.instance().endBeforeRedirect();
			Redirect redirect = Redirect.instance();
			redirect.setViewId("/pokerSelection.xhtml");
			redirect.execute();
		}
	}
	
	/*@Begin(join = true)
	public String getViewId() throws Exception {
//		mitPoker.cancelGame();
		Pageflow.instance().reposition("scoring");
		Conversation.instance().setViewId(null);
		return Pageflow.instance().getPageViewId();
	}*/

}
