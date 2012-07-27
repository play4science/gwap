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
			query = "select gc from GameConfiguration gc where topic=:topic and roundDuration=:roundDuration and level=:level and bid=:bid")
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