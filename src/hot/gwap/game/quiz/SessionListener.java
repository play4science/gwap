/*
 * This file is part of gwap
 *
 * Copyright (C) 2013
 * Project play4science
 * Lehr- und Forschungseinheit für Programmier- und Modellierungssprachen
 * Ludwig-Maximilians-Universität München
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
