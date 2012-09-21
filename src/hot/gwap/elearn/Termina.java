/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.elearn;

import gwap.game.AbstractGameSessionBean;
import gwap.model.GameConfiguration;
import gwap.model.Tag;
import gwap.model.action.Tagging;
import gwap.model.resource.Term;
import gwap.tools.TagSemantics;

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
 * @author Kathi Krug, Fabian Kneißl
 */
@Name("elearnTermina")
@Scope(ScopeType.CONVERSATION)
public class Termina extends AbstractGameSessionBean {

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
	

	@Override
	public void startGameSession() {
		if (gameConfiguration != null)
			nextGameConfiguration = gameConfiguration;
		startGameSession("elearnTermina");
	}
	
	@Override
	public void startRound() {
		super.startRound();
		
		previousTaggings = new ArrayList<Tag>();
		foundAssociations = 0;
		
		adjustGameConfiguration();
		gameRound.setGameConfiguration(gameConfiguration);
		
		term = elearnTermBean.updateTerm(gameConfiguration);
			
		gameRound.getResources().add(term);
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
				q = entityManager.createNamedQuery("gameConfiguration.byAllButTopic");
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

		if (term.getConfirmedTags().size() < maxNrResults) {
			gameRound.getResources().remove(term);
			term = elearnTermBean.updateRandomTermMinConfirmedTags(maxNrResults);
			gameRound.getResources().add(term);
		}
		
		// Could be selected in a more intelligent way :)
		tags = elearnTermBean.updateRandomTagsNotRelated(term, maxNrResults);
		
		for (int i = 0; i < maxNrResults; i++) {
			tags.add(term.getConfirmedTags().get(i));
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
			log.info("Association #0 has already been said for term #1", association, term);
			return;
		}
	
		Tagging tagging = new Tagging();
		initializeAction(tagging);
		tagging.setResource(term);
		
		Tag tag = checkAssociation(association, term);
		
		if (tag != null) {
			log.info("Association '#0' is correct for term '#1'", association, term);
			facesMessages.addFromResourceBundle("termina.term.correct");
			tagging.setTag(tag);
			tagging.setScore(scoreMultiplicator());
			foundAssociations++;
			currentRoundScore += scoreMultiplicator();
		} else {
			log.info("Association '#0' is wrong for term '#1'", association, term);
			facesMessages.addFromResourceBundle("termina.term.wrong");
			tagging.setTag(findOrCreateTag(association));
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
			if (TagSemantics.containsNotNormalized(term.getConfirmedTags(), tag) != null)
				foundAssociations++;
			log.info("Chose tag #0", tag);
		}
		currentRoundScore += foundAssociations*scoreMultiplicator();
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
	
	private Tag checkAssociation(String association, Term term) {
		return TagSemantics.containsNotNormalized(term.getConfirmedTags(), association);
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

	/**
	 * Important: Normalize the tag first, e.g. with TagSemantics.normalize()
	 *  
	 * @param recommendedTagName
	 * @return
	 */
	public Tag findOrCreateTag(String recommendedTagName) {
        if (recommendedTagName.length() > 0) {
        	String language = localeSelector.getLanguage();
			log.info("Added '#0' to recommended tags.", recommendedTagName);
	
			Query query = entityManager.createNamedQuery("tag.tagByNameAndLanguage");
			query.setParameter("language", language);
			query.setParameter("name", recommendedTagName);
			Tag tag;
			try {
				tag = (Tag) query.getSingleResult();
			} catch (NonUniqueResultException e) {
				log.error("The tag #0 (#1) is not unique", recommendedTagName, language);
				@SuppressWarnings("unchecked")
				List<Tag> tagList = query.getResultList();
				tag = tagList.get(0);
			} catch(NoResultException e) {
				log.info("The tag #0 (#1) is new", recommendedTagName, language);
				tag = new Tag();
				tag.setName(recommendedTagName);
				tag.setLanguage(language);
				entityManager.persist(tag);
			}
			return tag;
        } else
        	return null;
	}
	
}
