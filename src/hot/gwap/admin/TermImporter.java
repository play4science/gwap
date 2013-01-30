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

package gwap.admin;

import gwap.model.Tag;
import gwap.model.Topic;
import gwap.model.resource.Term;
import gwap.wrapper.ImportedTerm;

import java.io.ByteArrayInputStream;
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

import com.google.common.base.Strings;

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
	private boolean termsEnabled = true;
	
	public void upload() throws Exception {
		if (data == null) {
			return;
		}
		log.info("Uploaded term csv file #0 of size #1", name, size);
		Reader inFile = new InputStreamReader(new ByteArrayInputStream(data));
		CSVReader csvReader = new CSVReader(inFile, ',', '"', 1); // reader, separator, delimiter, skip #lines
		String[] line;
		terms = new ArrayList<ImportedTerm>();
		try {
			while ((line = csvReader.readNext()) != null) {
				// "topic", "rating", "term", "associations"
				//    0         1       2          3
				ImportedTerm t = new ImportedTerm();
				t.setTerm(line[2].trim());
				String[] associationsArray = line[3].split(", *");
				List<String> associations = new ArrayList<String>();
				for (String association : associationsArray) {
					associations.add(association.trim());
				}
				t.setAssociations(associations);
				t.setRating(Integer.parseInt(line[1]));
				t.setTopic(line[0].trim());
				terms.add(t);
			}
			facesMessages.add("Prepared #0 statements, please review them for correctness and submit if correct.", terms.size());
			log.info("#0 statements parsed", terms.size());
		} catch (Exception e) {
			terms = null;
			log.error("Error parsing csv file #0", e, name);
			facesMessages.add("Error parsing csv file: #0", e);
		} finally {
			csvReader.close();
		}
	}
	
	public void doImport() {
		for (ImportedTerm t : terms) {
			Term term = new Term();
			term.setEnabled(termsEnabled);
			term.setRating(t.getRating());
			term.setTag(findOrCreateTag(t.getTerm()));
			entityManager.persist(term);

			List<Tag> confirmedTags = term.getConfirmedTags();
			for (String association : t.getAssociations()) {
				confirmedTags.add(findOrCreateTag(association));
			}
			
			if (!Strings.isNullOrEmpty(t.getTopic())) {
				Topic topic = findOrCreateTopic(t.getTopic());
				topic.getResources().add(term);
			}
		}
		terms = null;
	}
	
	private Topic findOrCreateTopic(String name) {
		Query q = entityManager.createNamedQuery("topic.byName");
		q.setParameter("name", name);
		Topic topic;
		try {
			topic = (Topic) q.getSingleResult();
		} catch (NoResultException e) {
			topic = new Topic();
			topic.setName(name);
			topic.setEnabled(termsEnabled);
			entityManager.persist(topic);
		}
		return topic;
	}

	private Tag findOrCreateTag(String name) {
		Query q = entityManager.createNamedQuery("tag.tagByNameAndLanguage");
		q.setParameter("name", name);
		q.setParameter("language", localeSelector.getLanguage());
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
	public boolean isTermsEnabled() {
		return termsEnabled;
	}
	public void setTermsEnabled(boolean termsEnabled) {
		this.termsEnabled = termsEnabled;
	}
	
}
