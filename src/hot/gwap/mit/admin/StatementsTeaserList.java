/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.mit.admin;

import gwap.model.StatementsTeaser;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

/**
 * @author Fabian Kneißl
 */
@Name("mitAdminStatementsTeaserList")
public class StatementsTeaserList extends EntityQuery<StatementsTeaserList> {
	
	@In
	private EntityManager entityManager;
	
	public StatementsTeaserList() {
		setEjbql("select s from StatementsTeaser s order by s.publicationDate, s.id");
	}
	
	public void generatePublicationDate(){
		Query query = entityManager.createNamedQuery("statementsTeaser.latestByPublicationDate");
		Date latestPublication = ((StatementsTeaser) query.getResultList().get(0)).getPublicationDate();
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(latestPublication);
		Query query2 = entityManager.createNamedQuery("statementsTeaser.allForDateGeneration");
		List<StatementsTeaser> statementTeasersForGeneration = query2.getResultList();
		for(StatementsTeaser st : statementTeasersForGeneration){
			calendar.add(Calendar.HOUR, 24*7);
			st.setPublicationDate(calendar.getTime());
			entityManager.persist(st);
		}
	}
}
