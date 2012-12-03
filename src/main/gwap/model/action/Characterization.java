package gwap.model.action;

import gwap.model.resource.Resource;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * Used for characterizing statements with a custom Integer value.
 * 
 * @author Fabian Kneißl
 */
@NamedQueries({
	@NamedQuery(name="characterization.scoreSumByPerson",
			query="select sum(a.score) from Characterization a where a.person = :person"),
	@NamedQuery(name="characterization.byResource",
			query="from Characterization where resource = :resource"),
	@NamedQuery(name="characterization.groupedResults",
			query="select name, c.value, count(c.value) from Characterization c where c.resource.id = :resourceId group by name, value")
})
@Entity
@Name("characterization")
@Scope(ScopeType.PAGE)
public class Characterization extends Action {

	public enum Name {
		gender, maturity, cultivation;
	}

	private static final long serialVersionUID = 1L;

	private Integer value;
	
	@ManyToOne
	private Resource resource;
	
	@Enumerated(EnumType.STRING)
	private Name name;
	
	public Characterization() { }
	public Characterization(Name name) {
		this.name = name;
	}
	
	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource statement) {
		this.resource = statement;
	}
	
	public Name getName() {
		return name;
	}

	public void setName(Name name) {
		this.name = name;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}
	
}
