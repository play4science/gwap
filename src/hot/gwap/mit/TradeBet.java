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

import gwap.model.action.Bet;
import gwap.model.action.Purchase;
import gwap.model.action.Sale;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

/**
 * @author Fabian Kneißl
 */
@Name("mitTradeBet")
@Scope(ScopeType.EVENT)
public class TradeBet implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private static final int DIRECT_SALE_PREMIUM = 5;
	private static final int DIRECT_PURCHASE_PREMIUM = 5;

	@Logger					Log log;
	@In						private EntityManager entityManager;
	@In                     private FacesMessages facesMessages;
	
	@In(required = false)
	private Bet selectedBet;
	
	private Integer price;
	
	private Bet cachedBet;
	private Integer saleOfferPrice;
	
	/**
	 * Implementation of an automated market maker. Specifies the price for
	 * which a user can sell a bet.
	 *  
	 * @param bet
	 * @return
	 */
	public static Integer getDirectSalePrice(Bet bet) {
		return Math.max(0, bet.getScore() - DIRECT_SALE_PREMIUM);
	}
	
	/**
	 * Implementation of an automated market maker. Specifies the price for
	 * which a user can purchase a bet.
	 *  
	 * @param bet
	 * @return
	 */
	public static Integer getDirectPurchasePrice(Bet bet) {
		return Math.max(0, bet.getScore() + DIRECT_PURCHASE_PREMIUM);
	}
	
	public void sellDirectly(Bet bet) {
		log.info("#2 sells bet #0 directly for #1", bet, getDirectSalePrice(bet), bet.getPerson());

		bet = entityManager.find(Bet.class, bet.getId());
		
		// Actual trade
		Sale sale = new Sale();
		sale.setBet(bet);
		sale.setCreated(new Date());
		sale.setPerson(bet.getPerson());
		sale.setScore(getDirectSalePrice(bet));
		entityManager.persist(sale);
		
		Purchase purchase = new Purchase();
		purchase.setBet(bet);
		purchase.setCreated(sale.getCreated());
		purchase.setScore(-sale.getScore());
		entityManager.persist(purchase);
		
		purchase.setSale(sale);
		sale.setPurchase(purchase);
		
		bet.setPerson(null);
		
		// Create automatic sale offer
		Sale newSale = new Sale();
		newSale.setBet(bet);
		newSale.setCreated(new Date());
		newSale.setScore(getDirectPurchasePrice(bet));
		entityManager.persist(newSale);
		
		entityManager.flush();
		Events.instance().raiseEvent("mit.betList.update");
		facesMessages.addFromResourceBundle("bet.trade.sellDirectlySuccessful", sale.getScore());
	}
	
	public boolean isOfferedForSale(Bet bet) {
		return getSaleOfferPrice(bet) != null;
	}
	
	public Integer getSaleOfferPrice(Bet bet) {
		if (bet != cachedBet || saleOfferPrice == null) {
			Query query = entityManager.createNamedQuery("sale.offerByBetAndPerson");
			query.setParameter("bet", bet);
			query.setParameter("person", bet.getPerson());
			try {
				Sale sale = (Sale) query.getSingleResult();
				saleOfferPrice = sale.getScore();
			} catch (NoResultException e) {
				saleOfferPrice = null;
			}
			cachedBet = bet;
		}
		return saleOfferPrice;
	}
	
	public void offerForSale(Bet bet) {
		log.info("#2 offers bet #0 for sale for #1", bet, price, bet.getPerson());
		bet = entityManager.find(Bet.class, bet.getId());
		Sale saleOffer = new Sale();
		saleOffer.setBet(bet);
		saleOffer.setCreated(new Date());
		saleOffer.setPerson(bet.getPerson());
		saleOffer.setScore(price);
		entityManager.persist(saleOffer);
	}
	
	public void cancelSaleOffer(Bet bet) {
		Query query = entityManager.createNamedQuery("sale.offerByBetAndPerson");
		query.setParameter("bet", bet);
		query.setParameter("person", bet.getPerson());
		List<Sale> sales = query.getResultList();
		for (Sale sale : sales) {
			log.info("#1 cancels sale offer of #2 for bet #0", bet, bet.getPerson(), sale.getScore());
			entityManager.remove(sale);
		}
	}
	
	public Integer getPrice() {
		if (price == null && selectedBet != null)
			price = selectedBet.getScore();
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}
	
}
	

	
	
