/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
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