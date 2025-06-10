package client;

import com.google.gson.Gson;

import javax.management.Notification;
import javax.websocket.*;
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

    public void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg);
    }

    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}