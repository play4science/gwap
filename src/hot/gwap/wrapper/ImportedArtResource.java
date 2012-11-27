/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.wrapper;

import gwap.model.resource.ArtResource;
import gwap.model.resource.ArtResourceTitle;


/**
 * @author  Fabian Kneißl
 */
public class ImportedArtResource {
	private String filename;
	private String imageID;
	private String title;
	private String artistForename;
	private String artistSurname;
	private String yearCreated;
	private String location;
	private String institution;
	private String origin;
	private Boolean easement;

	public ImportedArtResource() {
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getImageID() {
		return imageID;
	}

	public void setImageID(String imageID) {
		this.imageID = imageID;
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

	public String getYearCreated() {
		return yearCreated;
	}

	public void setYearCreated(String yearCreated) {
		this.yearCreated = yearCreated;
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
		r.setDateCreated(yearCreated);
		r.setEasement(easement);
		r.setExternalId(imageID);
		r.setInstitution(institution);
		r.setLocation(location);
		r.setOrigin(origin);
		r.setPath(filename);
		ArtResourceTitle arTitle = new ArtResourceTitle();
		arTitle.setTitle(title);
		r.getTitles().add(arTitle);
		return r;
	}

}