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

package gwap.rest;

import gwap.model.Badge;
import gwap.model.GameRound;
import gwap.model.GameSession;
import gwap.model.GameType;
import gwap.model.Highscore;
import gwap.model.Person;
import gwap.model.resource.ArtResource;
import gwap.wrapper.UserStatistics;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author maders, wieser
 */

@Path("/user")
@Name("user")
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	@In private EntityManager entityManager;
	
	private JSONParser parser = new JSONParser();
	
	@GET
	@Path("/{id:[A-Za-z0-9][A-Za-z0-9]*}")
	public Response getUser(@PathParam("id") String deviceId) {
		Query query = entityManager.createNamedQuery("person.byDeviceId");
		query.setParameter("deviceId", deviceId);
		Person person = (Person) query.getSingleResult();
		
		String username = person.getExternalUsername();
		
		query = entityManager.createNamedQuery("gameRound.statisticsByPlayer");
		query.setParameter("deviceId", deviceId);
		UserStatistics userStatistics = (UserStatistics) query.getSingleResult();
		
		query = entityManager.createNamedQuery("gameRound.gamesWonByPlayer");
		query.setParameter("deviceId", deviceId);
		Long gamesWonByPlayer = ((Number)query.getSingleResult()).longValue();
		
		query = entityManager.createNamedQuery("artResource.byOriginAndDeviceId");
		query.setParameter("origin", ArtResource.ORIGIN_APP_USER);
		query.setParameter("deviceId", deviceId);
		Long photosTaken = ((Number)query.getSingleResult()).longValue();
		
		query = entityManager.createNamedQuery("artResource.byOriginAndDeviceId");
		query.setParameter("origin", ArtResource.ORIGIN_APP_CRIMESCENE);
		query.setParameter("deviceId", deviceId);
		Long crimescenesTaken = ((Number)query.getSingleResult()).longValue();
		
		query = entityManager.createNamedQuery("badge.byDeviceId");
		query.setParameter("deviceId", deviceId);
		List<Badge> unlockedBadges = query.getResultList();

		query = entityManager.createNamedQuery("badge.all");
		List<Badge> allBadges = query.getResultList();
		
		query = entityManager.createNamedQuery("highscore.all");
		query.setParameter("gametype", getGameType());
		List<Highscore> highscores = query.getResultList();
		int highscoreRank = 0;
		for (int i = 0; i < highscores.size(); i++) {
			if (highscores.get(i).getPersonId().equals(person.getId())) {
				highscoreRank = i+1;
				break;
			}
		}
		
		JSONObject userObject = new JSONObject();
		userObject.put("id", deviceId);
		userObject.put("username", username);
		userObject.put("score", userStatistics.getScore());
		userObject.put("playedTime", userStatistics.getSecondsPlayed());
		userObject.put("gamesPlayed", gamesWonByPlayer);
		userObject.put("photosTaken", photosTaken);
		userObject.put("crimescenesTaken", crimescenesTaken);
		userObject.put("coveredDistance", userStatistics.getCoveredDistance());
		userObject.put("rank", highscoreRank);
		
		JSONArray badgesArray = new JSONArray();
		for (Badge badge : allBadges) {
			JSONObject jsonBadge = new JSONObject();
			jsonBadge.put("id", badge.getId());
			jsonBadge.put("name", badge.getName());
			jsonBadge.put("description", badge.getDescription());
			jsonBadge.put("worth", badge.getWorth());
			if (unlockedBadges.contains(badge))
				jsonBadge.put("unlocked", true);
			else
				jsonBadge.put("unlocked", false);
			badgesArray.add(jsonBadge);
		}
		userObject.put("badges", badgesArray);
		
		return Response.ok(userObject.toString(), MediaType.APPLICATION_JSON).build();
	}

	private GameType getGameType() {
		Query query = entityManager.createNamedQuery("gameType.select");
		query.setParameter("name", "inspectorXApp");
		GameType gameType = (GameType) query.getSingleResult();
		return gameType;
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Transactional
	@Path("/{id:[A-Za-z0-9][A-Za-z0-9]*}")
	public Response updateUser(@PathParam("id") String deviceId, String payloadString) {
		JSONObject payload = null;
		try {
			payload = (JSONObject) parser.parse(payloadString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Query query = entityManager.createNamedQuery("person.byDeviceId");
		query.setParameter("deviceId", deviceId);
		Person person = (Person) query.getSingleResult();

		if (payload.containsKey("username")) { // update user
			person.setExternalUsername(payload.get("username").toString());
		} else { // create new gamesession+gameround
			query = entityManager.createNamedQuery("gameSession.byGameType");
			query.setParameter("gameType", getGameType());
			GameSession gameSession;
			try {
				gameSession = (GameSession) query.getSingleResult();
			} catch (NoResultException e) {
				gameSession = new GameSession();
				gameSession.setGameType(getGameType());
				entityManager.persist(gameSession);
			}
			
			int score = Integer.parseInt(payload.get("score").toString());
			int playedTime = Integer.parseInt(payload.get("playedTime").toString());
			double coveredDistance = Double.parseDouble(payload.get("coveredDistance").toString());
			boolean successful = Integer.parseInt(payload.get("gamesPlayed").toString()) == 1;

			GameRound gameRound = new GameRound();
			entityManager.persist(gameRound);
			gameRound.setGameSession(gameSession);
			gameRound.setPerson(person);
			gameRound.setScore(score);
			Calendar cal = GregorianCalendar.getInstance();
			gameRound.setEndDate(cal.getTime());
			cal.add(Calendar.MILLISECOND, -1 * playedTime);
			gameRound.setStartDate(cal.getTime());
			gameRound.setCoveredDistance(coveredDistance);
			gameRound.setSuccessful(successful);
			
			// Unlock new badges
			JSONArray newBadges = (JSONArray) payload.get("badges");
			for (Object o : newBadges) {
				JSONObject badge = (JSONObject) o;
				long badgeId = Long.parseLong(badge.get("id").toString());
				if (Boolean.parseBoolean(badge.get("unlocked").toString())) {
					Badge b = entityManager.find(Badge.class, badgeId);
					person.getBadges().add(b);
				}
			}
			
		}
		entityManager.flush();
		return Response.ok().build();
	}
}
