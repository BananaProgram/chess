package server;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    public final ConcurrentHashMap<String, ChessSession> sessions = new ConcurrentHashMap<>();

    public void add(String username, Session session, Integer gameID) {
        var connection = new ChessSession(username, session, gameID);
        sessions.put(username, connection);
    }

    public void remove(String username) {
        sessions.remove(username);
    }

    public void broadcast(String sourceUsername, ServerMessage notif, Integer gameID) throws IOException {
        String json = new Gson().toJson(notif);

        var closedSessions = new ArrayList<ChessSession>();
        for (var c : sessions.values()) {
            if (c.session.isOpen() && c.gameID.equals(gameID)) {
                if (!c.username.equals(sourceUsername)) {
                    System.out.printf("Sending message to %s (game %d): %s%n", c.username, c.gameID, json);
                    c.send(json);
                }
            } else {
                closedSessions.add(c);
            }
        }

        for (var c : closedSessions) {
            sessions.remove(c.username);
        }

    }

    public void send(String username, ServerMessage message) throws IOException {
        String json = new Gson().toJson(message);

        var closedSessions = new ArrayList<ChessSession>();
        for (var c : sessions.values()) {
            if (c.session.isOpen()) {
                if (c.username.equals(username)) {
                    System.out.printf("Sending message to %s (game %d): %s%n", c.username, c.gameID, json);
                    c.send(json);
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
