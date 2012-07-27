/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.mit;

import gwap.NotEnoughDataException;
import gwap.model.Person;
import gwap.model.action.Bet;
import gwap.model.action.LocationAssignment;
import gwap.model.action.StatementAnnotation;
import gwap.model.action.StatementCharacterization;
import gwap.model.resource.Location;
import gwap.model.resource.Location.LocationType;
import gwap.model.resource.LocationHierarchy;
import gwap.model.resource.Resource;
import gwap.model.resource.Statement;
import gwap.model.resource.StatementToken;
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
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

/**
 * @author Fabian Kneißl
 */
@AutoCreate
@Name("mitPokerScoring")
@Scope(ScopeType.STATELESS)
public class PokerScoring {
	
	public static final int CHARACTERIZATION_BONUS_TENDENCY = 5;
	public static final int CHARACTERIZATION_BONUS_10_PERCENT = 10;
	private static final int STATEMENT_CREATED = 10;
	private static final int ANNOTATION_LIKE_MAJORITY = 10;
	private static final int ANNOTATION_LIKE_THIRD = 5;
	private static final int LOCATION_ASSIGNMENT_MAX_SCORE = 10;
	private static final double LOCATION_ASSIGNMENT_ND_FACTOR = 0.03;
	private static final int BET_MAX_SCORE = 100;
	private static final double BET_ND_FACTOR = 0.09;
	
	public static final int MIN_NR_FOR_STATISTICS = 2;
	
	private HashMap<Long, List<Location>> hierarchyTrees = new HashMap<Long, List<Location>>();
	
	@In		private EntityManager entityManager;
	@Logger private Log log;
	
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
	
	public Integer characterization(StatementCharacterization characterization) throws NotEnoughDataException {
		int score = 0;
		characterization.setScore(score);
		score += withinRangeGiveBonus(characterization.getGender(), characterization.getStatement(), "gender");
		score += withinRangeGiveBonus(characterization.getMaturity(), characterization.getStatement(), "maturity");
		score += withinRangeGiveBonus(characterization.getCultivation(), characterization.getStatement(), "cultivation");
		characterization.setScore(score);
		return score;
	}
	
	private int withinRangeGiveBonus(Integer value, Statement statement, String type) throws NotEnoughDataException {
		if (notNull(value)) {
			Percentage percentage = getCharacterizationResult(statement, type);
			if (percentage.getTotal() >= MIN_NR_FOR_STATISTICS) {
				int difference = Math.abs(percentage.getFraction().intValue() - value.intValue());
				if (difference <= 10)
					return CHARACTERIZATION_BONUS_10_PERCENT;
				else if (percentage.getFraction().intValue()>0 && value.intValue()>0 || percentage.getFraction().intValue()<0 && value.intValue()<0)
					return CHARACTERIZATION_BONUS_TENDENCY;
			} else
				throw new NotEnoughDataException();
		}
		return 0;
	}

	public Integer highlighting(StatementAnnotation annotation, FacesMessages facesMessages) {
		int score = 0;
		Percentage percentage = getHighlightingPercentage(annotation);
		if (percentage != null && percentage.getTotal() >= MIN_NR_FOR_STATISTICS) {
			if (percentage.getPercentage() >= 50) {
				score = ANNOTATION_LIKE_MAJORITY;
			} else if (percentage.getPercentage() >= 30) {
				score = ANNOTATION_LIKE_THIRD;
			}
		}
		annotation.setScore(score);
		printPercentage(facesMessages, percentage, "highlighting");
		return score;
	}
	
	private void printPercentage(FacesMessages facesMessages, Percentage percentage, String type) {
		if (facesMessages == null)
			return;
		if (percentage != null && percentage.getTotal() >= MIN_NR_FOR_STATISTICS) {
			if (percentage.getPercentage() >= 30)
				facesMessages.addFromResourceBundle("game.recognize."+type+".high", percentage.getPercentage().intValue());
			else
				facesMessages.addFromResourceBundle("game.recognize."+type+".low", percentage.getPercentage().intValue());
		} else
			facesMessages.addFromResourceBundle("game.recognize."+type+".notEnoughData");
	}
	
	public int getPersonScoreSum(Person person) {
		// TODO Put into HQL
		if (person == null)
			return 0;
		try {
			person = entityManager.find(Person.class, person.getId());

			List<Bet> bets = (List<Bet>)entityManager.createNamedQuery("bet.byPersonWithoutScore").setParameter("person", person).getResultList();
			for (Bet bet: bets) {
				updateScoreForBet(bet);
			}

			// Score over all actions is too slow because of the many subtables, therefore search for each table individually
			Number locationAssignmentScore = (Number) entityManager.createNamedQuery("locationAssignment.scoreSumByPerson").setParameter("person", person).getSingleResult();
			Number statementCharacterizationScore = (Number) entityManager.createNamedQuery("statementCharacterization.scoreSumByPerson").setParameter("person", person).getSingleResult();
			Number statementAnnotationScore = (Number) entityManager.createNamedQuery("statementAnnotation.scoreSumByPerson").setParameter("person", person).getSingleResult();
			int actionScore = numberToInt(locationAssignmentScore)+numberToInt(statementCharacterizationScore)+numberToInt(statementAnnotationScore);
			
			Number statementsCreatedCount = (Number) entityManager.createNamedQuery("statement.countByCreator").setParameter("person", person).getSingleResult();
			int statementsCreatedScore = numberToInt(statementsCreatedCount) * STATEMENT_CREATED;
			int score = actionScore + statementsCreatedScore;
//			log.info("Person's #4 score = #0 (#1-#2+#3)", score, actionScore, statementsMinusScore, statementsPlusScore, person);
			return score;
		} catch (Exception e) {
			log.warn("Could not calculate person score sum");
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
	
	public Percentage getCharacterizationResult(Statement statement, String type) {
		if (type == null || 
				!(type.equals("gender") || type.equals("maturity") || type.equals("cultivation"))) {
			log.warn("Could not calculate "+type+" percentage because of wrong type '"+type+"'");
			return null;
		}

		try {
			Percentage percentage = (Percentage) entityManager.createNamedQuery("statementCharacterization."+type+"PercentageByStatement").setParameter("statement", statement).getSingleResult();
			log.info("Characterization #3 for #0: #4 (sum #1, count: #2)", statement, percentage.getSum(), percentage.getTotal(), type, percentage.getFraction());
			return percentage;
		} catch (Exception e) {
			log.warn("Could not calculate "+type+" percentage", e);
			return null;
		}
	}
	
	public int getCharacterizationAsPercentage(Statement statement, String type){
		Percentage result = getCharacterizationResult(statement, type);
		if (result != null && result.getFraction() != null) 
			return (result.getFraction().intValue()+100)/2;
		return 0;
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
	
	private boolean notNull(Number n) {
		return n != null && n.intValue() != 0;
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
