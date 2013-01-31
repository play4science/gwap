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

package gwap.tools;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

@Name("gwapLogger")
@Scope(ScopeType.STATELESS)
public class GwapLogger implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Logger                  private Log log;
	
	@Observer("org.jboss.seam.postSetVariable.resoure")
	public void postSetVariableResource() {
		log.info("postSetVariableResource");
	}
	@Observer("org.jboss.seam.postRemoveVariable.resoure")
	public void postRemoveVariableResource() {
		log.info("postRemoveVariableResource");
	}
	@Observer("org.jboss.seam.postDestroyContext.PAGE")
	public void postDestroyContextPAGE() {
		log.info("postDestroyContextPAGE");
	}
	
	
/*
    org.jboss.seam.preSetVariable.<name> — called when the context variable <name> is set
    org.jboss.seam.postSetVariable.<name> — called when the context variable <name> is set
    org.jboss.seam.preRemoveVariable.<name> — called when the context variable <name> is unset
    org.jboss.seam.postRemoveVariable.<name> — called when the context variable <name> is unset
    org.jboss.seam.preDestroyContext.<SCOPE> — called before the <SCOPE> context is destroyed
    org.jboss.seam.postDestroyContext.<SCOPE> — called after the <SCOPE> context is destroyed
    org.jboss.seam.beginConversation — called whenever a long-running conversation begins
    org.jboss.seam.endConversation — called whenever a long-running conversation ends
    org.jboss.seam.beginPageflow.<name> — called when the pageflow <name> begins
    org.jboss.seam.endPageflow.<name> — called when the pageflow <name> ends
    org.jboss.seam.createProcess.<name> — called when the process <name> is created
    org.jboss.seam.endProcess.<name> — called when the process <name> ends
    org.jboss.seam.initProcess.<name> — called when the process <name> is associated with the conversation
    org.jboss.seam.initTask.<name> — called when the task <name> is associated with the conversation
    org.jboss.seam.startTask.<name> — called when the task <name> is started
    org.jboss.seam.endTask.<name> — called when the task <name> is ended
    org.jboss.seam.postCreate.<name> — called when the component <name> is created
    org.jboss.seam.preDestroy.<name> — called when the component <name> is destroyed
    org.jboss.seam.beforePhase — called before the start of a JSF phase
    org.jboss.seam.afterPhase — called after the end of a JSF phase
 */
}
