/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.model.action;

import gwap.model.Tag;
import gwap.model.resource.Resource;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * TagATag Data
 * 
 * @author Christoph Wieser
 */

@NamedQueries( { 
	@NamedQuery(
			name = "metatagging.tagFrequencyByMetaTagAndLanguage",
			query = "select new gwap.wrapper.TagFrequency(m.tag.name, count(m.tag.name)) " +
					"from MetaTagging m " +
					"where m.tag.language=:language " +
					"group by m.tag.name " +
					"having count(m.tag.name) >= :threshold " +
					"order by count(m.tag.name) desc"),
	@NamedQuery(
			name = "metatagging.tagFrequencyByMetaTagAndLanguageAndResource",
			query = "select new gwap.wrapper.TagFrequency(m.tag.name, count(m.tag.name)) " +
					"from MetaTagging m " +
					"where m.tagResource=:tagResource and m.tag.language=:language " +
					"group by m.tag.name " +
					"having count(m.tag.name) >= :threshold " +
					"order by count(m.tag.name) desc")
})

@Entity
@Name("metaTagging")
@Scope(ScopeType.EVENT)
public class MetaTagging extends Action {

	private static final long serialVersionUID = 1L;
	
	@ManyToOne	private Tag tag;
	@ManyToOne	private Tag tagResource;
	@ManyToOne	private Resource resource;

	public Tag getTag() {
		return tag;
	}
	
	public void setTag(Tag tag) {
		this.tag = tag;
	}
	
	public Tag getTagResource() {
		return tagResource;
	}

	public void setTagResource(Tag tagResource) {
		this.tagResource = tagResource;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	@Override
	public String toString() {
		if (person!=null)
			return getPerson().toString() + " tagged " + tagResource.toString() + " with " + tag;
		else
			return "The players tagged " + tagResource.toString() + " with " + tag;			
	}
	
}
