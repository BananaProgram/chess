package client;

import com.google.gson.Gson;

import javax.management.Notification;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.Scanner;

public class WSClient extends Endpoint {

    public Session session;
    public NotificationHandler notificationHandler;

    public WSClient(NotificationHandler notificationHandler) throws Exception {
        URI uri = new URI("ws://localhost:8080/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);
        this.notificationHandler = notificationHandler;

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                Notification notif = new Gson().fromJson(message, Notification.class);
                notificationHandler.notify(notif);
            }
        });
    }

    public void joinGame(String username, String color) throws Exception {
        this.session.getBasicRemote().sendText(username + " has joined the game playing " + color);
    }

    public void observe(String username) throws Exception {
        this.session.getBasicRemote().sendText(username + " is observing the game");
    }

    // public void makeMove() {}

    public void leaveGame(String username) throws IOException {
        this.session.getBasicRemote().sendText(username + " has left the game");
        this.session.close();
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}
}