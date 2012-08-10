/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.model.action;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Scope;

@NamedQueries({
	@NamedQuery(name="bet.byResource", 
			query="select b from Bet b where b.resource = :resource and b.revisedBet is null"), 
	@NamedQuery(name="bet.byPerson", 
			query="select b from Bet b where (b.person = :person or exists (select id from Person p where p.personConnected = :person and p.id = b.person.id)) and b.revisedBet is null order by b.resource.text,b.created desc"),
	@NamedQuery(name="bet.byPersonWithoutScore", 
			query="select b from Bet b where (b.person = :person or exists (select id from Person p where p.personConnected = :person and p.id = b.person.id)) and b.revisedBet is null " +
					"and b.score is null"), 
	@NamedQuery(name="bet.byResourceAndPerson", 
			query="select b from Bet b where b.resource = :resource and (b.person = :person or exists (select id from Person p where p.personConnected = :person and p.id = b.person.id)) and b.revisedBet is null"),
	@NamedQuery(name="bet.allWithPerson",
			query="from Bet where person is not null"),
	@NamedQuery(name="bet.byScore",
			query="select b from Bet b where b.score != null order by b.score desc")
})

/**
 * A location is assigned to a statement. It can have a certain type if,
 * e.g., it is set by administrators
 * 
 * @author Fabian Kneißl
 */
@Entity
//@Name("bet")
@Scope(ScopeType.EVENT)
public class Bet extends LocationAssignment {
	
	private static final long serialVersionUID = 1L;

	private Integer points;
	
	private Integer currentMatch;
	
	@OneToOne private Bet revisedBet;
	
	public Integer getPoints() {
		return points;
	}
	public void setPoints(Integer points) {
		this.points = points;
	}	
	public Integer getCurrentMatch() {
		return currentMatch;
	}
	public void setCurrentMatch(Integer currentMatch) {
		this.currentMatch = currentMatch;
	}
	public Bet getRevisedBet() {
		return revisedBet;
	}
	public void setRevisedBet(Bet revisedBet) {
		this.revisedBet = revisedBet;
	}
	@Override
	public String toString() {
		return "Bet#"+getId();
	}
	
}
