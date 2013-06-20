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
import gwap.model.Tag;
import gwap.model.Topic;
import gwap.model.resource.Term;
import gwap.tools.CustomSourceBean;
import gwap.wrapper.BackstageAnswer;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.richfaces.model.TreeNodeImpl;

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
	private boolean foreignWrongRequested = true;
	private boolean ownWrongRequested = true;

	private Integer maxForeigns = 15;
	private Integer maxOwns = 15;

	private TreeNodeImpl<Item> rootNode;
	private String currentSource;
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("userResults")
	public Response getTaggingData(@QueryParam("term") String termName, 
									@QueryParam("owr") String owr, 
									@QueryParam("fwr") String fwr,
									@QueryParam("mon") String mon,
									@QueryParam("mfn") String mfn){
		JSONObject jsonObject = new JSONObject();
		setTermName(termName);
		
		setWrongRequests(fwr,owr);
		
		setMaxNodes(mfn,mon);
		
		if(term!= null){
			jsonObject.put("term", term.getTag().getName());
			
			//own tags
			JSONArray owns = new JSONArray();
			Query t = entityManager.createNamedQuery("tagging.answersByPersonAndResource");
			t.setParameter("resourceId", term.getId());
			t.setParameter("person", this.person);
//			t.setMaxResults(10);
			List<Tag> ownTags = t.getResultList();

			ArrayList<String> ownTagNames = new ArrayList<String>();
			for(Tag tag : ownTags){
				ownTagNames.add(tag.getName());
			}
			
			//foreign tags
			JSONArray foreigns = new JSONArray();
			
			int[] minMaxAppearences = new int[2];
			addToJSONArray(owns, foreigns, ownTagNames, minMaxAppearences, "tagging.topCorrectAnswersGeneral", "directMatch");
			addToJSONArray(owns, foreigns, ownTagNames, minMaxAppearences, "tagging.topUnknownAnswersGeneral", "indirectMatch");
			addToJSONArray(owns, foreigns, ownTagNames, minMaxAppearences, "tagging.topWrongAnswersGeneral", "WRONG");
			
			JSONArray topics = getTopicOfTerm();
			
			jsonObject.put("foreigns", foreigns);
			jsonObject.put("owns", owns);
			jsonObject.put("maxApp", minMaxAppearences[1]);
			jsonObject.put("minApp", minMaxAppearences[0]);
			jsonObject.put("topics", topics);
			
		} else {
			return Response.status(Status.BAD_REQUEST).build();
		}
		return Response.ok(jsonObject.toString(), MediaType.APPLICATION_JSON).build();
	}
	
	private JSONArray getTopicOfTerm() {
		Query q = customSourceBean.query("topic.byResource");
		q.setParameter("resource", this.term);
		List<Topic> tops = q.getResultList();
		
		JSONArray arr = new JSONArray();
		for(Topic t : tops){
			arr.add(t.getName());
		}
		return arr;
	}

	private void setMaxNodes(String mfn, String mon) {
		try {
			int maxforeign = Integer.parseInt(mfn.trim());
			this.maxForeigns = maxforeign;
		} catch(NumberFormatException nfe){	}
		try {
			int maxown = Integer.parseInt(mon.trim());
			this.maxOwns = maxown;
		} catch(NumberFormatException nfe){}
	}

	private void setWrongRequests(String fwr, String owr) {
		if(fwr.equals("false")){
			this.foreignWrongRequested = false;
		} else {
			this.foreignWrongRequested = true;
		}
		if(owr.equals("false")){
			this.ownWrongRequested = false;
		} else {
			this.ownWrongRequested = true;
		}
	}

	private void addToJSONArray(JSONArray owns, JSONArray foreigns,
			ArrayList<String> ownTagNames, int[] minMaxAppearences,
			String queryName, String matchType) {
		Query q = entityManager.createNamedQuery(queryName);
		q.setParameter("resourceId", term.getId());
	//	q.setMaxResults(5);
		List<BackstageAnswer> correctTags = q.getResultList();

		for(BackstageAnswer b : correctTags){
			JSONObject ob = wrapUpJson(b,matchType);
			
			minMaxAppearences[0] = Math.min(minMaxAppearences[0], b.getAppearence());
			minMaxAppearences[1] = Math.max(minMaxAppearences[1], b.getAppearence());
			boolean own = ownTagNames.contains(b.getTerm());
			if(filter(b,own,matchType)){
				if(own){
					if(owns.size() < maxOwns){
						owns.add(ob);
					}
				} else {
					if(foreigns.size() < maxForeigns){
						foreigns.add(ob);
					}
				}
			}
		}
	}

	private boolean filter(BackstageAnswer b, boolean own, String matchType) {
		boolean requested = false;
		if(matchType.equals("WRONG")){
			if(own && ownWrongRequested)
				requested = true;
			if(!own && foreignWrongRequested)
				requested = true;
		} else {
			requested = true;
		}
			
		boolean moreThanTwo = b.getAppearence() > 2 || own;
		
		return moreThanTwo && requested;
	}

	
	private JSONObject wrapUpJson(BackstageAnswer b, String string) {
		JSONObject ob = new JSONObject();
		
		ob.put("tag", b.getTerm());

		int app = b.getAppearence();
		
		ob.put("appearence", app);
		ob.put("matchType", string);

		return ob;
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
	
	public List<Topic> getAvailableTopics(){
		Query q = customSourceBean.query("topic.enabled");
		return q.getResultList();
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
	
	public boolean isForeignWrongRequested() {
		return foreignWrongRequested;
	}

	public boolean isOwnWrongRequested() {
		return ownWrongRequested;
	}


	public void setForeignWrongRequested(boolean foreignWrongRequested) {
		this.foreignWrongRequested = foreignWrongRequested;
	}

	public void setOwnWrongRequested(boolean ownWrongRequested) {
		this.ownWrongRequested = ownWrongRequested;
	}


	public String getMaxForeigns() {
		if(maxForeigns == null){
			maxForeigns = new Integer(15);
		}
		return "" + maxForeigns;
	}

//	public void setMaxForeigns(String maxForeigns) {
//		try{
//			this.maxForeigns = Integer.parseInt(maxForeigns.trim());
//		} catch(NumberFormatException nfe){
//			
//		}
//	}

	public String getMaxOwns() {
		if(maxOwns == 0){
			maxOwns = new Integer(15);
		}
		return "" + maxOwns;
	}

//	public void setMaxOwns(String maxOwns) {
//		try{
//			this.maxOwns = Integer.parseInt(maxOwns.trim());
//		} catch (NumberFormatException nfe) {}
//	}

	
    public TreeNodeImpl<Item> getTreeNode() {
        if (rootNode == null || ! customSourceBean.getSource().equals(currentSource )) {
            loadTree();
        }
        
        return rootNode;
    }
    
	private void loadTree() {
		currentSource = customSourceBean.getSource();
		int counter = 0;
		rootNode = new TreeNodeImpl<Item>();
		for(Topic  top : this.getAvailableTopics()){
			TreeNodeImpl<Item> topicNode = new TreeNodeImpl<Item>();
			topicNode.setData(new Item("topic", top.getName()));
			for(Term ter : getPlayedTermsOfTopic(top)){
				TreeNodeImpl<Item> termNode = new TreeNodeImpl<Item>();
				termNode.setData(new Item("term", ter.getTag().getName()));
				termNode.setParent(topicNode);
				topicNode.addChild(ter.getTag().getName(), termNode);
				counter ++;
			}
			rootNode.addChild(top.getName(), topicNode);
			counter ++;
		}
	}

	private List<Term> getPlayedTermsOfTopic(Topic topic) {
		if(person != null && customSourceBean != null ){
			Query q = entityManager.createNamedQuery("tagging.termsOfTopicTaggedByPerson");
			q.setParameter("person",person);
			q.setParameter("source", customSourceBean.getSource());
			q.setParameter("topic", topic);
			return q.getResultList();
			
		} else {
			return null;
		}
	}
	
	
    public class Item{
    	String type;
    	String val;
    	public Item(String type, String val ){
    		this.type = type;
    		this.val = val;
    	}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}	
		public String getVal() {
			return val;
		}
		public void setVal(String val) {
			this.val = val;
		}
    	public String toString(){
    		return val;
    	}
    }
    
}