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
import gwap.wrapper.BackstageAnswer;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * @author beckern
 */
@Name("pastTerminaSession")
@Path("pastTerminaSession")
public class PastTerminaSessionBean {

	@In EntityManager entityManager;
	@In private Person person;
	@In private CustomSourceBean customSourceBean;
	private Term term;
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("userResults/{termName}")
	public Response getTaggingData(@PathParam("termName") String termName){
		JSONObject jsonObject = new JSONObject();
		setTermName(termName);
		if(term!= null){
			jsonObject.put("term", term.getTag().getName());
			
			//own tags
			JSONArray owns = new JSONArray();
			Query t = entityManager.createNamedQuery("tagging.answersByPersonAndResource");
			t.setParameter("resourceId", term.getId());
			t.setParameter("person", this.person);
//			t.setMaxResults(10);
			List<String> ownTags = t.getResultList();
			
//			for(Tag tag : ownTags){
//				JSONObject ob = new JSONObject();
//				ob.put("tag", tag.getName());
//				ob.put("matchType", "directMatch");
//				ob.put("appearence", 1);
//				owns.add(ob);
//			}
//			jsonObject.put("owns", owns);
			
			//foreign tags
			JSONArray foreigns = new JSONArray();
			
			//correct
			Query q = entityManager.createNamedQuery("tagging.topCorrectAnswersGeneral");
			q.setParameter("resourceId", term.getId());
			q.setMaxResults(5);
			List<BackstageAnswer> correctTags = q.getResultList();
			int maxForeignAppearences = correctTags.get(0).getAppearence();
			int minForeignAppearences = correctTags.get(0).getAppearence();

			for(BackstageAnswer b : correctTags){
				JSONObject ob = new JSONObject();
				ob.put("tag", b.getTerm());
		
				int app = b.getAppearence();
				if(app > maxForeignAppearences)
					maxForeignAppearences = app;
				if(app < minForeignAppearences)
					minForeignAppearences = app;
				
				ob.put("appearence", app);
				ob.put("matchType", "directMatch");
				
				if(ownTags.contains(b.getTerm())){
					owns.add(ob);
				} else {
					foreigns.add(ob);
				}
			}
			
			//unknown
			Query r = entityManager.createNamedQuery("tagging.topUnknownAnswersGeneral");
			r.setParameter("resourceId", term.getId());
			r.setMaxResults(5);
			List<BackstageAnswer> unknownTags = r.getResultList();
			for(BackstageAnswer b : unknownTags){
				JSONObject ob = new JSONObject();

				ob.put("tag", b.getTerm());
				int app = b.getAppearence();
				if(app > maxForeignAppearences)
					maxForeignAppearences = app;
				if(app < minForeignAppearences)
					minForeignAppearences = app;
		
				ob.put("appearence", app);
				
				ob.put("matchType", "indirectMatch");
				if(ownTags.contains(b.getTerm())){
					owns.add(ob);
				} else {
					foreigns.add(ob);
				}
			}
			
			//wrong
			Query s = entityManager.createNamedQuery("tagging.topWrongAnswersGeneral");
			s.setParameter("resourceId", term.getId());
			s.setMaxResults(5);
			List<BackstageAnswer> wrongTags = s.getResultList();

			for(BackstageAnswer b : wrongTags){
				JSONObject ob = new JSONObject();
				ob.put("tag", b.getTerm());

				int app = b.getAppearence();
				if(app > maxForeignAppearences)
					maxForeignAppearences = app;
				if(app < minForeignAppearences)
					minForeignAppearences = app;
		
				ob.put("appearence", app);
				
				ob.put("matchType", "WRONG");
				if(ownTags.contains(b.getTerm())){
					owns.add(ob);
				} else {
					foreigns.add(ob);
				}
			}
			jsonObject.put("foreigns", foreigns);
			jsonObject.put("owns", owns);
			jsonObject.put("maxForeignApp", maxForeignAppearences);
			jsonObject.put("minForeignApp", minForeignAppearences);
			
			
		} else {
			return Response.status(Status.BAD_REQUEST).build();
		}
		return Response.ok(jsonObject.toString(), MediaType.APPLICATION_JSON).build();
	}
	
	private boolean saidByPerson(String term2) {
		// TODO Auto-generated method stub
		return false;
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
			return null;
		}
	}
	
}
