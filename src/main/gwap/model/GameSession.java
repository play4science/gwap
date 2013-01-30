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
	
	private String externalSessionId;	

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

	public String getExternalSessionId() {
		return externalSessionId;
	}

	public void setExternalSessionId(String externalSessionId) {
		this.externalSessionId = externalSessionId;
	}

	public IpBasedLocation getIpBasedLocation() {
		return ipBasedLocation;
	}

	public void setIpBasedLocation(IpBasedLocation ipBasedLocation) {
		this.ipBasedLocation = ipBasedLocation;
	}

}
