/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.elearn;
import gwap.model.GameConfiguration;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;

/**
 * @author Mislav Boras, Fabian Kneißl
 */
@Name("elearnTerminaBackstage")
@Scope(ScopeType.CONVERSATION)
public class TerminaBackstage extends Termina{

	/**
	 * 
	 */
	private static final long serialVersionUID = 600839;	
	
	@RequestParameter
	private Long gameConfigurationId;

	@RequestParameter
	private String externalSessionId;	
	
	@RequestParameter
	private String externalUsername;
	
	public String getExternalSessionId() {
		return externalSessionId;
	}

	public void setExternalSessionId(String externalSessionId) {
		this.externalSessionId = externalSessionId;
	}

	public Long getGameConfigurationId() {
		return gameConfigurationId;
	}

	public void setGameConfigurationId(Long gameConfigurationId) {
		this.gameConfigurationId = gameConfigurationId;
	}
	
	@Override
	public void startGameSession() {
		if (gameType != null) {
			log.info("Not starting game session again, already started!");
			return;
		}
		gameConfiguration = entityManager.find(GameConfiguration.class, gameConfigurationId);
		startGameSession("elearnTerminaBackstage");
		gameSession.setExternalSessionId(externalSessionId);
		person.setExternalUsername(externalUsername);
	}
	
	@Override
	protected void adjustGameConfiguration() {
		// only when it is not predefined
		if (gameConfigurationId == null)
			super.adjustGameConfiguration();
	}
	
	@Override
	public Integer getRoundsLeft() {
		if (elearnTermBean.updateSensibleTerm() != null)
			return 1;
		else
			return 0;
	}

	public String getExternalUsername() {
		return externalUsername;
	}

	public void setExternalUsername(String externalUsername) {
		this.externalUsername = externalUsername;
	}

}
