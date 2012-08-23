/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.tools;

import gwap.game.AbstractGameSessionBean;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;

/**
 * Provides methods for looking up the country, region, and city
 * from an IP address.
 * 
 * @author Fabian Kneissl
 */
@Name("ipBasedLocationBean")
@Scope(ScopeType.APPLICATION)@AutoCreate
public class IpBasedLocationBean implements Serializable {

	@Logger
	private Log log;
	
	private LookupService lookupService;
	
	@Create
	public void startService() {
		URL url = AbstractGameSessionBean.class.getResource("/GeoLiteCity.dat");
		try {
			lookupService = new LookupService(url.getFile(), LookupService.GEOIP_MEMORY_CACHE);
			log.info("Started Lookup Service");
		} catch (IOException e) {
			log.warn("Could not start LookupService", e);
		}
		
	}
	
	@Destroy
	public void stopService() {
		if (lookupService != null)
			lookupService.close();
		log.info("Stopped Lookup Service");
	}
	
	/**
	 * Lookup the location (country, region, city) from an IP address.
	 * 
	 * @param ipAddress
	 * @return Location
	 */
	public Location findByIpAddress(String ipAddress) {
		if (lookupService == null)
			return null;
		else {
			Location location = lookupService.getLocation(ipAddress);
			return location;
		}
	}
}
