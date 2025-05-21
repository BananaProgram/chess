package service;

import dataaccess.MemoryDataAccess;
import model.GameData;

import java.util.HashMap;
import java.util.List;

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
        int gameID = dataAccess.addGame(request);
        return new NewGameResult(gameID);
    }

    public void joinGame(String authToken, JoinRequest request) {
        String username = dataAccess.findUser(authToken);
        dataAccess.joinGame(request.gameID(), username, request.playerColor());
    }
}
