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

package gwap.mit;

import gwap.NotEnoughDataException;
import gwap.model.Person;
import gwap.model.action.Bet;
import gwap.model.action.Characterization;
import gwap.model.action.LocationAssignment;
import gwap.model.action.StatementAnnotation;
import gwap.model.resource.Location;
import gwap.model.resource.Location.LocationType;
import gwap.model.resource.LocationHierarchy;
import gwap.model.resource.Resource;
import gwap.model.resource.Statement;
import gwap.model.resource.StatementToken;
import gwap.tools.CharacterizationBean;
import gwap.tools.CharacterizationBean.ResultForType;
import gwap.tools.Pair;
import gwap.wrapper.LocationPercentage;
import gwap.wrapper.Percentage;
import gwap.wrapper.Score;
import gwap.wrapper.StatementTokenPercentage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

/**
 * @author Fabian Kneißl
 */
@AutoCreate
@Name("mitPokerScoring")
@Scope(ScopeType.STATELESS)
public class PokerScoring {
	
	public static final int CHARACTERIZATION_BONUS_30_PERCENT = 5;
	public static final int CHARACTERIZATION_BONUS_50_PERCENT = 10;
	public static final int ANNOTATION_LIKE_MAJORITY = 10;
	public static final int ANNOTATION_LIKE_THIRD = 5;
	private static final int LOCATION_ASSIGNMENT_MAX_SCORE = 10;
	private static final double LOCATION_ASSIGNMENT_ND_FACTOR = 0.03;
	private static final int BET_MAX_SCORE = 100;
	private static final double BET_ND_FACTOR = 0.09;
	public static final int POKER_CORRECT = 10;
	public static final int POKER_CORRECT_DIFFICULT = 20;
	public static final int POKER_OWNER_PER_GUESS = 5;
	public static final int POKER_OWNER_PER_GUESS_LT_10 = 2;
	
	public static final int MIN_NR_FOR_STATISTICS = 3;
	
	@In		private EntityManager entityManager;
	@Logger private Log log;
	@In     private CharacterizationBean characterizationBean;
	
	/**
	 * Returns the score for a person's LocationAssignment
	 * 
	 * @param la
	 * @return null if the location does not equal the predefined location
	 */
	public Score locationAssignment(LocationAssignment la) {
		Percentage percentage = getFuzzyPercentage(la.getLocation(), la.getResource());
		// 100 - percentage, because f(0)=maximumScore
		if (percentage.getTotal() <= 0)
			return null;
		int score = getNormalDistributedScore(100-percentage.getPercentage(), LOCATION_ASSIGNMENT_ND_FACTOR, LOCATION_ASSIGNMENT_MAX_SCORE);
		la.setScore(score);
		log.info("Score for LocationAssignment #0 with percentage #1 is #2", la, percentage, score);
		return new Score(score, percentage);
	}

	/**
	 * Calculates the score for a given LocationAssignment in the poker game
	 * 
	 * @param locationAssignment
	 * @return null if the location does not equal the predefined location
	 */
	public Score poker(LocationAssignment locationAssignment) {
		Query query = entityManager.createNamedQuery("pokerBet.byResourceAndLocation");
		query.setParameter("resource", locationAssignment.getResource());
		query.setParameter("location", locationAssignment.getLocation());
		List<Bet> bets = query.getResultList();
		if (bets.isEmpty())
			return null;
		else { // LocationAssignment.location is the same as bet.location
			Percentage percentage = getRawPercentage(locationAssignment.getLocation(), locationAssignment.getResource());
			int score = POKER_CORRECT;
			if (percentage.getTotal() >= 2 && percentage.getFraction() <= 0.4)
				score = POKER_CORRECT_DIFFICULT;
			locationAssignment.setScore(score);
			return new Score(score, percentage);
		}
	}

	public Integer characterization(Resource resource, Characterization[] characterizations) throws NotEnoughDataException {
		int score = 0;
		
		boolean validCharacterization = false;
		for (Characterization c : characterizations) {
			if (CharacterizationBean.isValueSet(c)) {
				validCharacterization = true;
				break;
			}
		}
		
		if (validCharacterization) {
			boolean hasEnoughData = false;
			for (Characterization c : characterizations) {
				if (CharacterizationBean.isValueSet(c) && !CharacterizationBean.isValueUnknown(c)) {
					ResultForType result = characterizationBean.getResult(resource.getId(), c.getName());
					Percentage percentage = new Percentage(0, result.getTotal());
					// Check if value is there
					for (Pair<Integer, Integer> p : result.getResult()) {
						if (c.getValue().equals(p.a)) {
							percentage = new Percentage(p.b, result.getTotal());
							break;
						}
					}
					if (percentage.getTotal() >= MIN_NR_FOR_STATISTICS) {
						hasEnoughData = true;
						int scoreSingle = 0;
						if (percentage.getFraction() >= 0.5)
							scoreSingle = CHARACTERIZATION_BONUS_50_PERCENT;
						else if (percentage.getFraction() >= 0.3)
							scoreSingle = CHARACTERIZATION_BONUS_30_PERCENT;
						c.setScore(scoreSingle);
						score += scoreSingle;
					}
				}
			}
			if (!hasEnoughData)
				throw new NotEnoughDataException();
		}
		return score;
	}

