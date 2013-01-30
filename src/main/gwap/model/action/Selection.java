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
