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

package gwap.elearn;

import gwap.model.GameConfiguration;
import gwap.model.Topic;
import gwap.tools.CustomSourceBean;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;

/**
 * Provides methods for changing values in the game configuration.
 * This is especially important for setting the configuration before
 * starting a game.
 * 
 * @author Fabian Kneißl
 */
@Name("elearnGameConfigurationBean")
@Scope(ScopeType.CONVERSATION)
public class GameConfigurationBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@In
	private EntityManager entityManager;
	
	@In(required=false) @Out(required=false)
	private GameConfiguration gameConfiguration;

	private List<Topic> availableTopics;
	
	@In private CustomSourceBean customSourceBean;
	
	/**
	 * Creates a new GameConfiguration with some default values
	 */
	@Factory("gameConfiguration")
	public void initGameConfiguration() {
		gameConfiguration = new GameConfiguration();
		gameConfiguration.setBid(2);
		gameConfiguration.setRoundDuration(60);
	}
	
	/**
	 * Retrieves a list of topics available for selection in games
	 * 
	 * @return list of available topics
	 */
	@SuppressWarnings("unchecked")
	public List<Topic> getAvailableTopics() {
		if (availableTopics == null) {
			Query q = customSourceBean.query("topic.enabled");
			availableTopics = q.getResultList();
		}
		return availableTopics;
	}
	
	public void setTopicId(Long topicId) {
		if (topicId == null) {
			gameConfiguration.setTopic(null);
		} else {
			Topic topic = entityManager.find(Topic.class, topicId);
			gameConfiguration.setTopic(topic);
		}
	}
	
	public Long getTopicId() {
		if (gameConfiguration == null)
			return null;
		else
			return gameConfiguration.getId();
	}
	
	public void riseBid() {
		if (gameConfiguration.getBid() < 5)
			gameConfiguration.setBid(gameConfiguration.getBid() + 1);
	}

	public void lowerBid() {
		if (gameConfiguration.getBid() > 1)
			gameConfiguration.setBid(gameConfiguration.getBid() - 1);
	}

	public void riseRoundDuration() {
		if (gameConfiguration.getRoundDuration() < 60)
			gameConfiguration.setRoundDuration(gameConfiguration.getRoundDuration() + 15);
	}

	public void lowerRoundDuration() {
		if (gameConfiguration.getRoundDuration() > 15)
			gameConfiguration.setRoundDuration(gameConfiguration.getRoundDuration() - 15);
	}
}
