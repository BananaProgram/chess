package service;

import dataaccess.SQLDataAccess;
import model.GameData;
import reqres.*;

import java.util.List;
import java.util.Objects;

public class GameService {
    private final SQLDataAccess dataAccess;

    public GameService(SQLDataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public ListGamesResult listGames(String authToken) {
        try {
            if (dataAccess.findUser(authToken) == null) {
                return new ListGamesResult(null, "Error: unauthorized");
            }
            List<GameData> games = dataAccess.listGames();
            return new ListGamesResult(games, null);
        } catch (Exception e) {
            return new ListGamesResult(null, "Internal Server Error");
        }
    }

    public NewGameResult createGame(String authToken, NewGameRequest request) {
        try {
            if (dataAccess.findUser(authToken) == null) {
                return new NewGameResult(null,"Error: unauthorized");
            }
            if (request.gameName() == null) {
                return new NewGameResult(null, "Error: bad request");
            }
            int gameID = dataAccess.addGame(request);
            return new NewGameResult(gameID, null);
        } catch (Exception e) {
            return new NewGameResult(null, "Internal Server Error");
        }
    }

    public ErrorMessage joinGame(String authToken, JoinRequest request) {
        try {
            String username = dataAccess.findUser(authToken);
            if (username == null) {
                return new ErrorMessage("Error: unauthorized");
            }
            GameData game = dataAccess.findGame(request.gameID());
            if (game == null || request.playerColor() == null || !(request.playerColor().equals("WHITE") || request.playerColor().equals("BLACK"))) {
                return new ErrorMessage("Error: bad request");
            }
            if ((Objects.equals(request.playerColor(), "WHITE") && game.whiteUsername() != null) ||
                    (Objects.equals(request.playerColor(), "BLACK") && game.blackUsername() != null)) {
                return new ErrorMessage("Error: already taken");
            }
            dataAccess.joinGame(request.gameID(), username, request.playerColor());
            return new ErrorMessage(null);
        } catch (Exception e) {
            return new ErrorMessage("Internal Server Error");
        }

    }
}
