package server;

import org.eclipse.jetty.websocket.api.Session;

import javax.imageio.IIOException;
import java.io.IOException;

public class ChessSession {
    public String username;
    public Session session;
    public Integer gameID;

    public ChessSession(String username, Session session, Integer gameID) {
        this.username = username;
        this.session = session;
        this.gameID = gameID;
    }

    public void send(String message) throws IOException {
        session.getRemote().sendString(message);
    }
}
