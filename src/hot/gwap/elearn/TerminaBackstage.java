/*
 * This file is part of gwap
 *
 * Copyright (C) 2013
 * Project play4science
 * Lehr- und Forschungseinheit für Programmier- und Modellierungssprachen
 * Ludwig-Maximilians-Universität München
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
		if (elearnTermBean.updateTerm(gameConfiguration) != null)
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