	public Integer highlighting(StatementAnnotation annotation) throws NotEnoughDataException {
		int score = 0;
		annotation.setScore(score);
		Percentage percentage = getHighlightingPercentage(annotation);
		if (percentage != null) {
			if (percentage.getTotal() > MIN_NR_FOR_STATISTICS) {
				if (percentage.getPercentage() >= 50) {
					score = ANNOTATION_LIKE_MAJORITY;
				} else if (percentage.getPercentage() >= 30) {
					score = ANNOTATION_LIKE_THIRD;
				}
			} else if (percentage.getTotal() == MIN_NR_FOR_STATISTICS) {
				List<StatementAnnotation> allAnnotations = entityManager.createNamedQuery("statementAnnotation.byStatement")
					.setParameter("statement", annotation.getStatement())
					.getResultList();
				for (int i = 0; i < allAnnotations.size(); i++) {
					StatementAnnotation sa = allAnnotations.get(i);
					if (percentage.getPercentage() >= 50) {
						sa.setScore(ANNOTATION_LIKE_MAJORITY);
					} else if (percentage.getPercentage() >= 30) {
						sa.setScore(ANNOTATION_LIKE_THIRD);
					}
					if (sa.getId().equals(annotation.getId()))
						score = sa.getScore();
				}
			} else {
				throw new NotEnoughDataException();
			}
		}
		annotation.setScore(score);
		return score;
	}
	
	public int getPersonScoreSum(Person person) {
		if (person == null || person.getId() == null)
			return 0;
		try {
			person = entityManager.find(Person.class, person.getId());

			// Calculate scores for bets not yet having a score
			List<Bet> bets = (List<Bet>)entityManager.createNamedQuery("bet.byPersonWithoutScore").setParameter("person", person).getResultList();
			for (Bet bet: bets) {
				updateScoreForBet(bet);
			}

			Query q = entityManager.createNamedQuery("highscore.mit.bySinglePerson");
			q.setParameter("gametype", "mitRecognize");
			q.setParameter("person", person);
			int actionScore = numberToInt((Number) ((gwap.model.Highscore)q.getSingleResult()).getScore());

			return actionScore;
		} catch (NoResultException nre) {
			return 0;
		} catch (Exception e) {
			log.warn("Could not calculate person score sum", e);
			return 0;
		}
	}
	
	public void calculateAllBetScores() {
		List<Bet> bets = entityManager.createNamedQuery("bet.allWithPerson").getResultList();
		for (Bet bet : bets)
			updateScoreForBet(bet);
	}
	
	public void updateScoreForBets(Resource resource) {
		List<Bet> bets = entityManager.createNamedQuery("bet.byResource").setParameter("resource", resource).getResultList();
		for (Bet bet : bets)
			updateScoreForBet(bet);
	}

	public void updateScoreForBet(Bet bet) {
		entityManager.refresh(bet);
		bet.setScore(0);
		bet.setCurrentMatch(0);
		if (bet.getPoints() != null) {
			Percentage percentage = getFuzzyPercentage(bet.getLocation(), bet.getResource());
			if (percentage.getTotal() > 0) {
				double deviation = Math.abs(percentage.getPercentage() - bet.getPoints());
				int score = getNormalDistributedScore(deviation, BET_ND_FACTOR, BET_MAX_SCORE);
				log.info("Score for bet #0 with deviation #1 (bet on #3%, really #4%) is #2", bet, deviation, score, bet.getPoints(), percentage);
				bet.setScore(score);
				bet.setCurrentMatch(percentage.getPercentage().intValue());
			}
		}
		entityManager.flush();
	}
	
	public void updateScoreForPokerBets(Resource resource) {
		List<Bet> bets = entityManager.createNamedQuery("pokerBet.byResource")
				.setParameter("resource", resource)
				.getResultList();
		for (Bet bet : bets)
			updateScoreForPokerBet(bet);
	}

