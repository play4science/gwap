package gwap.model.action;

import gwap.model.resource.Statement;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * Used for annotating statements with a custom text. It can be used to 
 * annotate the whole statement (then, all StatementTokens are referenced) or
 * to only some parts of it (then, these StatementTokens are referenced).
 * 
 * @author Fabian Kneißl
 */
@NamedQueries({
	@NamedQuery(name="statementCharacterization.scoreSumByPerson",
			query="select sum(a.score) from StatementCharacterization a where a.person = :person"),
	@NamedQuery(name="statementCharacterization.genderPercentageByStatement",
			query="select new gwap.wrapper.Percentage( sum(a.gender), count(a.gender) ) " +
					"from StatementCharacterization a where a.statement = :statement and a.gender is not null"),
	@NamedQuery(name="statementCharacterization.maturityPercentageByStatement",
			query="select new gwap.wrapper.Percentage( sum(a.maturity), count(a.maturity) ) " +
					"from StatementCharacterization a where a.statement = :statement and a.maturity is not null"),
	@NamedQuery(name="statementCharacterization.cultivationPercentageByStatement",
			query="select new gwap.wrapper.Percentage( sum(a.cultivation), count(a.cultivation) ) " +
					"from StatementCharacterization a where a.statement = :statement and a.cultivation is not null")
})
@Entity
@Name("statementCharacterization")
@Scope(ScopeType.PAGE)
public class StatementCharacterization extends Action {

	private static final long serialVersionUID = 1L;
	
	@ManyToOne		private Statement statement;
	
	private Integer gender;
	private Integer maturity;
	private Integer cultivation;
	
	public Statement getStatement() {
		return statement;
	}

	public void setStatement(Statement statement) {
		this.statement = statement;
	}

	public Integer getMaturity() {
		return maturity;
	}

	public void setMaturity(Integer maturity) {
		this.maturity = maturity;
	}

	public Integer getCultivation() {
		return cultivation;
	}

	public void setCultivation(Integer cultivation) {
		this.cultivation = cultivation;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}
	
	public boolean isEmpty(){
		if(gender == 0 && maturity == 0 && cultivation == 0)
			return true;
		else 
			return false;
	}
	
}
