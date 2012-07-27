/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.tools;

import gwap.model.resource.ArtResource;

import org.jboss.seam.annotations.Name;

@Name("artResourceFrequency")
public class ArtResourceFrequency implements Comparable<ArtResourceFrequency> {

	
	private double count;
	private ArtResource artResource;

	public ArtResourceFrequency() {
	}
	
	public ArtResourceFrequency(ArtResource resource, Long count) {
		this.artResource = resource;
		this.count = count;
	}
	
	public ArtResource getResource() {
		return artResource;
	}

	public void setResource(ArtResource artResource) {
		this.artResource = artResource;
	}

	public double getCount() {
		return count;
	}

	public void setCount(double count) {
		this.count = count;
	}

	public String toString() {
		return "("+artResource.toString()+","+count+")";
	}

	public int compareTo(ArtResourceFrequency o2)
	{
		if (o2!=null)
		{
			double a = count;
            double b = o2.getCount();
            return a < b ? 1 : a == b ? 0 : -1;		
		}
		else
			return 1;
	}
	
}