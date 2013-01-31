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

package gwap.wrapper;

import java.io.Serializable;

/**
 * @author kneissl
 */
public class Percentage implements Serializable {

	private double sum;
	private int total;
	
	public Percentage() { }
	
	public Percentage(Number sum, Number total) {
		if (sum != null)
			this.sum = sum.doubleValue();
		if (total != null)
			this.total = total.intValue();
	}
	
	public double getSum() {
		return sum;
	}
	
	public int getTotal() {
		return total;
	}
	
	public Double getPercentage() {
		if (total > 0)
			return sum*100.0 / total;
		else
			return null;
	}
	
	public Double getFraction() {
		if (total > 0)
			return sum / total;
		else
			return null;
	}
	
	@Override
	public String toString() {
		if (total != 0) 
			return Integer.toString((int)(sum*100/total));
		else
			return "NaN";
	}
}
