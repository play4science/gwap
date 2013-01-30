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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

/**
 * @author steinmayr
 */

@Name("dbLimiter")
@Scope(ScopeType.STATELESS)

/* Somewhat hackish solution for database performance issues
 * 
 * Limit internal results of a query, by only processing a random subset of resources 
 * DBLimiter creates a list of random resources (with little overhead)
 * This can then be passed to a query in form of a parameter "limitlist"
 * 
 * In native SQL this could be achieved more efficiently by using subqueries and limit
 * However, HQL does not allow the use of "limit". setMaxResults() does not limit the
 * number of resources processed internally (at least in some cases)
 * */
public class DBLimiter {
	private int limit=1000;
	
	@In	private EntityManager entityManager;
	@Logger private Log log;
	
	public void PrepareLimitedQuery(Query query)
	{
		PrepareLimitedQuery(query, (String)null);		
	}
	
	public void PrepareLimitedQuery(Query query, Query lquery)
	{
		Timer t=new Timer();
		
		lquery.setMaxResults(limit);		
		
		List<Long> limitList=lquery.getResultList();
		log.info("LimitedQuery query time: #0", t.timePassed());
		
		
		query.setParameter("limitlist", limitList);		
		return;		
	}
	
	public void PrepareLimitedQuery(Query query, String source)
	{
		Timer t=new Timer();
		if (source==null)
			source="artResource.idRandom";
			
		Query lquery=entityManager.createNamedQuery(source);
		lquery.setMaxResults(limit);
		
		
		List<Long> limitList=lquery.getResultList();

		log.info("artResource.idRandom query time: #0", t.timePassed());
		
		
		query.setParameter("limitlist", limitList);
		
		return;
	}

}