	public void updateScoreForPokerBet(Bet bet) {
		entityManager.refresh(bet);
		bet.setScore(0);
		bet.setCurrentMatch(0);
		Percentage percentage = getRawPercentage(bet.getLocation(), bet.getResource());
		// Need to add rounds where the resource was skipped
		Query q = entityManager.createNamedQuery("gameRound.nrRoundsWithResourceAndNoLocationAssignment");
		q.setParameter("gameTypeName", "mitPoker");
		q.setParameter("resource", bet.getResource());
		long nrRounds = ((Number)q.getSingleResult()).longValue();
		percentage = new Percentage(percentage.getSum()-1, percentage.getTotal()-1+nrRounds); // excluding the user's bet
		if (percentage.getTotal() > 0) {
			int score = 0;
			if (percentage.getTotal() < 10 && percentage.getSum() <= 1)
				score = percentage.getTotal()*POKER_OWNER_PER_GUESS_LT_10;
			else if (percentage.getTotal() >= 10 && percentage.getPercentage() <= 10.0)
				score = percentage.getTotal()*POKER_OWNER_PER_GUESS;
			else if (percentage.getTotal() >= 10 && percentage.getPercentage() <= 20.0)
				score = percentage.getTotal()*POKER_OWNER_PER_GUESS_LT_10;
			bet.setScore(score);
			bet.setCurrentMatch(percentage.getPercentage().intValue());
			log.info("Score for poker bet #0 (#1 bet on the same, total #2) is #3", bet, (long)percentage.getSum(), percentage.getTotal(), score);
		}
		entityManager.flush();
	}

	/**
	 * Probability density function of the normal distribution with one variance-like factor.
	 * Example: preMultiplicator=0.09 => f(0)=maximumScore, f(36)=1
	 * @param normalScore
	 * @param preMultiplicator affects the stretch of the function
	 * @param maximumScore maximum score to reach
	 * @return values between maximumScore and 0
	 */
	private int getNormalDistributedScore(double normalScore, double preMultiplicator, int maximumScore) {
		double score = maximumScore * 2.5 * 1.0/Math.sqrt(2*Math.PI) * Math.exp(-0.5*Math.pow(normalScore * preMultiplicator, 2));
		return (int) Math.round(score);
	}

	private Percentage getRawPercentage(Location location, Resource resource) {
		Number sum = (Number) entityManager.createNamedQuery("locationAssignment.countByResourceAndLocation")
				.setParameter("resource", resource).setParameter("location", location).getSingleResult();
		Number total = (Number) entityManager.createNamedQuery("locationAssignment.countByResource")
				.setParameter("resource", resource).getSingleResult();
		return new Percentage(sum, total);
	}
	
	private Percentage getFuzzyPercentage(Location location, Resource resource) {
		Number sum = (Number) entityManager.createNamedQuery("locationAssignment.scoringSumByResourceAndLocation")
				.setParameter("resource", resource).setParameter("location", location).getSingleResult();
		Number total = (Number) entityManager.createNamedQuery("locationAssignment.countByResource")
				.setParameter("resource", resource).getSingleResult();
		return new Percentage(sum, total);
	}

	/**
	 * 
	 * @param originalLocation the location choosen by the user
	 * @param comparedLocation the compared location
	 * @param parents the list of Locations in the higher hierarchy - can be made by calling getLocationHierarchyTree()
	 * @return score for matching
	 */
	public double getFuzzyMatchingValue(Location originalLocation, Location comparedLocation) {
		try {
			Number correlation = (Number) entityManager.createNamedQuery("locationHierarchy.correlationByLocations")
					.setParameter("location", comparedLocation)
					.setParameter("sublocation", originalLocation)
				.getSingleResult();
			return correlation.doubleValue();
		} catch (NoResultException e) {
			log.info("Got no result for locations #0 / #1 :(", originalLocation, comparedLocation);
			return 0;
		}
	}
	
	public List<Location> getLocationHierachyTree(Location rootLocation){
		List<Location> tree = new ArrayList<Location>();
		Location currentLocation = rootLocation;
		
		while(!currentLocation.getType().equals(LocationType.AREA) && !currentLocation.getType().equals(LocationType.COUNTRY)){
			LocationHierarchy nextLocation = (LocationHierarchy) entityManager
					.createNamedQuery("locationHierarchy.bySublocationId")
					.setParameter("sublocationId", currentLocation.getId())
					.getSingleResult();
			tree.add(nextLocation.getLocation());
			currentLocation = nextLocation.getLocation();
		}
		
		return tree;
	}
	
	public Percentage getHighlightingPercentage(StatementAnnotation annotation) {
		if (annotation.getStatementTokens().size() == 0)
			return null;
		try {
			@SuppressWarnings("unchecked")
			List<Long> similarAnnotationsId = entityManager
					.createNamedQuery("statementAnnotation.similarAnnotations")
					.setParameter("statement", annotation.getStatement())
					.setParameter("statementAnnotation", annotation)
					.setParameter("minMatchingTokens", (long)annotation.getStatementTokens().size())
					.getResultList();
			int same = 0;
			for (Long id : similarAnnotationsId) {
				StatementAnnotation a = entityManager.find(StatementAnnotation.class, id);
				if (a.getStatementTokens().size() == annotation.getStatementTokens().size())
					same++;
			}
			int total = numberToInt((Number) entityManager.createNamedQuery("statementAnnotation.countNotEmptyByStatement")
					.setParameter("statement", annotation.getStatement())
					.getSingleResult());
			Percentage percentage = new Percentage(same, total);
			log.info("Highlighting: #0: #1% the same (sum: #2, count: #3)", annotation.getStatement(), percentage.getPercentage(), percentage.getSum(), percentage.getTotal());
			return percentage;
		} catch (Exception e) {
			log.warn("Could not calculate highlighting percentage", e);
			return null;
		}
	}
	
