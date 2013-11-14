/*
 * This file is part of gwap, an open platform for games with a purpose
 *
 * Copyright (C) 2013
 * Project play4science
 * Lehr- und Forschungseinheit für Programmier- und Modellierungssprachen
 * Ludwig-Maximilians-Universität München
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package gwap.model.action;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.OneToOne;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Scope;

@NamedQueries({
	
})

/**
 * A purchase can be created by a person or a system. It involves a Bet 
 * and a Sale. If the person is <code>null</code>, it is the system.
 * If purchase is <code>null</code>, it is an open offer, otherwise the 
 * trade has been completed successfully.
 * 
 * @author Fabian Kneißl
 */
@Entity
@Scope(ScopeType.EVENT)
public class Purchase extends Action {

	private static final long serialVersionUID = 1L;

	@ManyToOne private Bet bet;
	
	@OneToOne private Sale sale;
	
	public Bet getBet() {
		return bet;
	}

	public void setBet(Bet bet) {
		this.bet = bet;
	}

	public Sale getSale() {
		return sale;
	}

	public void setSale(Sale sale) {
		this.sale = sale;
	}

	@Override
	public String toString() {
		return "Sale#"+getId();
	}
	
}
