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
}
