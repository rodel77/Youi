package xyz.rodeldev.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

public class SessionManager {
    private Map<UUID, Session> sessions = new HashMap<>();

    public Session pushSession(Player player){
        Session session = new Session(player);
        sessions.put(player.getUniqueId(), session);
        return session;
    }

    public Session getSession(UUID uuid){
        return sessions.get(uuid);
    }

    public boolean destroySession(UUID uuid){
        return sessions.remove(uuid)!=null;
    }
}