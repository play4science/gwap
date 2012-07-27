/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.model;

import gwap.model.action.Action;
import gwap.model.resource.Resource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * A game session consists of several rounds. The target of a game session
 * could be to tag ten images, hence, the game session would have ten rounds.  
 * 
 * @author Christoph Wieser
 */

@NamedQueries( {
	@NamedQuery(
			name = "gameRound.randomGameRoundByResourceCountAndLimit",
			query = "select g.id from Tagging t " +
					"left join t.gameRound g " +
					"where (select count(r) from g.resources r)=:count " +
					"and g.person.id!=:personid " +
					"and g.gameSession.gameType.id=:gametypeid " +
					"and t.tag.language=:lang " + 
					"group by g.id, g.startDate " +
					"having count(t)>:limit " +
					"order by g.startDate desc"),
	@NamedQuery(
			name = "gameRound.randomIdByGameTypeIdAndNotPersonId",
			query = "select g.id from GameRound g " +
					"where g.gameSession.gameType.id=:gametypeid " +
					"and g.person.id!=:personid " +
					"order by random()"),
	@NamedQuery(
			name = "gameRound.AtLeastTaggingsLimit",
			query = "select g.id from Tagging t " +
					"inner join t.gameRound g " +
					"where t.tag.language=:lang and " +
					"g.id in (:limitlist) " +
					"group by g.id " +
					"having count(t)>:limit"),
	@NamedQuery(
			name = "gameRound.FromSession",
			query = "select g from GameRound g " +
					"where g.gameSession.id=(:sessionid) " +
					"and g.person.id!=null " +
					"order by g.startDate desc"),
	@NamedQuery(
			name = "gameRound.FromSessionNotIdList",
			query = "select g from GameRound g " +
					"where g.gameSession.id=(:sessionid) " +
					"and g.person.id=(:personid) " +
					"and not g.id in (:limitlist) " +
					"order by g.startDate desc")					
					
					
}
)
					

@Entity
@Name("gameRound")
@Scope(ScopeType.CONVERSATION)
public class GameRound implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id	@GeneratedValue
	private Long id;
	
	@ManyToMany							private List<Resource> resources = new ArrayList<Resource>();	
	@ManyToOne							private Person person;
	@ManyToOne                          private GameSession gameSession;
	@OneToMany(mappedBy="gameRound")	private List<Action> actions = new ArrayList<Action>();
	@ManyToMany							private List<Tag> opponentTags = new ArrayList<Tag>();
	
	@ManyToMany							private List<CombinedTag> opponentCombinedTags = new ArrayList<CombinedTag>();
	
	@ManyToOne                          private GameConfiguration gameConfiguration;

	private Integer number;
	private Date startDate;
	private Date endDate;
	private Integer score;
	
	public GameRound() {
		setStartDate(new Date());
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getNumber() {
		return number;
	}
	
	public void setNumber(Integer number) {
		this.number = number;
	}

	public List<Resource> getResources() {
		return resources;
	}

	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}

	public List<Action> getActions() {
		return actions;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}
	
	public String toString() {
		return "round " + number;
	}

	public List<Tag> getOpponentTags() {
		return opponentTags;
	}

	public void setOpponentTags(List<Tag> opponentTags) {
		this.opponentTags = opponentTags;
	}
	
	public List<CombinedTag> getOpponentCombinedTags() {
		return opponentCombinedTags;
	}
	
	public void setOpponentCombinedTags(List<CombinedTag> opponentTags) {
		this.opponentCombinedTags = opponentTags;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public GameSession getGameSession() {
		return gameSession;
	}

	public void setGameSession(GameSession gameSession) {
		this.gameSession = gameSession;
	}

	public GameConfiguration getGameConfiguration() {
		return gameConfiguration;
	}

	public void setGameConfiguration(GameConfiguration gameConfiguration) {
		this.gameConfiguration = gameConfiguration;
	}
	
}
