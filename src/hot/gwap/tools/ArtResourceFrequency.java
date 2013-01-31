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
