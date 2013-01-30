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
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import org.jboss.seam.annotations.Name;

/**
 * Various game types or variants can be represented here.
 * 
 * @author Christoph Wieser
 */

@NamedQueries( { 
	@NamedQuery(name = "gameType.select",
			query = "select g from GameType g where name=:name"),
	@NamedQuery(name = "gameType.all",
			query = "select g from GameType g"),
	@NamedQuery(name = "gameType.byPlatform",
			query = "select g from GameType g where g.platform=:platform order by label,name"),
	@NamedQuery(name = "gameType.byEnabledPlatform",
			query = "select g from GameType g where g.platform=:platform and g.enabled=true order by label,name") 
})

@Entity
@Name("gameType")
public class GameType implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id	@GeneratedValue
	private Long id;
	
	@OneToMany(mappedBy="gameType")	private List<GameSession> gameSessions = new ArrayList<GameSession>();
	
	private String name;
	private String label;
	private String description;
	private Integer rounds;
	private Integer players;
	private Integer roundDuration;
	private String platform;
	private String workflow;
	private Boolean enabled;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getRounds() {
		return rounds;
	}

	public void setRounds(Integer rounds) {
		this.rounds = rounds;
	}

	public Integer getPlayers() {
		return players;
	}

	public void setPlayers(Integer players) {
		this.players = players;
	}

	public Integer getRoundDuration() {
		return roundDuration;
	}

	public void setRoundDuration(Integer roundDuration) {
		this.roundDuration = roundDuration;
	}

	public String getWorkflow() {
		return workflow;
	}

	public void setWorkflow(String workflow) {
		this.workflow = workflow;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public List<GameSession> getGameSessions() {
		return gameSessions;
	}

	public void setGameSessions(List<GameSession> gameSessions) {
		this.gameSessions = gameSessions;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public String toString() {
		return name;
	}
}
