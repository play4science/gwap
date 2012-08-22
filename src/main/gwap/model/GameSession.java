/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.model;

import gwap.model.resource.IpBasedLocation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * A game session is an instance of a game and can consist of several rounds.
 * 
 * @author Christoph Wieser
 */

@Entity
@Name("gameSession")
@Scope(ScopeType.CONVERSATION)
public class GameSession implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue
	private Long id;

	@ManyToOne								private GameType gameType;
	@OneToMany(mappedBy="gameSession")		private List<GameRound> gameRounds = new ArrayList<GameRound>();
	@ManyToOne                              private IpBasedLocation ipBasedLocation; 

	public List<GameRound> getGameRounds() {
		return gameRounds;
	}

	public void setGameRounds(List<GameRound> gameRounds) {
		this.gameRounds = gameRounds;
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public GameType getGameType() {
		return gameType;
	}

	public void setGameType(GameType gameType) {
		this.gameType = gameType;
	}

	public String toString() {
		return "Id: " + id; 
	}

	public IpBasedLocation getIpBasedLocation() {
		return ipBasedLocation;
	}

	public void setIpBasedLocation(IpBasedLocation ipBasedLocation) {
		this.ipBasedLocation = ipBasedLocation;
	}

}
