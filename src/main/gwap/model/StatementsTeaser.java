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
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

@NamedQueries({
		@NamedQuery(name = "statementsTeaser.all", query = "from StatementsTeaser")
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

}
