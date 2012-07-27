/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.model.action;

import gwap.model.GameRound;
import gwap.model.Person;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;

/**
 * The action is the base class for all user involvement.
 * 
 * @author Fabian Kneißl
 */
@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class Action implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id	@GeneratedValue
	protected Long id;

	@ManyToOne	protected Person person;
	@ManyToOne	protected GameRound gameRound;
	
	protected Date created;
	
	protected Integer score;
	
	public Action() {
		created = new Date();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public GameRound getGameRound() {
		return gameRound;
	}

	public void setGameRound(GameRound gameRound) {
		this.gameRound = gameRound;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}
}