	public Percentage getStatementPercentageFrom(Resource resource, Location location) {
		try {
			int same = numberToInt((Number) entityManager.createNamedQuery("locationAssignment.countByResourceAndLocation").setParameter("resource", resource).setParameter("location", location).getSingleResult());
			int total = numberToInt((Number) entityManager.createNamedQuery("locationAssignment.countByResource").setParameter("resource", resource).getSingleResult());
			Percentage percentage = new Percentage(same, total);
			log.info("Location assignment: #0: #1% for Location #2", resource, percentage, location);
			return percentage;
		} catch (Exception e) {
			log.warn("Could not calculate statement percentage", e);
			return null;
		}
	}
	
	public List<LocationPercentage> getStatementPercentages(Resource resource) {
		List<LocationPercentage> percentages = new ArrayList<LocationPercentage>();
		try {
			int total = numberToInt((Number) entityManager.createNamedQuery("locationAssignment.countByResource").setParameter("resource", resource).getSingleResult());
			List<Object[]> list = entityManager.createNamedQuery("locationAssignment.countAndLocationByResource").setParameter("resource", resource).getResultList();
			for (Object[] countAndLocation : list) {
				Number count = (Number) countAndLocation[0];
				Long locationId = (Long) countAndLocation[1];
				Location location = entityManager.find(Location.class, locationId);
				percentages.add(new LocationPercentage(location, count, total));
			}
			return percentages;
		} catch (Exception e) {
			log.warn("Could not calculate statement percentages", e);
			return null;
		}
	}
	
	public List<StatementTokenPercentage> getHighlightingResult(Statement statement) {

			List<StatementTokenPercentage> highlightingResult = new ArrayList<StatementTokenPercentage>();
			Query q = entityManager.createNamedQuery("statementAnnotation.byToken" )
					.setParameter("statementId", statement.getId());
			List<Object[]> annotationCounts = q.getResultList();
			Map<Long, Integer> annotationCountsMap = new HashMap<Long, Integer>();

			Query q2 = entityManager.createNamedQuery("statementAnnotation.countByStatement" )
					.setParameter("statementId", statement.getId());
			int total = ((Number)q2.getSingleResult()).intValue();
			
			for (Object[] row : annotationCounts) {
				int count = ((Number)row[1]).intValue();
				annotationCountsMap.put((Long)row[0], count);
			}
			for (int i = 0; i<statement.getStatementTokens().size(); i++){
				StatementToken token = statement.getStatementTokens().get(i);
				Integer sum = annotationCountsMap.get(token.getId());
				if (sum == null)
					sum = 0;
				highlightingResult.add(new StatementTokenPercentage(token, sum, total));
			}
			
		return highlightingResult;
	}
	
	public int getHighlightingTotal(Statement statement) {
		Query q2 = entityManager.createNamedQuery("statementAnnotation.countByStatement" )
				.setParameter("statementId", statement.getId());
		int total = ((Number)q2.getSingleResult()).intValue();
		
		return total;
	}
	
	public Bet getBet(Statement statement, Person person) {
		try {
			Query query = entityManager.createNamedQuery("bet.byResourceAndPerson");
			query.setParameter("person", person);
			query.setParameter("resource", statement);
			query.setMaxResults(1);
			Bet bet = (Bet) query.getSingleResult();
			return bet;
		} catch (Exception e) {
			log.warn("No LocationAssignment defined", e);
			return null;
		}
	}
	
	private int numberToInt(Number n) {
		if (n == null)
			return 0;
		else
			return n.intValue();
	}
	
	public String getStatementTokenBorder(StatementTokenPercentage stp, Person person){
		StatementToken statementToken = stp.getStatementToken();
		Query q = entityManager.createNamedQuery("statementAnnotation.byStatementAndPerson");
		q.setParameter("person", person);
		q.setParameter("statement", statementToken.getStatement());
		q.setMaxResults(1);
		List<StatementAnnotation> resultList = q.getResultList();
		if (resultList.size() > 0)
			for (StatementToken token : resultList.get(0).getStatementTokens()) {
				if (token.getId().equals(statementToken.getId()))
					return "border: 4px solid black;";
			}
		return "";
	}
}
