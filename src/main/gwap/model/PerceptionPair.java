/*
# * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.model;

import gwap.model.action.PerceptionRating;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.jboss.seam.annotations.Name;

/**
 * This is general declaration of PerceptionPairs that must not be used in
 * games.
 * 
 * @author Jonas Hoelzler
 */

@NamedQueries({ @NamedQuery(name = "PerceptionPair.all", query = "select t from PerceptionPair t"),
	@NamedQuery(name = "PerceptionPair.averageRatingByResourceAndPairname", query = "select avg(p.value) from PerceptionPair p join p.perceptionRating r where p.pairname = :pairname and r.resource = :resource")})
@Entity
@Name("perceptionPair")
public class PerceptionPair implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	private Long value;
	private String pairname;

	@ManyToOne
	private PerceptionRating perceptionRating;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}

	public String toString() {
		return pairname + " :" + value;
	}

	public String getPairname() {
		return pairname;
	}

	public void setPairname(String pairname) {
		this.pairname = pairname;
	}

	public void setPerceptionRating(PerceptionRating perceptionRating) {
		this.perceptionRating = perceptionRating;
	}

}
