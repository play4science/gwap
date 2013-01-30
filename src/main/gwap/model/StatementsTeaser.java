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

package gwap.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@NamedQueries({
		@NamedQuery(name = "statementsTeaser.all", query = "from StatementsTeaser"),
		@NamedQuery(name = "statementsTeaser.allForDateGeneration", query = "select s from StatementsTeaser s where s.publicationDate=null order by s.id "),
		@NamedQuery(name = "statementsTeaser.latestByPublicationDate", 
			query = "from StatementsTeaser where publicationDate <= current_date() order by publicationDate desc"),
		@NamedQuery(name = "statementsTeaser.orderedByPublicationDate", 
			query = "from StatementsTeaser where publicationDate != null order by publicationDate desc")
})
@Entity
public class StatementsTeaser implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@OneToMany(cascade=CascadeType.ALL,mappedBy="statementsTeaser")
	private List<StatementWithGeoPoint> statementList = new ArrayList<StatementWithGeoPoint>();

	@Lob
	private String teaser;
	
	@Temporal(TemporalType.DATE)
	private Date publicationDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<StatementWithGeoPoint> getStatementList() {
		return statementList;
	}

	public void setStatementList(List<StatementWithGeoPoint> statementList) {
		this.statementList = statementList;
	}

	public String getTeaser() {
		return teaser;
	}

	public void setTeaser(String teaser) {
		this.teaser = teaser;
	}

	public Date getPublicationDate() {
		return publicationDate;
	}

	public void setPublicationDate(Date publicationDate) {
		this.publicationDate = publicationDate;
	}

	

}
