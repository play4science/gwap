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
		name = "highscore.byIntervalCustom",
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
		name = "highscore.byGameSession",
		query = "select new Highscore(coalesce(p.personConnected.id, p.id), sum(g.score)) " +
		        "from GameRound g join g.person p " +
		        "where g.gameSession.gameType=:gametype " +
		        "group by coalesce(p.personConnected.id, p.id), g.gameSession.id " +
		        "having sum(g.score)>0 " +
				"order by sum(g.score) desc"),
	@NamedQuery(
		name = "highscore.byGameSessionCustom",
		query = "select new Highscore(coalesce(p.personConnected.id, p.id), sum(g.score)) " +
				"from GameRound g join g.person p join g.resources resource " +
				"where g.gameSession.gameType=:gametype " +
				"and resource.source=:source " +
				"group by coalesce(p.personConnected.id, p.id), g.gameSession.id " +
				"having sum(g.score)>0 " +
		"order by sum(g.score) desc"),
	@NamedQuery(
		name = "highscore.byGameSessionAndInterval",
		query = "select new Highscore(coalesce(p.personConnected.id, p.id), sum(g.score)) " +
		        "from GameRound g join g.person p " +
		        "where g.gameSession.gameType=:gametype " +
				"and g.endDate >= :dateLowerBound and g.endDate <= :dateUpperBound " +
		        "group by coalesce(p.personConnected.id, p.id), g.gameSession.id " +
		        "having sum(g.score)>0 " +
				"order by sum(g.score) desc"),
	@NamedQuery(
			name = "highscore.byGameSessionAndIntervalCustom",
		query = "select new Highscore(coalesce(p.personConnected.id, p.id), sum(g.score)) " +
				"from GameRound g join g.person p join g.resources resource " +
				"where g.gameSession.gameType=:gametype " +
				"and g.endDate >= :dateLowerBound and g.endDate <= :dateUpperBound " +
				"and resource.source=:source " +
				"group by coalesce(p.personConnected.id, p.id), g.gameSession.id " +
				"having sum(g.score)>0 " +
		"order by sum(g.score) desc"),
	@NamedQuery(
			name = "highscore.mit.byAllPersons",
			query = "select new Highscore(coalesce(p.personConnected.id, p.id), sum(a.score)) " +
				"from Action a join a.person p where " +
				"(" +
				"  a.class = Bet and a.revisedBet is null or " +
				"  a.class = PokerBet and a.revisedBet is null or " +
				"  a.class = LocationAssignment or " +
				"  a.class = Characterization or a.class = StatementAnnotation or " +
				"  a.class = Sale and a.purchase is not null or " +
				"  a.class = Purchase and a.sale is not null " +
				") and a.score is not null " +
				"group by coalesce(p.personConnected.id, p.id) order by sum(a.score) desc" ),
	@NamedQuery(
			name = "highscore.mit.bySinglePerson",
			query = "select new Highscore(coalesce(p.personConnected.id, p.id), sum(a.score)) " +
				"from Action a join a.person p where " +
				"(" +
				"  a.class = Bet and a.revisedBet is null or " +
				"  a.class = PokerBet and a.revisedBet is null or " +
				"  a.class = LocationAssignment or " +
				"  a.class = Characterization or " +
				"  a.class = StatementAnnotation or " +
				"  a.class = Sale and a.purchase is not null or " +
				"  a.class = Purchase and a.sale is not null " +
				") and a.score is not null and " +
				"coalesce(p.personConnected, p.id) = :person " +
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
