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
