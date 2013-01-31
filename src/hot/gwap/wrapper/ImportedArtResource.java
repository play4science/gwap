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

package gwap.wrapper;

import gwap.model.resource.ArtResource;


/**
 * @author  Fabian Kneißl
 */
public class ImportedArtResource {
	private String path;
	private String externalId;
	private String title;
	private String artistForename;
	private String artistSurname;
	private String dateCreated;
	private String location;
	private String institution;
	private String origin;
	private Boolean easement;

	public ImportedArtResource() {
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getArtistForename() {
		return artistForename;
	}

	public void setArtistForename(String artistForename) {
		this.artistForename = artistForename;
	}

	public String getArtistSurname() {
		return artistSurname;
	}

	public void setArtistSurname(String artistSurname) {
		this.artistSurname = artistSurname;
	}

	public String getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public Boolean getEasement() {
		return easement;
	}

	public void setEasement(Boolean easement) {
		this.easement = easement;
	}

	public ArtResource toArtResource() {
		ArtResource r = new ArtResource();
		r.setDateCreated(dateCreated);
		r.setEasement(easement);
		r.setExternalId(externalId);
		r.setInstitution(institution);
		r.setLocation(location);
		r.setOrigin(origin);
		r.setPath(path);
		return r;
	}

}
