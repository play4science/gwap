/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
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
