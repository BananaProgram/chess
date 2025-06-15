package client;

import chess.ChessMove;
import com.google.gson.Gson;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

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
                System.out.println("Received message");
                System.out.println(message);
                ServerMessage notif = new Gson().fromJson(message, ServerMessage.class);
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

    public void makeMove(String authToken, int gameID, ChessMove move) {
        UserGameCommand command = new UserGameCommand(
                UserGameCommand.CommandType.MAKE_MOVE,
                authToken,
                gameID,
                move
        );

        String json = new Gson().toJson(command);
        try {
            this.session.getBasicRemote().sendText(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void leaveGame(String username) throws IOException {
        this.session.getBasicRemote().sendText(username + " has left the game");
        this.session.close();
    }

    public void resign(String authtoken, int gameID) {
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authtoken, gameID);
            String json = new Gson().toJson(command);
            this.session.getBasicRemote().sendText(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("WebSocket closed: " + closeReason.getReasonPhrase());
    }

}