/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.tools;

import java.util.Date;

/**
 * @author steinmayr
 */
public class Timer {
	
	private Date start;
	public Timer()
	{
		start=new Date();		
	}
	
	public long timePassed()
	{
		return new Date().getTime()-start.getTime();
	}
	
	

}
