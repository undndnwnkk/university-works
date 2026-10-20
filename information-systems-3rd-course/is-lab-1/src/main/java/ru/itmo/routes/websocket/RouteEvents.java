package ru.itmo.routes.websocket;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/ws/routes")
public class RouteEvents {
    private static final Set<Session> SESSIONS = ConcurrentHashMap.newKeySet();

    @OnOpen public void onOpen(Session session) { SESSIONS.add(session); }
    @OnClose public void onClose(Session session) { SESSIONS.remove(session); }

    public static void broadcast(String action, Integer id) {
        String json = "{\"action\":\"" + action + "\",\"id\":" + id + "}";
        SESSIONS.removeIf(session -> {
            if (!session.isOpen()) return true;
            try { session.getBasicRemote().sendText(json); return false; }
            catch (IOException ignored) { return true; }
        });
    }
}
