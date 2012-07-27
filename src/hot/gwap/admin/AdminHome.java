package gwap.admin;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

@Name("adminHome")
@Scope(ScopeType.PAGE)
public class AdminHome implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Logger                  private Log log;
	@In                      private EntityManager entityManager;
	
	public List getTeaserCount() {
		Query q = entityManager.createNamedQuery("artResourceTeaser.count");
		List results = q.getResultList();
		return results;
	}
}
