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
