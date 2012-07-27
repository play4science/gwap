/* DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.action;

import gwap.model.Person;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

@Name("adminBean")
@Scope(ScopeType.PAGE)
public class AdminBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Logger                  private Log log;
	@In(required=false)      private Person person;
	@In                      private EntityManager entityManager;
	
	private Boolean isAdmin;  
	
	@Create
	public void updateIsAdmin() {
		log.info("isAdmin Check");
		if ( person != null ) {
			try {
				Query query = entityManager.createNamedQuery("person.hasRole");
				query.setParameter("personId", person.getId());
				query.setParameter("role", "admin");
				Person personInRole= (Person) query.getSingleResult();
				isAdmin = personInRole != null;
			} catch (NoResultException ex) {
				isAdmin = false;
			}
		} else {
		  isAdmin = false;
		}
	}

	public Boolean getIsAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(Boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
}
