/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.widget;

import gwap.model.GameType;
import gwap.model.Highscore;
import gwap.model.Person;
import gwap.model.Source;
import gwap.wrapper.HighscoreSet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

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
	@In(required=false)      private Source customSource;
	
	@DataModel               private List<HighscoreSet> highscores=null;

	@DataModel               private List<Highscore> highscoreAll;
	@DataModel               private List<Highscore> highscoreDaily;
	@DataModel               private List<Highscore> highscoreMonthly;
	
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
				set.setHighscoreDaily(updateHighscoreDaily(g));
				set.setHighscoreAll(updateHighscorePerGameSession(g));
			} else {
//				set.setHighscoreAll(updateHighscore(g));
				set.setHighscoreMonthly(updateHighscoreMonthly(g));
				set.setHighscoreDaily(updateHighscoreDaily(g));
			}
			highscores.add(set);
		}
	}

	public List<Highscore> updateHighscorePerGameSession(GameType gameType) {
		log.info("Updating Highscore per GameSession");

		Query query = entityManager.createNamedQuery("highscore.mit.byGameSession");
		query.setMaxResults(20);
		query.setParameter("gametype", gameType);
		List<Highscore> res = query.getResultList();
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
	
	public List<Highscore> updateHighscoreByInterval(GameType gameType, Date dateLowerBound) {
		log.info("Updating Highscore");

		Query query;
		if (customSource == null) {
			query = entityManager.createNamedQuery("highscore.byInterval");
		} else {
			query = entityManager.createNamedQuery("highscore.byIntervalAndSource");
			query.setParameter("source", customSource);
		}
		
		query.setParameter("gametype", gameType);
		
		query.setParameter("dateUpperBound", new Date());
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
				queryPerson.setParameter("dateUpperBound", new Date());
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

