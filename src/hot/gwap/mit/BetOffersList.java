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

package gwap.mit;

import gwap.model.Person;
import gwap.model.action.Purchase;
import gwap.model.action.Sale;
import gwap.model.resource.Resource;
import gwap.model.resource.Statement;
import gwap.tools.AbstractPaginatedList;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

/**
 * @author Fabian Kneißl
 */
@Name("mitBetOffersList")
@Scope(ScopeType.PAGE)
public class BetOffersList extends AbstractPaginatedList implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Logger
	protected Log log;
	@In
	protected EntityManager entityManager;
	@In
	protected Person person;
	@In
	protected PokerScoring mitPokerScoring;
	@In
	private FacesMessages facesMessages;
	
	private List<Sale> sales;
	
	@Out(required=false)
	private Sale selectedSale;
	
	public List<Sale> getBetOffersList() {
		if (sales == null) {
			updateList();
		}
		
		return sales;
	}
	
	public void purchaseBet() {
		log.info("#0 purchases bet #1 for #2", person, selectedSale.getBet(), selectedSale.getScore());
		selectedSale = entityManager.find(Sale.class, selectedSale.getId());
		Purchase purchase = new Purchase();
		purchase.setBet(selectedSale.getBet());
		purchase.setCreated(new Date());
		purchase.setScore(-selectedSale.getScore());
		purchase.setPerson(person);
		entityManager.persist(purchase);
		
		purchase.setSale(selectedSale);
		selectedSale.setPurchase(purchase);
		
		selectedSale.getBet().setPerson(person);
		
		facesMessages.addFromResourceBundle("bet.trade.purchaseSuccessful", selectedSale.getScore());
		selectedSale = null;
		updateList();
	}
	
	@Override
	public void updateList() {
		Query q = entityManager.createNamedQuery("sale.offersNotPerson");
		q.setParameter("person", person);
		sales = q.getResultList();
		paginationControl.setNumResults(sales.size());
		if (paginationControl.getNumPages() > 1) {
			q.setFirstResult(paginationControl.getFirstResult());
			q.setMaxResults(paginationControl.getResultsPerPage());
			sales = q.getResultList();
		}
		for (Sale sale : sales) {
			Resource resource = sale.getBet().getResource();
			if (resource instanceof Statement)
				((Statement) resource).getStatementTokens().size();
			if (sale.getBet().getCurrentMatch() == null || sale.getBet().getScore() == null)
				mitPokerScoring.updateScoreForBet(sale.getBet());
			// Update price of automated sales
			if (sale.getPerson() == null) {
				sale.setScore(TradeBet.getDirectPurchasePrice(sale.getBet()));
			}
		}
		log.info("Created betOffersList with #0 sales", sales.size());
		if (sales.size() > 0)
			selectedSale = sales.get(resultNumber);
	}
	
	public void showDetail(Long selectedSaleId) {
		List<Sale> aux = getBetOffersList();
		for(int i = 0; i<aux.size(); i++){
			if(selectedSaleId.equals(aux.get(i).getId())){
				setResultNumber(i);
				selectedSale = aux.get(i);
			}
		}
		log.info("Ausgewählte id in showDetail(): " + selectedSale.getId());
	}

	public Sale getSelectedSale() {
		return selectedSale;
	}
	
}
