/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.model.action;

import gwap.model.CombinedTag;
import gwap.model.resource.Resource;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * A combination represents the relation between a Resource and a CombinedTag.
 * 
 * @author Florian Störkle
 */


@NamedQueries({
	@NamedQuery(
			name = "combination.randomCombinedTagsByResourceAndLanguage",
			query = "select tag from Combination c " +
					"left join c.combinedTag tag " +
					"where c.resource=:resource and c.language=:language " +
					"group by tag.id, tag.value " +
					"order by random()"),
	@NamedQuery(
			name = "combination.combinedTagsSimpleByLanguage",
			query = "select c from CombinedTag c " +
					"where c.firstTag=:firstTag and c.secondTag=:secondTag and c.firstTag.language=:language"),
	@NamedQuery(
			name = "combination.combinationByResourceAndLanguageAndCombinedTag",
			query = "select c from Combination c " +
					"where c.resource=:resource and c.language=:language and c.combinedTag=:combinedTag")
})

@Entity
@Name("combination")
@Scope(ScopeType.EVENT)
public class Combination extends Action {

	private static final long serialVersionUID = 1L;
	
	@ManyToOne	private CombinedTag combinedTag;
	@ManyToOne	private Resource resource;
	private String language;
	
	public Combination() {
		
	}
	
	public Combination(final CombinedTag tag) {
		this.combinedTag = tag;
	}

	public CombinedTag getCombinedTag() {
		return combinedTag;
	}

	public void setCombinedTag(CombinedTag combinedTag) {
		this.combinedTag = combinedTag;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}
	
	public String getLanguage() {
		return language;
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}

	@Override
	public String toString() {
		if (combinedTag == null) {
			return "Empty combination";
		}
		
		return combinedTag.toString() + " for " +
			(resource == null ? "no resource yet" : "resource with id " + resource.getId());
	}
}
