/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Entity
@Name("gameSessionRating")
@Scope(ScopeType.CONVERSATION)
public class GameSessionRating implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue
	private Long id;
	
	@ManyToOne	private GameSession gameSession;
	@ManyToOne	private Person person;
				private Long rating=0L;
	
				
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public GameSession getGameSession() {
		return gameSession;
	}

	public void setGameSession(GameSession gameSession) {
		this.gameSession = gameSession;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Long getRating() {
		return rating;
	}

	public void setRating(Long rating) {
		this.rating = rating;
	}
}