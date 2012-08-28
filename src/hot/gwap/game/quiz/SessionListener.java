/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.game.quiz;

import gwap.game.quiz.SessionTracker;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;



/**
 * Session Listener for Quiz-Session. Only works in clustered environments
 * @author Jonas
 *
 */
public class SessionListener implements HttpSessionListener, java.io.Serializable{

    public void sessionCreated(HttpSessionEvent event) {
         System.out.println("session created: "+event.getSession().getId());
         SessionTracker.instance().add(event.getSession());
    }

    public void sessionDestroyed(HttpSessionEvent event) {
         System.out.println("session destroyed: "+event.getSession().getId());
         SessionTracker.instance().remove(event.getSession());
    }
}