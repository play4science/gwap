/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.admin;

import gwap.model.Person;
import gwap.model.Source;
import gwap.model.resource.ArtResource;
import gwap.wrapper.ImportedArtResource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.log.Log;

import au.com.bytecode.opencsv.CSVReader;

/**
 * @author Fabian Kneißl
 */
@Name("adminArtResourceImporter")
@Scope(ScopeType.PAGE)
public class ArtResourceImporter {

	private static final String FILENAME_REGEXP = "[A-Za-z0-9_.-]+";
	@In						private EntityManager entityManager;
	@Logger					private Log log;
	@In						private FacesMessages facesMessages;
	@In                     private LocaleSelector localeSelector;
	
	private byte[] data;
	private String name;
	private long size;
	private String contentType;
	private List<ImportedArtResource> resources;
	
	private Long sourceId;
	
	public void upload() {
		if (data == null) {
			return;
		}
		log.info("Uploaded term csv file #0 of size #1", name, size);
		Source source = entityManager.find(Source.class, sourceId);
		Reader inFile = new InputStreamReader(new ByteArrayInputStream(data));
		CSVReader csvReader = new CSVReader(inFile, ',', '"', 1); // reader, separator, delimiter, skip #files
		String[] line;
		resources = new ArrayList<ImportedArtResource>();
		HashSet<String> allFilenames = new HashSet<String>();
		try {
			// Skip first line
			csvReader.readNext();
			while ((line = csvReader.readNext()) != null) {
				if (line.length != 9)
					throw new ImportException("Malformed row, wrong number of columns: '"+line+"'");
				// "filename", "image id", "title", "artist", "year created", "location", "institution", "origin", "easement"
				//     0           1          2         3           4              5            6           7          8
				ImportedArtResource r = new ImportedArtResource();
				r.setFilename(getContentOf(line, 0));
				r.setImageID(getContentOf(line, 1));
				r.setTitle(getContentOf(line, 2));
				String name = getContentOf(line, 3);
				// Intelligently find forename and surname
				Pattern p = Pattern.compile("(.* )?((?:\\p{Lower}+ )?(?:\\p{Alpha}+')?\\p{Upper}[\\p{Alpha}-]+)");
				Matcher matcher = p.matcher(name);
				if (!matcher.matches())
					throw new ImportException("Could not find forename and surname for name '"+name+"'");
				r.setArtistForename(matcher.group(1));
				r.setArtistSurname(matcher.group(2));
				r.setYearCreated(getContentOf(line, 4));
				r.setLocation(getContentOf(line, 5));
				r.setInstitution(getContentOf(line, 6));
				r.setOrigin(getContentOf(line, 7));
				String easementAsString = getContentOf(line, 8);
				if (easementAsString != null) {
					if ("true".equalsIgnoreCase(easementAsString))
						r.setEasement(true);
					else if ("false".equalsIgnoreCase(easementAsString))
						r.setEasement(false);
					else
						throw new ImportException("Easement should be either 'true' or 'false' and not '"+easementAsString+"'");
				}
				// Check for illegal characters in filename
				if (!r.getFilename().matches(FILENAME_REGEXP))
					throw new ImportException("Filename must not contain characters other than "+FILENAME_REGEXP+": "+r.getFilename());
				// Check for correct year
				if (r.getYearCreated() != null && !r.getYearCreated().matches("[1-9][0-9]*"))
					throw new ImportException("Year created does not represent a year: "+r.getYearCreated());
				// Check if image file exists
				String filePath = source.getUrl() + r.getFilename();
				if (!new File(filePath).canRead())
					throw new ImportException("Image with filename '"+filePath+"' does not exist.");
				// Check for duplicate filenames
				if (allFilenames.contains(r.getFilename()))
					throw new ImportException("Duplicate entry for filename '"+r.getFilename()+"'.");
				allFilenames.add(r.getFilename());
				resources.add(r);
			}
			facesMessages.add("Prepared #0 resources, please review them for correctness and submit if correct.", resources.size());
			log.info("#0 statements parsed", resources.size());
		} catch (Exception e) {
			resources = null;
			log.error("Error parsing csv file #0", e, name);
			facesMessages.add("Error parsing csv file: #0", e);
		}
	}
	
	private String getContentOf(String[] line, int i) {
		String content = line[i];
		if (content == null)
			return null;
		content = content.trim();
		if (content.isEmpty())
			return null;
		return content;
	}

	public void doImport() {
		for (ImportedArtResource r : resources) {
			ArtResource artResource = r.toArtResource();
			entityManager.persist(artResource);
			Person artist = findOrCreateArtist(r.getArtistForename(), r.getArtistSurname());
			artResource.setArtist(artist);
		}
		resources = null;
	}
	
	private Person findOrCreateArtist(String forename, String surname) {
		Query q = entityManager.createNamedQuery("person.byForenameAndSurname");
		q.setParameter("forename", forename);
		q.setParameter("surname", surname);
		Person person;
		try {
			person = (Person) q.getSingleResult();
		} catch (NoResultException e) {
			person = new Person();
			person.setForename(forename);
			person.setSurname(surname);
			entityManager.persist(person);
		}
		return person;
	}
	
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}
	public List<ImportedArtResource> getResources() {
		return resources;
	}

	public void setResources(List<ImportedArtResource> resources) {
		this.resources = resources;
	}

	
}
