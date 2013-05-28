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

package gwap.widget;

import gwap.model.GameType;
import gwap.model.Highscore;
import gwap.model.Person;
import gwap.tools.CustomSourceBean;
import gwap.wrapper.HighscoreSet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.log.Log;


@Name("highscoreBean")
@Scope(ScopeType.PAGE)
public class HighscoreBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Create	 public void init()    { log.info("Creating"); }
	@Destroy public void destroy() { log.info("Destroying"); }
	
	@Logger                  private Log log;
	@In                      private EntityManager entityManager;
	@In(required=false)		 private Person person;
	@In                      private CustomSourceBean customSourceBean;
	
	@DataModel               private List<HighscoreSet> highscores=null;

	@DataModel               private List<Highscore> highscoreAll;
	@DataModel               private List<Highscore> highscoreDaily;
	@DataModel               private List<Highscore> highscoreMonthly;
	@DataModel               private List<Highscore> highscoreLastMonth;
	
	public List<HighscoreSet> getHighscores()
	{
		if (highscores==null)
			updateHighscores();
		
		return highscores;
	}

	@Factory("highscores") @SuppressWarnings("unchecked")
	public void updateHighscores() {
		highscores = new ArrayList<HighscoreSet>();
		String platform = (String) Component.getInstance("platform");
		
		Query query = entityManager.createNamedQuery("gameType.byEnabledPlatform");
		query.setParameter("platform", platform);
		List<GameType> gameTypes=query.getResultList();
		for (GameType g : gameTypes)
		{
			HighscoreSet set=new HighscoreSet();
			set.setGameType(g);
			if ("accentiurbani".equals(platform)) {
				set.setHighscoreAll(updateHighscorePerGameSession(g));
			} else if ("elearning".equals(platform)) {
				set.setHighscoreDaily(updateHighscorePerGameSessionDistinctPersonDaily(g));
				set.setHighscoreAll(updateHighscorePerGameSessionDistinctPerson(g));
			} else {
//				set.setHighscoreAll(updateHighscore(g));
				set.setHighscoreMonthly(updateHighscoreMonthly(g));
				set.setHighscoreLastMonth(updateHighscoreLastMonth(g));
				set.setHighscoreDaily(updateHighscoreDaily(g));
			}
			highscores.add(set);
		}
	}

	public List<Highscore> updateHighscorePerGameSession(GameType gameType) {
		log.info("Updating Highscore per GameSession");

		Query query = customSourceBean.query("highscore.byGameSession");
		query.setMaxResults(20);
		query.setParameter("gametype", gameType);
		List<Highscore> res = query.getResultList();
		modifyHighscore(res, entityManager);
		
		return res;
	}

	public List<Highscore> updateHighscorePerGameSessionDistinctPerson(GameType gameType) {
		log.info("Updating Highscore per GameSession distinct person");

		Query query = customSourceBean.query("highscore.byGameSession");
		query.setParameter("gametype", gameType);
		List<Highscore> temp = query.getResultList();
		List<Highscore> res = new ArrayList<Highscore>();
		Set<Long> distinctPersonIds = new HashSet<Long>();
		for (Highscore highscore : temp) {
			if (!distinctPersonIds.contains(highscore.getPersonId())) {
				distinctPersonIds.add(highscore.getPersonId());
				res.add(highscore);
				if (res.size() == 20)
					break;
			}
		}
		modifyHighscore(res, entityManager);
		
		return res;
	}

	public List<Highscore> updateHighscorePerGameSessionDistinctPersonDaily(GameType gameType) {
		log.info("Updating Highscore per GameSession distinct person");

		Query query = customSourceBean.query("highscore.byGameSessionAndInterval");
		query.setParameter("gametype", gameType);
		query.setParameter("dateUpperBound", new Date());
		Calendar calendar = new GregorianCalendar();
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		query.setParameter("dateLowerBound", calendar.getTime());
		List<Highscore> temp = query.getResultList();
		List<Highscore> res = new ArrayList<Highscore>();
		Set<Long> distinctPersonIds = new HashSet<Long>();
		for (Highscore highscore : temp) {
			if (!distinctPersonIds.contains(highscore.getPersonId())) {
				distinctPersonIds.add(highscore.getPersonId());
				res.add(highscore);
				if (res.size() == 20)
					break;
			}
		}
		modifyHighscore(res, entityManager);
		
		return res;
	}
	
	public List<Highscore> updateHighscoreDaily(GameType gameType) {
		Date now = new Date();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(now);
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		Date yesterday = calendar.getTime();
		
		return updateHighscoreByInterval(gameType, yesterday);
	}

	public List<Highscore> updateHighscoreMonthly(GameType gameType) {
		Date now = new Date();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(now);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Date beginOfMonth = calendar.getTime();

		return updateHighscoreByInterval(gameType, beginOfMonth);
	}
	
	public List<Highscore> updateHighscoreLastMonth(GameType gameType) {
		Date now = new Date();
		
		// first Day of this month
		Calendar calendarUpper= new GregorianCalendar();
		calendarUpper.setTime(now);
		calendarUpper.set(Calendar.DAY_OF_MONTH, 1);
		calendarUpper.set(Calendar.HOUR_OF_DAY, 0);
		calendarUpper.set(Calendar.MINUTE, 0);
		calendarUpper.set(Calendar.SECOND, 0);
		Date beginOfThisMonth = calendarUpper.getTime();
		
		// first Day of last month
		Calendar calendarLower = (Calendar) calendarUpper.clone();
		calendarLower.add(Calendar.MONTH, -1);
		Date beginOfLastMonth = calendarLower.getTime();

		return updateHighscoreByInterval(gameType, beginOfLastMonth, beginOfThisMonth);
	}
	
	public List<Highscore> updateHighscoreByInterval(GameType gameType, Date dateLowerBound) {
		return updateHighscoreByInterval(gameType, dateLowerBound, new Date());
	}
	
	public List<Highscore> updateHighscoreByInterval(GameType gameType, Date dateLowerBound, Date dateUpperBound) {
		log.info("Updating Highscore");

		Query query;
		query = customSourceBean.query("highscore.byInterval");
		
		query.setParameter("gametype", gameType);
		
		query.setParameter("dateUpperBound", dateUpperBound);
		query.setParameter("dateLowerBound", dateLowerBound);
		query.setMaxResults(5);
		List<Highscore> res = query.getResultList();
		
		if (person != null) {
			boolean containedTopX = false;
			for (Highscore highscore : res) {
				if (highscore.getPersonId().equals(person.getId())) {
					containedTopX = true;
					break;
				}
			}
			if (!containedTopX) {
				Query queryPerson = entityManager.createNamedQuery("highscore.byPersonAndInterval");
				queryPerson.setParameter("gametype", gameType);
				queryPerson.setParameter("dateUpperBound", dateUpperBound);
				queryPerson.setParameter("dateLowerBound", dateLowerBound);
				queryPerson.setParameter("pid", person.getId());
				try {
					Highscore personalHighscore = (Highscore) queryPerson.getSingleResult();
					personalHighscore.setPerson(person);
					res.add(personalHighscore);
				} catch (NoResultException e) {	}
			}
		}
		
		modifyHighscore(res, entityManager);
		return res;
	}
	
	public static void modifyHighscore(List<Highscore> highscoreList, EntityManager entityManager) {
		Query query = entityManager.createNamedQuery("person.byId");
		for (Highscore highscore : highscoreList) {
			if (highscore.getPersonId() != null) {
				query.setParameter("id", highscore.getPersonId());
				highscore.setPerson((Person)query.getSingleResult());
			}
		}
	}
}

