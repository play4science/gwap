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
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

@NamedQueries({
	@NamedQuery(
		name = "highscore.all",
		query = "select new Highscore(coalesce(p.personConnected.id, p.id), sum(g.score)) " +
				"from GameRound g join g.person p " +
				"where g.gameSession.gameType=:gametype " +
				"group by coalesce(p.personConnected.id, p.id) " +
				"having sum(g.score)>0 " +
				"order by sum(g.score) desc"),
	@NamedQuery(
		name = "highscore.byInterval",
		query = "select new Highscore(coalesce(p.personConnected.id, p.id), sum(g.score)) " +
		        "from GameRound g join g.person p " +
		        "where g.endDate >= :dateLowerBound and g.endDate <= :dateUpperBound " +
		        "and g.gameSession.gameType=:gametype " +
		        "group by coalesce(p.personConnected.id, p.id) " +
		        "having sum(g.score)>0 " +
				"order by sum(g.score) desc"), // TODO: add anonymous games
	@NamedQuery(
		name = "highscore.byIntervalAndSource",
		query = "select new Highscore(coalesce(p.personConnected.id, p.id), sum(g.score)) " +
				"from GameRound g join g.person p join g.resources resource " +
				"where g.endDate >= :dateLowerBound and g.endDate <= :dateUpperBound " +
				"and g.gameSession.gameType=:gametype " +
//				"and p.email != null " +
				"and resource.source=:source " +
                "group by coalesce(p.personConnected.id, p.id), resource.source " +
				"having sum(g.score)>0 " +
				"order by sum(g.score) desc"), 
	@NamedQuery(
		name = "highscore.byPersonAndInterval",
		query = "select new Highscore(coalesce(p.personConnected.id, p.id), sum(g.score)) " +
				"from GameRound g join g.person p " +
				"where g.endDate >= :dateLowerBound and g.endDate <= :dateUpperBound " +
				"and (p.id = :pid or p.personConnected.id = :pid) " +
				"and g.gameSession.gameType=:gametype " +
				"group by coalesce(p.personConnected.id, p.id) "), // TODO: add anonymous games
	@NamedQuery(
		name = "highscore.mit.byGameSession",
		query = "select new Highscore(coalesce(p.personConnected.id, p.id), sum(g.score)) " +
		        "from GameRound g join g.person p " +
		        "where g.gameSession.gameType=:gametype " +
		        "group by coalesce(p.personConnected.id, p.id), g.gameSession.id " +
		        "having sum(g.score)>0 " +
				"order by sum(g.score) desc"),
	@NamedQuery(
		name = "highscore.mit.byPerson",
		query = "select new Highscore(coalesce(p.personConnected.id, p.id), sum(g.score)) " +
				"from GameRound g join g.person p " +
				"where (p = :person or p.personConnected = :person) " +
				"and g.gameSession.gameType=:gametype " +
				"group by coalesce(p.personConnected.id, p.id) "),
	@NamedQuery(
			name = "highscore.mit.byAllPersons",
			query = "select new Highscore(coalesce(p.personConnected.id, p.id), sum(a.score)) " +
				"from Action a join a.person p where " +
				"(" +
				" (a.class = Bet and a.revisedBet is null) or " +
				"  a.class = LocationAssignment or " +
				"  a.class = StatementCharacterization or a.class = StatementAnnotation" +
				") and " +
				"a.person != null and a.score != null " +
				"and a.gameRound.gameSession.gameType.name=:gametype " +
				"group by coalesce(p.personConnected.id, p.id) order by sum(a.score) desc" ),
	@NamedQuery(
			name = "highscore.mit.bySinglePerson",
			query = "select new Highscore(coalesce(p.personConnected.id, p.id), sum(a.score)) " +
				"from Action a join a.person p where " +
				"(" +
				" (a.class = Bet and a.revisedBet is null) or " +
				"  a.class = LocationAssignment or " +
				"  a.class = StatementCharacterization or a.class = StatementAnnotation" +
				") and " +
				"(p = :person or p.personConnected = :person) " +
				"and a.gameRound.gameSession.gameType.name=:gametype " + 
				"group by coalesce(p.personConnected.id, p.id)" )
})

@Entity
public class Highscore implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	private Long personId;
	private Long score;
	@OneToOne
	private Person person;
	private GameType gametype;
	
	public GameType getGameType() {
		return gametype;
	}

	public void setGameType(GameType gametype) {
		this.gametype = gametype;
	}

	public Highscore() {
	}
	
	public Highscore(Long personId, Long score) {
		this.personId = personId;
		this.score = score;
	}

	public Long getScore() {
		return score;
	}

	public void setScore(Long score) {
		this.score = score;
	}
	
	public Long getPersonId() {
		return personId;
	}
	
	public void setPersonId(Long personId) {
		this.personId = personId;
	}
	
	public String toString() {
		return personId + ": " + score;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

}
