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

package gwap.model.resource;

import gwap.model.Source;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

import org.jboss.seam.annotations.Name;

/**
 * Queue for ArtResources that were rarely tagged. The Entities serve as source for tagging.xhtml 
 * 
 * @author Christoph Wieser
 */

@NamedQueries({
	@NamedQuery(name = "artResourceCache.all",       query = "select r from ArtResourceCache r"),
	@NamedQuery(name = "artResourceCache.allRandom", query = "select r from ArtResourceCache r order by random()"),

	@NamedQuery(name = "artResourceCache.allByLanguage",       query = "select r from ArtResourceCache r where r.language=:language"),
	@NamedQuery(name = "artResourceCache.allByLanguageCustom", query = "select r from ArtResourceCache r where r.language=:language and r.source=:source"),
	
	@NamedQuery(name = "artResourceCache.allByLanguageRandom",       query = "select r from ArtResourceCache r where r.language=:language order by random()"),
	@NamedQuery(name = "artResourceCache.allByLanguageRandomCustom", query = "select r from ArtResourceCache r where r.language=:language and r.source=:source order by random()"),

	@NamedQuery(name = "artResourceCache.allByLanguageName",       query = "select r from ArtResourceCache r where r.language=:language and r.name=:name"),
	@NamedQuery(name = "artResourceCache.allByLanguageNameCustom", query = "select r from ArtResourceCache r where r.language=:language and r.name=:name and r.source=:source"),
	
	@NamedQuery(name = "artResourceCache.countByLanguageName",       query = "select count(r.id) from ArtResourceCache r where r.language=:language and r.name=:name"),
	@NamedQuery(name = "artResourceCache.countByLanguageNameCustom", query = "select count(r.id) from ArtResourceCache r where r.language=:language and r.name=:name and r.source=:source"),
	
	@NamedQuery(name = "artResourceCache.allByLanguageRandomName",       query = "select r from ArtResourceCache r where r.language=:language and r.name=:name order by random()"),
	@NamedQuery(name = "artResourceCache.allByLanguageRandomNameCustom", query = "select r from ArtResourceCache r where r.language=:language and r.name=:name and r.source=:source order by random()") })

@Entity
@Name("artResourceCache")
public class ArtResourceCache implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id	@GeneratedValue
	private Long id;
	private String name;
	private String language;
	
	@OneToOne     private ArtResource resource;
	@OneToOne     private Source source;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ArtResource getArtResource() {
		return resource;
	}

	public void setArtResource(ArtResource resource) {
		this.resource = resource;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
