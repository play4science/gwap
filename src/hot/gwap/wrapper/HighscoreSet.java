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
