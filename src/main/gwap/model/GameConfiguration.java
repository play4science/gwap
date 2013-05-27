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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * @author Fabian Kneißl
 */
@NamedQueries({
	@NamedQuery(name="gameConfiguration.byAll", 
			query = "select gc from GameConfiguration gc where topic=:topic and roundDuration=:roundDuration and level=:level and bid=:bid"),
	@NamedQuery(name="gameConfiguration.byAllWithoutTopic", 
			query = "select gc from GameConfiguration gc where roundDuration=:roundDuration and level=:level and bid=:bid and topic is null"),
	@NamedQuery(name="gameConfiguration.byBidAndRoundDuration", 
			query = "select gc from GameConfiguration gc where topic is null and roundDuration=:roundDuration and level is null and bid=:bid"),
	@NamedQuery(name="gameConfiguration.byTopicBidAndRoundDuration", 
			query = "select gc from GameConfiguration gc where roundDuration=:roundDuration and level is null and bid=:bid and topic=:topic"),
	@NamedQuery(name="gameConfiguration.all", 
			query = "select gc from GameConfiguration gc join gc.topic t where t.id is not null order by gc.id"),
	@NamedQuery(name="gameConfiguration.getById",
			query = "select gc from GameConfiguration gc join gc.topic t where gc.id=:id")
})
@Entity
@Name("gameConfiguration")
@Scope(ScopeType.CONVERSATION)
public class GameConfiguration implements Serializable {

	private static final long serialVersionUID = -3067599122846685355L;

	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne
	private Topic topic;

	private Integer roundDuration;

	private Integer level;

	private Integer bid;

	public static GameConfiguration deepCopy(GameConfiguration gc) {
		GameConfiguration copy = new GameConfiguration();
		copy.setTopic(gc.getTopic());
		copy.setRoundDuration(gc.getRoundDuration());
		copy.setLevel(gc.getLevel());
		copy.setBid(gc.getBid());
		return copy;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Topic getTopic() {
		return topic;
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
	}

	public Integer getRoundDuration() {
		return roundDuration;
	}

	public void setRoundDuration(Integer roundDuration) {
		this.roundDuration = roundDuration;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getBid() {
		return bid;
	}

	public void setBid(Integer bid) {
		this.bid = bid;
	}

}
