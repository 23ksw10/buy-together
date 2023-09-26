package com.together.buytogether.member.domain;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {
    private final Map<String, HttpSession> sessionStore = new ConcurrentHashMap<>();

    public void createSession(String sessionId, HttpSession session) {
        sessionStore.put(sessionId, session);
    }

    public List<HttpSession> getAllSessions() {
        List<HttpSession> sessions = new ArrayList<>();
        sessionStore.forEach((key, value) -> sessions.add(value));
        return sessions;
    }

    public void invalidateSession(String sessionId) {
        sessionStore.remove(sessionId);
    }
}
