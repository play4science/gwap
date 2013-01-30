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

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import javax.servlet.http.HttpSession;

public class SessionTracker {
    private static SessionTracker ourInstance = new SessionTracker();
    private WeakHashMap<String, HttpSession> sessions = new WeakHashMap<String, HttpSession>();

    public static SessionTracker instance() {
         return ourInstance;
    }

    private SessionTracker() {
    }

    public List<HttpSession> getSessions() {
         return new ArrayList<HttpSession>(sessions.values());
    }

    public void add(HttpSession session){
         sessions.put(session.getId(),session);
    }
    public void remove(HttpSession session){
         sessions.remove(session.getId());
    }
    public HttpSession getSession(String id){
         return sessions.get(id); 
    }
}
