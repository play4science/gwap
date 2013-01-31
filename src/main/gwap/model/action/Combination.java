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
					"group by tag.id, tag.value, tag.firstTag.id, tag.secondTag.id " +
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
