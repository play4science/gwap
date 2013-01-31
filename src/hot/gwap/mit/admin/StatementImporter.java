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

package gwap.mit.admin;

import gwap.mit.TextHelper;
import gwap.model.Person;
import gwap.model.action.Bet;
import gwap.model.action.StatementAnnotation;
import gwap.model.resource.Location;
import gwap.model.resource.Location.LocationType;
import gwap.model.resource.Statement;
import gwap.tools.StatementHelper;
import gwap.wrapper.ImportedStatement;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import au.com.bytecode.opencsv.CSVReader;

/**
 * @author Fabian Kneißl
 */
@Name("mitAdminStatementImporter")
@Scope(ScopeType.PAGE)
public class StatementImporter implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@In						private EntityManager entityManager;
	@Logger					private Log log;
	@In						private FacesMessages facesMessages;
	@In						private Person person;
	
	private byte[] data;
	private String name;
	private long size;
	private String contentType;
	private List<ImportedStatement> statements;
	
	public void upload() {
		if (data == null) {
			return;
		}
		log.info("Uploaded statement csv file #0 of size #1", name, size);
		Reader inFile = new InputStreamReader(new ByteArrayInputStream(data));
		CSVReader csvReader = new CSVReader(inFile, ',', '"', 1); // reader, separator, delimiter, skip #files
		String[] line;
		statements = new ArrayList<ImportedStatement>();
		try {
			while ((line = csvReader.readNext()) != null) {
				// "area", "region", "province", "description", "statement", "category", "italianoStandard", "comment", "creator"
				//    0        1          2            3             4            5               6              7         8
				if (line[4] != null && line[4].trim().length() > 0) {
					Location location = parseLocation(line[2], LocationType.PROVINCE);
					if (location == null)
						location = parseLocation(line[1], LocationType.REGION);
					if (location == null)
						location = parseLocation(line[0], LocationType.AREA);
					
					ImportedStatement s = new ImportedStatement();
					s.setLocation(location);
					s.setStatement(line[4].trim());
					s.setItalianoStandard(line[6].trim());
					
					String annotation = line[3];
					if (annotation != null)
						annotation = annotation.trim();
					if (line[4].toLowerCase().indexOf(annotation.toLowerCase()) >= 0)
						s.setDescription(annotation);
					
					if (location != null)
						statements.add(s);
				}
			}
			facesMessages.add("Prepared #0 statements, please review them for correctness and submit if correct.", statements.size());
			log.info("#0 statements parsed", statements.size());
		} catch (IOException e) {
			statements = null;
			log.error("Error parsing csv file #0", e, name);
		}
	}
	
	public void doImport() {
		Date date = new Date();
		for (ImportedStatement s : statements) {
			Statement statement = new Statement();
			statement.setCreator(person);
			statement.setEnabled(true);
			entityManager.persist(statement);
			
			// Tokens
			StatementHelper.createStatementTokens(statement, s.getStatement(), entityManager);
			if (s.getItalianoStandard() != null && s.getItalianoStandard().length() > 0)
				StatementHelper.createStatementStandardTokens(statement, s.getItalianoStandard(), entityManager);
			
			// Location
			Bet bet = new Bet();
			bet.setCreated(date);
			bet.setLocation(s.getLocation());
			bet.setPerson(person);
			bet.setResource(statement);
			entityManager.persist(bet);
			
			// Annotation
			if (s.getDescription() != null && s.getDescription().length() > 0) {
				List<String> tokens = TextHelper.splitIntoTokens(s.getDescription());
				int startTokenNr = -1;
				int length = tokens.size();
				for (int i = 0; i < statement.getStatementTokens().size(); i++) {
					if (statement.getStatementTokens().get(i).getToken().getValue().equalsIgnoreCase(tokens.get(0))) {
						boolean found = true;
						for (int j = 1; j < length; j++) {
							if (!statement.getStatementTokens().get(i+j).getToken().getValue().equalsIgnoreCase(tokens.get(j))) {
								found = false;
								break;
							}
						}
						if (found) {
							startTokenNr = i;
							break;
						}
					}
				}
				if (startTokenNr >= 0) {
					StatementAnnotation sa = new StatementAnnotation();
					sa.setCreated(new Date());
					sa.setStatement(statement);
					sa.setPerson(person);
					sa.setText(StatementAnnotation.PREDEFINED);
					entityManager.persist(sa);
					for (int i = startTokenNr; i < startTokenNr + length; i++) {
						sa.getStatementTokens().add(statement.getStatementTokens().get(i));
					}
				} else {
					log.info("Did not find previously located tokens");
				}
			}
		}
		statements = null;
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
	public List<ImportedStatement> getStatements() {
		return statements;
	}
	
	private Location parseLocation(String value, LocationType locationType) {
		Location result = null;
		if (value != null) {
			value = value.trim();
			if (value.length() > 0) {
				if (value.equals("Forlì") && locationType == LocationType.PROVINCE)
					value = "Forlì-Cesena";
				else if (value.equals("Trentino") && locationType == LocationType.REGION)
					value = "Trentino-Alto Adige";
				else if (value.equals("Emilia Romagna"))
					value = "Emilia-Romagna";
				else if (value.equals("Friuli Venezia Giulia"))
					value = "Friuli-Venezia Giulia";
				try {
					result = (Location) entityManager.createNamedQuery("location.byNameAndType")
						.setParameter("name", value)
						.setParameter("type", locationType)
						.getSingleResult();
				} catch (NoResultException e) {
					log.info("No location found for name=#0, type=#1", value, locationType);
				}
			}
		}
		return result;
	}
}
