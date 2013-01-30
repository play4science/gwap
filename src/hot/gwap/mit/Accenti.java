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

package gwap.mit;

import gwap.game.AbstractGameSessionBean;
import gwap.model.action.Action;
import gwap.model.action.LocationAssignment;
import gwap.model.action.ResourceRating;
import gwap.model.resource.AudioResource;
import gwap.model.resource.Location;
import gwap.model.resource.Location.LocationType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.faces.Redirect;

/**
 * @author Fabian Kneißl
 */
@Name("mitAccenti")
@Scope(ScopeType.CONVERSATION)
public class Accenti extends AbstractGameSessionBean {

	private static final long serialVersionUID = 1L;

	@In(create=true)		private AudioResource audioResource;
	@In                     private AudioResourceBean mitAudioResourceBean;
	
	@Out
	private List<Location> breadcrumbLocations = new ArrayList<Location>();
	
	private Long locationId;
	private Boolean guessedCorrectly;
	private int guessesLeft;
	private final int NR_OF_GUESSES = 3;
	
	@Override
	public void startGameSession() {
		startGameSession("mitAccenti");
	}
	
	@Override
	public void startRound() {
		super.startRound();
		gameRound.getResources().add(audioResource);
		locationId = null;
		guessedCorrectly = null;
		guessesLeft = NR_OF_GUESSES;
		facesMessages.clear();
		if (audioResource == null) {
			log.info("Redirecting to noResourceLeft-page out of pageflow because no resources are left");
			Conversation.instance().endBeforeRedirect();
			Redirect redirect = Redirect.instance();
			redirect.setViewId("/accentiNoResourceLeft.xhtml");
			redirect.execute();
		}
	}
	
	@Override
	protected void loadNewResource() {
		audioResource = mitAudioResourceBean.getAudioResource();	
	}
	
	public boolean assignLocation(Long locationId) {
		this.locationId = locationId;
		return assignLocation();
	}
	
	public boolean assignLocation() {
		guessedCorrectly = false;
		if (locationId == null)
			return false;
		Location location = entityManager.find(Location.class, locationId);
		if (location == null)
			return false;
		for (Action a : gameRound.getActions()) {
			if (a instanceof LocationAssignment) {
				if (((LocationAssignment)a).getLocation().getId() == location.getId())
					return false;
			}
		}
		guessesLeft--;
		LocationAssignment sla = new LocationAssignment();
		sla.setCreated(new Date());
		sla.setLocation(location);
		sla.setPerson(person);
		sla.setResource(audioResource);
		sla.setGameRound(gameRound);
		entityManager.persist(sla);
		gameRound.getActions().add(sla);
		log.info("Assigned location #0 to resource #1", location, audioResource);
		
		guessedCorrectly = audioResource.getLocation().getId().equals(locationId);
		if (guessedCorrectly) {
			facesMessages.addFromResourceBundle("game.accenti.guessCorrect");
			rewardWithPoints();
		} else if (getGuessingEnabled()) {
			facesMessages.addFromResourceBundle("game.accenti.guessIncorrect");
		} else {
			facesMessages.addFromResourceBundle("game.accenti.guessSolution", audioResource.getLocation().getName());
		}
		return true;
	}
	
	private void rewardWithPoints() {
		int points = 0;
		if (guessesLeft == NR_OF_GUESSES - 1)
			points = 100;
		else if (guessesLeft == NR_OF_GUESSES - 2)
			points = 50;
		else if (guessesLeft == NR_OF_GUESSES - 3)
			points = 25;
		currentRoundScore = points;
	}

	public List<Action> getAssignedLocations() {
		return gameRound.getActions();
	}

	public Long getLocationId() {
		return locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}
	
	public List<Location> addToBreadcrumbLocation(Long locationId) {
		Location l = entityManager.find(Location.class, locationId);
		if (l != null)
			breadcrumbLocations.add(l);
		return breadcrumbLocations;
	}
	
	public List<Location> navigateToBreadcrumbLocation(Long locationId) {
		for (int i = 0; i < breadcrumbLocations.size(); i++) {
			if (breadcrumbLocations.get(i).getId().equals(locationId)) {
				for (int j = i+1; j < breadcrumbLocations.size(); j++) {
					breadcrumbLocations.remove(j);
				}
				break;
			}
		}
		return breadcrumbLocations;
	}
	
	public boolean getAudioResourceAvailable() {
		return (mitAudioResourceBean.getAudioResource() != null);
	}
	
	public boolean getGuessingEnabled() {
		return (guessedCorrectly == null || !guessedCorrectly) && guessesLeft > 0;
	}
	
	public boolean getNextEnabled() {
		return (guessedCorrectly != null && guessedCorrectly) || guessesLeft == 0;
	}

	public Boolean getGuessedCorrectly() {
		return guessedCorrectly;
	}
	
	public void updateProfile() {
		try {
			Location hometown = null;
			if (locationId != null)
				hometown = entityManager.find(Location.class, locationId);
			if (hometown != null)
				person.setHometown(hometown);
			log.info("Updating profile to location id #0 (#1)", locationId, hometown);
			entityManager.merge(person);
			facesMessages.add("#{messages['game.profile.updated']}");
		} catch(Exception e) {
			facesMessages.add("#{messages['game.profile.updateError']}");
			log.warn("UpdateProfile failed", e);
		}
	}
	
	public boolean getFillProfile() {
		// in 5th round
		if (roundNr != null && roundNr == 5 && (person.getBirthyear() == null && person.getGender() == null && person.getHometown() == null)) {
			return true;
		}
		return false;
	}
		
	public void setRating(long rating) {
		log.info("Person #0 rated AudioResource #1 #2", person, audioResource, rating);
		ResourceRating resourceRating = new ResourceRating();
		resourceRating.setGameRound(gameRound);
		resourceRating.setPerson(person);
		resourceRating.setRating(rating);
		resourceRating.setResource(audioResource);
		entityManager.persist(resourceRating);
	}
	
	public List<LocationAssignment> getLocationAssignments() {
		List<LocationAssignment> assignments = new ArrayList<LocationAssignment>();
		for (Action a : gameRound.getActions()) {
			if (a instanceof LocationAssignment)
				assignments.add((LocationAssignment)a);
		}
		return assignments;
	}
	
	public List<Location> getHometownSelectItems() {
		Query q = entityManager.createNamedQuery("location.topLevelByHierarchyName");
		q.setParameter("hierarchyName", "mit.accenti.profile");
		List<Location> locations = q.getResultList();
		return locations;
	}
	
	public Location getLocationItaly() {
		Query q = entityManager.createNamedQuery("location.byNameAndType");
		q.setParameter("name", "Italia");
		q.setParameter("type", LocationType.COUNTRY);
		return (Location) q.getSingleResult();
	}
	
}
