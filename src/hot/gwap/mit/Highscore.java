/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.mit;

import gwap.model.action.Bet;
import gwap.widget.HighscoreBean;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;

/**
 * @author reichstaller
 */

@Name("mitHighscore")
public class Highscore {
	
	@In EntityManager entityManager;
	@ Logger private Log log;
	private List<Bet> bestBets;
	private List<gwap.model.Highscore> bestUsers;

	public List<Bet> getBestBets(){
		Query q = entityManager.createNamedQuery("bet.byScore");
		q.setMaxResults(10);
		List<Bet> result = q.getResultList();
		for(Bet bet : result){
			if (bet.getPerson().getUsername().equals(""))
				bet.getPerson().setUsername("Gast");
		}
			
		return result;
		
	}
	
	public List<gwap.model.Highscore> getBestUsers(){
		Query q = entityManager.createNamedQuery("highscore.mit.byAllPersons");
		q.setMaxResults(10);
		q.setParameter("gametype", "mitRecognize");
		List<gwap.model.Highscore> result  = q.getResultList();
		HighscoreBean.modifyHighscore(result, entityManager);
		for(gwap.model.Highscore h : result){
			if (h.getPerson().getUsername().equals(""))
				h.getPerson().setUsername("Gast");
		}
		
		return result;
	}
	


}
