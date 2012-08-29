/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.tools;

import gwap.model.resource.ArtResource;

/**
 * @author wieser
 */
public interface ArtResourceCacheBean {

	public abstract ArtResource getArtResource(String name);

}