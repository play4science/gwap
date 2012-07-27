/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.model.action;

import gwap.model.resource.Resource;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * A description saves which user used a tag to describe a resource.
 * In contrast to a tag, a description has not yet been verified.
 * 
 * @author Bartholomäus Steinmayr
 */

/*@NamedQueries( { 
	@NamedQuery(
			name = "tagging.tagFrequencyByResouceAndLanguage",
			query = "select new TagFrequency(t.tag.name, count(t.tag.name)) " +
					"from Tagging t " +
					"where t.resource=:resource and t.tag.language=:language " +
					"group by t.tag.name " +
					"having count(t.tag.name) >= :threshold"),
	@NamedQuery(
			name = "tagging.randomTagByResourceAndLanguage",
			query = "select distinct t.tag " +
					"from Tagging t " +
					"where  t.resource=:resource and t.tag.language=:language " +
					""),
	@NamedQuery(
			name = "tagging.taggingsByTag",
			query = "select count(*) from Tagging t where t.tag=:tag") 
})
*/

@Entity
@Name("selection")
@Scope(ScopeType.EVENT)
public class Selection extends Action {

	private static final long serialVersionUID = 1L;

	@ManyToOne	private Resource resource;
				private Boolean correct;

	public Boolean getCorrect() {
		return correct;
	}

	public void setCorrect(Boolean correct) {
		this.correct = correct;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}
	
	@Override
	public String toString() {
		return (getPerson()!=null?getPerson().toString():"null") + (correct?"correctly":"falsely")+" selected " + (resource!=null?resource.toString():"null");
	}
	
}