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

import gwap.model.resource.ArtResource;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.international.LocaleSelector;

@Name("artResourceFrequencyBean")
public class ArtResourceFrequencyBean {

	@In	private EntityManager entityManager;
	@In private LocaleSelector localeSelector;
	
	public void normalizeAll(List<ArtResourceFrequency> freqs)
	{
		for (ArtResourceFrequency f : freqs)
			normalize(f);	
		
		Collections.sort(freqs);
	}

	public void normalizeAll(List<ArtResourceFrequency> freqs, double factor)
	{
		for (ArtResourceFrequency f : freqs)
			f.setCount(f.getCount()*factor);	
		
		Collections.sort(freqs);
	}
	
	public void normalizeAll(List<ArtResourceFrequency> freqs, double factor, List<ArtResourceFrequency> malus)
	{		
		Map<ArtResource, Double> m=new HashMap<ArtResource, Double>();
		
		for (ArtResourceFrequency f : malus)
			m.put(f.getResource(), f.getCount());
		
		for (ArtResourceFrequency f : freqs)
		{
			double mal=0.0;
			Double t=m.get(f.getResource());
			if (t!=null)
				mal=t;
			
			f.setCount((f.getCount()-mal)*factor);
		}
		
		Collections.sort(freqs);
	}
	

	public void normalize(ArtResourceFrequency f)
	{
		Query query=entityManager.createNamedQuery("artResource.taggingCount");
		query.setParameter("resid", f.getResource().getId());
		query.setParameter("lang", localeSelector.getLanguage());
		double taggings=((Long)query.getSingleResult()).doubleValue();
		f.setCount(f.getCount()/taggings);		
	}


}
