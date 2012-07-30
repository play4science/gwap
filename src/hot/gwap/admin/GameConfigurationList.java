/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.admin;

import gwap.model.GameConfiguration;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

/**
 * @author Mislav Boras
 */
@Name("gameConfigurationList")
public class GameConfigurationList extends EntityQuery<GameConfiguration>{
	public GameConfigurationList() {
		setEjbql("select gc from GameConfiguration gc join gc.topic t where t.id is not null order by gc.id");
	}
}
