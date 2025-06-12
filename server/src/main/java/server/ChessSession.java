package server;

import org.eclipse.jetty.websocket.api.Session;

import javax.imageio.IIOException;
import java.io.IOException;

public class ChessSession {
    public String username;
    public Session session;

    public ChessSession(String username, Session session) {
        this.username = username;
        this.session = session;
    }

    public void send(String message) throws IOException {
        session.getRemote().sendString(message);
    }
}
