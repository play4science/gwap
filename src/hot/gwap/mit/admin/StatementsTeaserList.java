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
		Query query = entityManager.createNamedQuery("statementsTeaser.orderedByPublicationDate");
		query.setMaxResults(1);
		Date latestPublication = ((StatementsTeaser) query.getSingleResult()).getPublicationDate();
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
