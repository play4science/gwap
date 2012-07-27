/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.wrapper;

import gwap.model.resource.Location;

/**
 * @author kneissl
 */
public class LocationPercentage extends Percentage {

	private Location location;
	
	public LocationPercentage() { }
	
	public LocationPercentage(Location location, Number sum, Number total) {
		super(sum, total);
		this.location = location;
	}



	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
	
}
