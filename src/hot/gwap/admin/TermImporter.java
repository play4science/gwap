/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.admin;

import gwap.model.Tag;
import gwap.model.resource.Term;
import gwap.wrapper.ImportedTerm;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
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
@Name("adminTermImporter")
@Scope(ScopeType.PAGE)
public class TermImporter {

	@In						private EntityManager entityManager;
	@Logger					private Log log;
	@In						private FacesMessages facesMessages;
	@In                     private LocaleSelector localeSelector;
	
	private byte[] data;
	private String name;
	private long size;
	private String contentType;
	private boolean ignoreFirstLine = false;
	private List<ImportedTerm> terms;
	
	public void upload() {
		if (data == null) {
			return;
		}
		log.info("Uploaded term csv file #0 of size #1", name, size);
		Reader inFile = new InputStreamReader(new ByteArrayInputStream(data));
		CSVReader csvReader = new CSVReader(inFile, ',', '"', 1); // reader, separator, delimiter, skip #files
		String[] line;
		terms = new ArrayList<ImportedTerm>();
		try {
			if (ignoreFirstLine)
				csvReader.readNext();
			while ((line = csvReader.readNext()) != null) {
				// "term", "associations", "rating"
				//    0           1           2
				ImportedTerm t = new ImportedTerm();
				t.setTerm(line[0].trim());
				String[] associationsArray = line[1].split(", *");
				List<String> associations = new ArrayList<String>();
				for (String association : associationsArray) {
					associations.add(association.trim());
				}
				t.setAssociations(associations);
				t.setRating(Integer.parseInt(line[2]));
				
				terms.add(t);
			}
			facesMessages.add("Prepared #0 statements, please review them for correctness and submit if correct.", terms.size());
			log.info("#0 statements parsed", terms.size());
		} catch (IOException e) {
			terms = null;
			log.error("Error parsing csv file #0", e, name);
		}
	}
	
	public void doImport() {
		for (ImportedTerm t : terms) {
			Term term = new Term();
			term.setEnabled(true);
			term.setRating(t.getRating());
			term.setTag(findOrCreateTag(t.getTerm()));
			entityManager.persist(term);

			List<Tag> confirmedTags = term.getConfirmedTags();
			for (String association : t.getAssociations()) {
				confirmedTags.add(findOrCreateTag(association));
			}
			
		}
		terms = null;
	}
	
	private Tag findOrCreateTag(String name) {
		Query q = entityManager.createNamedQuery("tag.tagByNameAndLanguage");
		q.setParameter("name", name);
		q.setParameter("language", "de");
		Tag tag;
		try {
			tag = (Tag) q.getSingleResult();
		} catch (NoResultException e) {
			tag = new Tag();
			tag.setName(name);
			tag.setLanguage("de");
			tag.setBlacklisted(false);
			entityManager.persist(tag);
		}
		return tag;
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
	public boolean isIgnoreFirstLine() {
		return ignoreFirstLine;
	}
	public void setIgnoreFirstLine(boolean ignoreFirstLine) {
		this.ignoreFirstLine = ignoreFirstLine;
	}
	public List<ImportedTerm> getTerms() {
		return terms;
	}
	
}
