/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.elearn;

import gwap.model.GameConfiguration;
import gwap.model.Topic;

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
	
	/**
	 * Creates a new GameConfiguration with some default values
	 */
	@Factory("gameConfiguration")
	public void initGameConfiguration() {
		gameConfiguration = new GameConfiguration();
		gameConfiguration.setLevel(1);
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
			Query q = entityManager.createNamedQuery("topic.enabled");
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
