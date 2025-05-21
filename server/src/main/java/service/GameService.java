package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.GameData;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class GameService {
    private final MemoryDataAccess dataAccess;

    public GameService(MemoryDataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public ListGamesResult listGames(String authToken) {
        List<GameData> games = dataAccess.listGames();
        return new ListGamesResult(games);
    }

    public NewGameResult createGame(String authToken, NewGameRequest request) {
        if (dataAccess.findUser(authToken) == null) {
            return new NewGameResult(null,"Error: unauthorized");
        }
        if (request.gameName() == null) {
            return new NewGameResult(null, "Error: bad request");
        }
        int gameID = dataAccess.addGame(request);
        return new NewGameResult(gameID, null);
    }

    public ErrorMessage joinGame(String authToken, JoinRequest request) {
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
        System.out.println(request.playerColor());
        dataAccess.joinGame(request.gameID(), username, request.playerColor());
        return new ErrorMessage(null);
    }
}
