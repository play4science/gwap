/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
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