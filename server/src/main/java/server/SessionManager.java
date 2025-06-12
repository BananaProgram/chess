package server;

import org.eclipse.jetty.websocket.api.Session;

import javax.management.Notification;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    public final ConcurrentHashMap<String, ChessSession> sessions = new ConcurrentHashMap<>();

    public void add(String username, Session session) {
        var connection = new ChessSession(username, session);
        sessions.put(username, connection);
    }

    public void remove(String username) {
        sessions.remove(username);
    }

    public void broadcast(String sourceUsername, Notification notif) throws IOException {
        var closedSessions = new ArrayList<ChessSession>();
        for (var c : sessions.values()) {
            if (c.session.isOpen()) {
                if (!c.username.equals(sourceUsername)) {
                    c.send(notif.toString());
                }
            } else {
                closedSessions.add(c);
            }
        }

        for (var c : closedSessions) {
            sessions.remove(c.username);
        }

    }
}
