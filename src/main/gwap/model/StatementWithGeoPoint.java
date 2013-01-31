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
