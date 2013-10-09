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

package gwap.admin;

import gwap.model.Person;
import gwap.model.Source;
import gwap.model.resource.ArtResource;
import gwap.model.resource.ArtResourceTitle;
import gwap.wrapper.ImportedArtResource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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

	private static final String FILENAME_REGEXP = "[A-Za-z0-9_.,()-]+";
	@In						private EntityManager entityManager;
	@Logger					private Log log;
	@In						private FacesMessages facesMessages;
	@In                     private LocaleSelector localeSelector;
	@In(create=true)		private ArtResourceEnabled adminArtResourceEnabled;
	
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
		Reader inFile = new InputStreamReader(new ByteArrayInputStream(data));
		Source source = entityManager.find(Source.class, sourceId);
		try {
			parse(inFile, source);
			facesMessages.add("Prepared #0 resources, please review them for correctness and submit if correct.", resources.size());
			log.info("#0 statements parsed", resources.size());
		} catch (Exception e) {
			resources = null;
			log.error("Error parsing csv file #0", e, name);
			facesMessages.add("Error parsing csv file: #0", e);
		}
	}
	
	public void parse(Reader inFile, Source source) throws Exception {
		CSVReader csvReader = new CSVReader(inFile, ',', '"', 1); // reader, separator, delimiter, skip #rows
		String[] line;
		resources = new ArrayList<ImportedArtResource>();
		HashSet<String> allFilenames = new HashSet<String>();
		try {
			while ((line = csvReader.readNext()) != null) {
				// filename, image id, title, artistForename, artistSurname, year created, location, institution, origin, easement
				//     0        1        2         3               4              5            6           7          8     9
				if (line.length != 10)
					throw new ImportException("Malformed row, wrong number of columns: '"+line+"'");
				ImportedArtResource r = new ImportedArtResource();
				r.setPath(getContentOf(line, 0));
				r.setExternalId(getContentOf(line, 1));
				r.setTitle(getContentOf(line, 2));
				// Intelligently find forename and surname
				//Pattern p = Pattern.compile("(?:(.*) )?((?:\\p{Lower}+ )?(?:\\p{Alpha}+')?\\p{Upper}[\\p{Alpha}-]+)");
				r.setArtistForename(getContentOf(line, 3));
				r.setArtistSurname(getContentOf(line, 4));
				r.setDateCreated(getContentOf(line, 5));
				r.setLocation(getContentOf(line, 6));
				r.setInstitution(getContentOf(line, 7));
				r.setOrigin(getContentOf(line, 8));
				String easementAsString = getContentOf(line, 9);
				if (easementAsString != null) {
					if ("true".equalsIgnoreCase(easementAsString))
						r.setEasement(true);
					else if ("false".equalsIgnoreCase(easementAsString))
						r.setEasement(false);
					else
						throw new ImportException("Easement should be either 'true' or 'false' and not '"+easementAsString+"'");
				}
				// Check for illegal characters in filename
				if (!r.getPath().matches(FILENAME_REGEXP))
					throw new ImportException("Filename must not contain characters other than "+FILENAME_REGEXP+": "+r.getPath());
				// Check for correct year
//				if (r.getDateCreated() != null && !r.getDateCreated().matches(".*[1-9][0-9]*.*"))
//					throw new ImportException("Year created does not represent a year: "+r.getDateCreated());
				
				// Check for duplicate filenames
				if (allFilenames.contains(r.getPath()))
					throw new ImportException("Duplicate entry for filename '"+r.getPath()+"'.");
				allFilenames.add(r.getPath());
				
				// Check if image file exists
				String filePath = source.getUrl() + r.getPath();
				if (!new File(filePath).canRead()) {
					log.error("Image with filename '"+filePath+"' does not exist.");
				} else {
					resources.add(r);
				}
			}
		} finally {
			csvReader.close();
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
		Source source = entityManager.find(Source.class, sourceId);
		for (ImportedArtResource r : resources) {
			ArtResource artResource = r.toArtResource();
			entityManager.persist(artResource);
			
			ArtResourceTitle arTitle = new ArtResourceTitle();
			arTitle.setTitle(r.getTitle());
			arTitle.setResource(artResource);
			entityManager.persist(arTitle);

			artResource.setSource(source);
			Person artist = findOrCreateArtist(r.getArtistForename(), r.getArtistSurname());
			artResource.setArtist(artist);
			
			adminArtResourceEnabled.updateArtResource(artResource);
		}
		facesMessages.add("Successfully imported #0 images.", resources.size());
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
			person.setUsername("");
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
