/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.mit;

import gwap.model.Person;
import gwap.model.action.Bet;
import gwap.model.resource.Resource;
import gwap.model.resource.Statement;
import gwap.widget.PaginationControl;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.log.Log;

/**
 * @author Fabian Kneißl
 */
@Name("mitBetList")
@Scope(ScopeType.PAGE)
public class BetList implements Serializable {

	private static final long serialVersionUID = 1L;

	@Logger
	Log log;
	@In
	EntityManager entityManager;
	@In
	Person person;
	@In
	PokerScoring mitPokerScoring;
	@In(create=true) @Out    protected PaginationControl paginationControl;
	
	@Create
	public void create() { log.info("Creating"); }

	@Out(required=false)
	private Bet selectedBet;

	private List<Bet> betList;
	private Integer resultNumber = 0;


	@DataModel
	@SuppressWarnings("unchecked")
	public List<Bet> getBetList() {
		if (betList == null) {
			updateBetList();
		}
		
		return betList;
	}

	@Observer("mit.betList.update")
	public void updateBetList() {
		long start = System.currentTimeMillis();
		Query q = entityManager.createNamedQuery("bet.byPerson")
				.setParameter("person", person);
		betList = q.getResultList();
		paginationControl.setNumResults(betList.size());
		if (paginationControl.getNumPages() > 1) {
			q.setFirstResult(paginationControl.getFirstResult());
			q.setMaxResults(paginationControl.getResultsPerPage());
			betList = q.getResultList();
		}
		for (Bet bet : betList) {
			Resource resource = bet.getResource();
			if (resource instanceof Statement)
				((Statement) resource).getStatementTokens().size();
			if (bet.getCurrentMatch() == null || bet.getScore() == null)
				mitPokerScoring.updateScoreForBet(bet);
		}
		log.info("Created betList with #0 statements in #1ms",
				betList.size(), System.currentTimeMillis() - start);
		if (betList.size() > 0)
			selectedBet = betList.get(resultNumber);
	}

	public void showDetail(Long selectedBetId) {
		List<Bet> aux = getBetList();
		for(int i = 0; i<aux.size(); i++){
			if(selectedBetId.equals(aux.get(i).getId())){
				setResultNumber(i);
				selectedBet = aux.get(i);
			}
		}
		log.info("Ausgewählte id in showDetail(): " + selectedBet.getId());
	}

	public Bet getSelectedBet() {
//		log.info("MYSELECTEDBET: " + selectedBet.getResource().getId());
		return selectedBet;
	}
	
	public List<Bet> retrieveBetsFromStatement(Statement statement){
		Query q = entityManager.createNamedQuery("bet.byResource");
		q.setParameter("resource", statement);
		betList = q.getResultList();
		return betList;
	}
	
	
	public Integer getPageNumber() {
		return paginationControl.getPageNumber();
	}
	public void setPageNumber(Integer pageNumber) {
		if (pageNumber != paginationControl.getPageNumber()) {
			paginationControl.setPageNumber(pageNumber);
			updateBetList();
		}
	}
	
	public Integer getResultNumber() {
		return resultNumber;
	}
	public void setResultNumber(Integer resultNumber) {
		this.resultNumber = resultNumber;
	}

}
