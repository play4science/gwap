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

package gwap.wrapper;

import gwap.model.GameType;
import gwap.model.Highscore;

import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.datamodel.DataModel;

@Name("highscoreSet")
public class HighscoreSet {
	@DataModel              private List<Highscore> highscoreAll;
	@DataModel              private List<Highscore> highscoreDaily;
	@DataModel              private List<Highscore> highscoreMonthly;
	@DataModel              private List<Highscore> highscoreLastMonth;
							private GameType gameType;
							
	public List<Highscore> getHighscoreAll() {
		return highscoreAll;
	}
	public void setHighscoreAll(List<Highscore> highscoreAll) {
		this.highscoreAll = highscoreAll;
	}
	public List<Highscore> getHighscoreMonthly() {
		return highscoreMonthly;
	}
	public void setHighscoreMonthly(List<Highscore> highscoreMonthly) {
		this.highscoreMonthly = highscoreMonthly;
	}
	public List<Highscore> getHighscoreLastMonth() {
		return highscoreLastMonth;
	}
	public void setHighscoreLastMonth(List<Highscore> highscoreLastMonth) {
		this.highscoreLastMonth = highscoreLastMonth;
	}
	public List<Highscore> getHighscoreDaily() {
		return highscoreDaily;
	}
	public void setHighscoreDaily(List<Highscore> highscoreDaily) {
		this.highscoreDaily = highscoreDaily;
	}
	public GameType getGameType() {
		return gameType;
	}
	public void setGameType(GameType gameType) {
		this.gameType = gameType;
	}
	
	

}
