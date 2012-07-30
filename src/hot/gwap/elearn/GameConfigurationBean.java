/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.elearn;

import gwap.model.GameConfiguration;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;

/**
 * @author Fabian Kneißl
 */
@Name("elearnGameConfigurationBean")
@Scope(ScopeType.CONVERSATION)
public class GameConfigurationBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@In(required=false) @Out(required=false)
	private GameConfiguration gameConfiguration;
	
	@Factory("gameConfiguration")
	public void initGameConfiguration() {
		gameConfiguration = new GameConfiguration();
		gameConfiguration.setLevel(1);
		gameConfiguration.setBid(2);
		gameConfiguration.setRoundDuration(60);
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
