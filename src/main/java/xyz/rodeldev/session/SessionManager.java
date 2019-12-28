package xyz.rodeldev.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {
    private Map<UUID, Session> sessions = new HashMap<>();

    public Session pushSession(UUID uuid){
        Session session = new Session();
        sessions.put(uuid, session);
        return session;
    }

    public Session getSession(UUID uuid){
        return sessions.get(uuid);
    }

    public boolean destroySession(UUID uuid){
        return sessions.remove(uuid)!=null;
    }
}