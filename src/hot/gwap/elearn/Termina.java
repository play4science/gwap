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

package gwap.elearn;

import gwap.game.AbstractGameSessionBean;
import gwap.model.GameConfiguration;
import gwap.model.Tag;
import gwap.model.action.Tagging;
import gwap.model.resource.Term;
import gwap.tools.TagSemantics;
import gwap.wrapper.MatchingTag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.LocaleSelector;

/**
 * @author Katharina Krug, Fabian Kneißl
 */
@Name("elearnTermina")
@Scope(ScopeType.CONVERSATION)
public class Termina extends AbstractGameSessionBean {

	private static final int WRONG_ASSOCIATION_SCORE = -1;

	private static final long serialVersionUID = -4140570198882726459L;

	@In(required=false) @Out(required=false)	protected GameConfiguration gameConfiguration;
	@In(create=true)							protected TermBean elearnTermBean;
	@In(required=false) @Out(required=false)	protected Term term;
	@In											protected LocaleSelector localeSelector;
	
	protected GameConfiguration nextGameConfiguration;
	protected String association;
	protected Integer foundAssociations;

	protected List<Tag> tags;
	protected List<String> answers;
	protected List<Tag> previousTaggings;

	protected MatchingTag lastAssociation;

	public List<Tag> getPreviousTaggings() {
		return previousTaggings;
	}

	@Override
	public void startGameSession() {
		if (gameConfiguration != null)
			nextGameConfiguration = gameConfiguration;
		startGameSession("elearnTermina");
	}
	
	@Override
	public boolean startRound() {
		if (!super.startRound())
			return false;
		
		lastAssociation = null;
		
		previousTaggings = new ArrayList<Tag>();
		foundAssociations = 0;
		
		adjustGameConfiguration();
		gameRound.setGameConfiguration(gameConfiguration);
		
		term = elearnTermBean.updateTerm(gameConfiguration);
			
		gameRound.getResources().add(term);
		return true;
	}
	
	protected void adjustGameConfiguration() {
		if (nextGameConfiguration != null) {
			gameConfiguration = nextGameConfiguration;
		} else if (gameConfiguration == null) {
			gameConfiguration = new GameConfiguration();
			gameConfiguration.setLevel(1);
			gameConfiguration.setBid(2);
			gameConfiguration.setRoundDuration(60);
		}

		// New method: Level gets updated if no more terms exist (see endRound)
		// gameConfiguration.setLevel(1 + ((gameRound.getNumber()-1) / 10));
		
		// find GameConfiguration if it exists, otherwise create it
		try {
			Query q;
			if (gameConfiguration.getTopic() == null) {
				q = entityManager.createNamedQuery("gameConfiguration.byAllWithoutTopic");
			} else {
				q = entityManager.createNamedQuery("gameConfiguration.byAll");
				q.setParameter("topic", gameConfiguration.getTopic());
			}
			q.setParameter("level", gameConfiguration.getLevel());
			q.setParameter("bid", gameConfiguration.getBid());
			q.setParameter("roundDuration", gameConfiguration.getRoundDuration());
			q.setMaxResults(1);
			gameConfiguration = (GameConfiguration) q.getSingleResult();
		} catch (NoResultException e) {
			entityManager.persist(gameConfiguration);
		}
		
		nextGameConfiguration = GameConfiguration.deepCopy(gameConfiguration);
	}

	@Override
	protected void loadNewResource() {
		// this is done in the other two methods		
	}
	
	public void startRoundPalette() {
		startRound();
		
		// 1. Create list of shown tags
		int maxNrResults = gameConfiguration.getBid();
		
		// Could be selected in a more intelligent way :)
		List<Tag> notRelatedTags = elearnTermBean.updateRandomTagsNotRelated(term, maxNrResults);

		List<Tag> rejectedTags = term.getRejectedTags();
		Collections.shuffle(rejectedTags);
		
		tags = new ArrayList<Tag>();
		
		// Add wrong entries
		int rejectedNr = 0;
		for (int i = 0; i < maxNrResults; i++) {
			if (Math.random() > 0.33333 && rejectedNr < rejectedTags.size())
				tags.add(rejectedTags.get(rejectedNr++));
			else
				tags.add(notRelatedTags.get(i));
		}
		
		// Add correct entries
		List<Tag> confirmedTags = term.getConfirmedTags();
		Collections.shuffle(confirmedTags);
		for (int i = 0; i < maxNrResults; i++) {
			tags.add(confirmedTags.get(i));
		}
		Collections.shuffle(tags);
		
		// 2. Create store for the user's selection
		answers = new ArrayList<String>();
	}
	
	/**
	 * Action for normal game round (not for palette mode)
	 */
	public void choose() {
		if (association == null || association.length() == 0) {
			facesMessages.addFromResourceBundle("termina.term.tooShort");
			return;
		}
		if (TagSemantics.containsNotNormalized(previousTaggings, association) != null) {
			facesMessages.addFromResourceBundle("termina.term.duplicate");
			lastAssociation = new MatchingTag("duplicate");
			log.info("Association #0 has already been said for term #1", association, term);
			return;
		}
	
		Tagging tagging = new Tagging();
		initializeAction(tagging);
		tagging.setResource(term);
		
		Tag tag = TerminaMatching.checkAssociationInList(association, term.getConfirmedTags());

		lastAssociation = new MatchingTag(association);
		
		if (tag != null) {
			log.info("Association '#0' is correct for term '#1'", association, term);
			facesMessages.addFromResourceBundle("termina.term.correct");
			tagging.setTag(tag);
			foundAssociations++;
			tagging.setScore(scoreMultiplicator());
			currentRoundScore += scoreMultiplicator();
			lastAssociation.setDirectMatch(true);
			lastAssociation.setScore(tagging.getScore());
		} else {
			tag = TerminaMatching.checkAssociationInList(association, term.getRejectedTags());
			if (tag != null) {
				log.info("Association '#0' is wrong for term '#1'", association, term);
				facesMessages.addFromResourceBundle("termina.term.wrong");
				tagging.setTag(tag);
				tagging.setScore(WRONG_ASSOCIATION_SCORE);
				currentRoundScore += WRONG_ASSOCIATION_SCORE;
				lastAssociation.setScore(tagging.getScore());
			} else {
				log.info("Association '#0' is unknown for term '#1'", association, term);
				facesMessages.addFromResourceBundle("termina.term.unknown");
				tagging.setTag(findOrCreateTag(association));
				lastAssociation.setIndirectMatch(true);
			}
		}
		previousTaggings.add(tagging.getTag());
		entityManager.persist(tagging);
		gameRound.getActions().add(tagging);
		association = "";
	}
	
