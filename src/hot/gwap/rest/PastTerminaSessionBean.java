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

package gwap.rest;
import gwap.model.Person;
import gwap.model.resource.Term;
import gwap.tools.CustomSourceBean;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

/**
 * @author beckern
 */
@Name("pastTerminaSession")
public class PastTerminaSessionBean {

	@In EntityManager entityManager;
	@In private Person person;
	@In private CustomSourceBean customSourceBean;
	private Term term;
	
	public String getTaggingData(){
		if(term!= null){
			return term.getTag().getName();
		} else {
			return "not yet set";
		}
		
	}
	
	public List<Term> getPlayedTerms(){
		if(person != null && customSourceBean != null ){
			Query q = entityManager.createNamedQuery("tagging.termsTaggedByPerson");
			q.setParameter("person",person);
			q.setParameter("source", customSourceBean.getSource());
			
			return q.getResultList();
			
		} else {
			return null;
		}
	}
	

	public void setTermName(String termName){
		Query q = entityManager.createNamedQuery("term.byName");
		q.setParameter("termName", termName);
		Term t = (Term)q.getSingleResult();
		this.term = t;
	}
	
	public String getTermName(){
		if(term != null){
			return term.getTag().getName();			
		} else {
			return "";
		}
	}
	
}
