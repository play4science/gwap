/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.model;

import gwap.model.resource.GeoPoint;
import gwap.model.resource.Statement;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * @author kneissl
 */
@NamedQueries({
	@NamedQuery(name="statementWithGeoPoint.byStatementsTeaser", query="from StatementWithGeoPoint where statementsTeaser = :statementsTeaser")
})
@Entity
public class StatementWithGeoPoint implements Serializable {
	
	@Id @GeneratedValue
	private Long id;
	
	@ManyToOne
	private Statement statement;
	
	@ManyToOne(cascade=CascadeType.ALL)
	private GeoPoint geoPoint;
	
	@ManyToOne
	private StatementsTeaser statementsTeaser;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Statement getStatement() {
		return statement;
	}

	public void setStatement(Statement statement) {
		this.statement = statement;
	}

	public GeoPoint getGeoPoint() {
		return geoPoint;
	}

	public void setGeoPoint(GeoPoint geoPoint) {
		this.geoPoint = geoPoint;
	}

	public StatementsTeaser getStatementsTeaser() {
		return statementsTeaser;
	}

	public void setStatementsTeaser(StatementsTeaser statementsTeaser) {
		this.statementsTeaser = statementsTeaser;
	}
	
}