	@Override
	public void endRound() {
		currentRoundScore -= scoreMultiplicator() * (gameConfiguration.getBid() - foundAssociations);
		super.endRound();
		entityManager.flush();
		if (getRoundsLeft().equals(0)) {
			nextGameConfiguration.setLevel(gameConfiguration.getLevel()+1);
			log.info("Next level: #0", nextGameConfiguration.getLevel());
		}
	}
	
	public String choosePalette() {
		foundAssociations = 0;
		for (String tag : answers) {
			Tagging tagging = new Tagging();
			initializeAction(tagging);
			tagging.setResource(term);
			Tag tagT = findOrCreateTag(tag);
			tagging.setTag(tagT);
			if (term.getConfirmedTags().contains(tagT)) {
				foundAssociations++;
				tagging.setScore(scoreMultiplicator());
			} else {
				foundAssociations--;
				tagging.setScore(-scoreMultiplicator());
			}
			entityManager.persist(tagging);
			gameRound.getActions().add(tagging);
			log.info("Chose tag #0", tagT);
		}
		if (foundAssociations > 0) {
			currentRoundScore += foundAssociations*scoreMultiplicator();
		} else {
			foundAssociations = 0;
		}
		if (foundAssociations == gameConfiguration.getBid())
			facesMessages.addFromResourceBundle("termina.term.correct");
		return "next";
	}

	private int scoreMultiplicator() {
		int zeitbonus = 1;
		if (gameConfiguration.getRoundDuration() <= 15) {
			zeitbonus = 4;
		} else if (gameConfiguration.getRoundDuration() <= 30) {
			zeitbonus = 3;
		} else if (gameConfiguration.getRoundDuration() <= 45) {
			zeitbonus = 2;
		}
		
		return zeitbonus * gameConfiguration.getLevel();
	}
	
	@Override
	public Integer getRoundsLeft() {
		if (elearnTermBean.updateTerm(nextGameConfiguration) != null)
			return 1;
		else
			return 0;
	}

	public void riseBid() {
		if (nextGameConfiguration.getBid() < 5)
			nextGameConfiguration.setBid(nextGameConfiguration.getBid() + 1);
	}

	public void lowerBid() {
		if (nextGameConfiguration.getBid() > 1)
			nextGameConfiguration.setBid(nextGameConfiguration.getBid() - 1);
	}

	public void riseRoundDuration() {
		if (nextGameConfiguration.getRoundDuration() < 60)
			nextGameConfiguration.setRoundDuration(nextGameConfiguration.getRoundDuration() + 15);
	}

	public void lowerRoundDuration() {
		if (nextGameConfiguration.getRoundDuration() > 15)
			nextGameConfiguration.setRoundDuration(nextGameConfiguration.getRoundDuration() - 15);
	}

	public GameConfiguration getGameConfiguration() {
		return gameConfiguration;
	}

	public GameConfiguration getNextGameConfiguration() {
		return nextGameConfiguration;
	}

	public String getAssociation() {
		return association;
	}

	public void setAssociation(String association) {
		this.association = association;
	}

	public Integer getFoundAssociations() {
		return foundAssociations;
	}
	
	public List<Tag> getTags() {
		return tags;
	}

	public List<String> getAnswers() {
		return answers;
	}

	public void setAnswers(List<String> answers) {
		this.answers = answers;
	}
	
	public Term getTerm(){
		return this.term;
	}

	/**
	 * Important: Normalize the tag first, e.g. with TagSemantics.normalize()
	 *  
	 * @param tagName
	 * @return
	 */
	public Tag findOrCreateTag(String tagName) {
        if (tagName.length() > 0) {
        	String language = localeSelector.getLanguage();
			log.info("Added '#0' to tags.", tagName);
	
			Query query = entityManager.createNamedQuery("tag.tagByNameAndLanguage");
			query.setParameter("language", language);
			query.setParameter("name", tagName);
			Tag tag;
			try {
				tag = (Tag) query.getSingleResult();
			} catch (NonUniqueResultException e) {
				log.error("The tag #0 (#1) is not unique", tagName, language);
				@SuppressWarnings("unchecked")
				List<Tag> tagList = query.getResultList();
				tag = tagList.get(0);
			} catch(NoResultException e) {
				log.info("The tag #0 (#1) is new", tagName, language);
				tag = new Tag();
				tag.setName(tagName);
				tag.setLanguage(language);
				entityManager.persist(tag);
			}
			return tag;
        } else
        	return null;
	}

	public MatchingTag getLastAssociation() {
		return lastAssociation;
	}

	public void setLastAssociation(MatchingTag lastAssociation) {
		this.lastAssociation = lastAssociation;
	}
	
	
}
