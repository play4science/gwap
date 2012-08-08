/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
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
			query = "from StatementsTeaser where publicationDate <= current_date() order by publicationDate desc")
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
