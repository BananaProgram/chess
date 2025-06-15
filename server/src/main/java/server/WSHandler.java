package server;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import dataaccess.SQLDataAccess;

@WebSocket
public class WSHandler {

    private final SessionManager sessions = new SessionManager();
    private final SQLDataAccess db = new SQLDataAccess();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        var username = db.findUser(command.getAuthToken());
        if (username == null) {
            var error = ServerMessage.error("Invalid or expired auth token.");
            session.getRemote().sendString(new Gson().toJson(error));
            return;
        }

        switch (command.getCommandType()) {
            case CONNECT -> connect(username, command.getGameID(), session);
            case LEAVE -> leave(username, session, command.getGameID());
            case RESIGN -> forfeit(username, session, command.getGameID());
            case MAKE_MOVE -> makeMove(username, command.getMove(), command.getGameID());
        }
    }

    private void connect(String username, Integer gameID, Session session) throws IOException {
        GameData game = db.findGame(gameID);
        if (game == null) {
            var error = ServerMessage.error("Game with ID " + gameID + " does not exist.");
            session.getRemote().sendString(new Gson().toJson(error));
            return;
        }

        String color = null;
        if (game.blackUsername().equals(username)) {
            color = "black";
        } else if (game.whiteUsername().equals(username)) {
            color = "white";
        }
        sessions.add(username, session, gameID);
        String message;
        if (color == null) {
            message = String.format("%s has joined the game as an observer", username);
        } else {
            message = String.format("%s has joined the game playing %s", username, color);
        }
        var notification = ServerMessage.notification(message);
        var board = ServerMessage.loadGame(game);
        //sessions.send(username, board);
        sessions.broadcast(username, notification, gameID);
        sessions.send(username, board);
    }

    private void leave(String username, Session session, Integer gameID) throws IOException {
        GameData game = db.findGame(gameID);

        if (username.equals(game.whiteUsername())) {
            db.removePlayer(gameID, "white");
        }
        if (username.equals(game.blackUsername())) {
            db.removePlayer(gameID, "black");
        }

        sessions.remove(username);
        String message = String.format("%s has left the game", username);
        var notification = ServerMessage.notification(message);
        sessions.broadcast(username, notification, gameID);
    }

    private void makeMove(String username, ChessMove move, Integer gameID) throws IOException {
        GameData game = db.findGame(gameID);

        boolean isWhite = username.equals(game.whiteUsername());
        boolean isBlack = username.equals(game.blackUsername());
        if (!isWhite && !isBlack) {
            var error = ServerMessage.error("Error: Only players can make moves");
            sessions.send(username, error);
            return;
        }

        ChessPiece piece = game.game().getBoard().getPiece(move.getStartPosition());
        if (piece == null) {
            sessions.send(username, ServerMessage.error("Error: No piece at start position"));
            return;
        }

        ChessGame.TeamColor playerColor = isWhite ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
        if (piece.getTeamColor() != playerColor) {
            sessions.send(username, ServerMessage.error("Error: You cannot move your opponent's piece"));
            return;
        }

        if (game.game().isOver()) {
            sessions.send(username, ServerMessage.error("Error: Game is already over"));
            return;
        }

        try {
            game.game().makeMove(move);
            db.saveGame(gameID, game.game());

            String message = String.format("%s: %s", username, move);
            var notification = ServerMessage.notification(message);
            var board = ServerMessage.loadGame(game);

            sessions.broadcast(username, notification, gameID);
            sessions.broadcast(null, board, gameID);
            //sessions.send(username, board);
        } catch (InvalidMoveException e) {
            var error = ServerMessage.error("Invalid move: " + e.getMessage());
            sessions.send(username, error);  // only send to the player who made the move
        }

    }

    private void forfeit(String username, Session session, Integer gameID) throws IOException {
        GameData game = db.findGame(gameID);

        boolean isWhite = username.equals(game.whiteUsername());
        boolean isBlack = username.equals(game.blackUsername());
        if (!isWhite && !isBlack) {
            sessions.send(username, ServerMessage.error("Error: Only players can resign"));
            return;
        }

        if (game.game().isOver()) {
            sessions.send(username, ServerMessage.error("Error: Game already over"));
            return;
        }
        game.game().gameOver();
        db.saveGame(game.gameID(), game.game());

        String message = String.format("%s has forfeited the game", username);
        var notification = ServerMessage.notification(message);
        sessions.send(username, notification);
        sessions.broadcast(username, notification, gameID);
        sessions.remove(username);
    }

}
