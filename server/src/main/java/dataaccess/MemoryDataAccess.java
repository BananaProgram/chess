package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.NewGameRequest;

import java.util.*;

public class MemoryDataAccess implements DataAccess {
    private HashMap<String, UserData> users = new HashMap<>();
    private HashMap<String, String> authTokens = new HashMap<>();
    private HashMap<Integer, GameData> games = new HashMap<>();

    private int gameID = 1;

    public void clear() {
        users = new HashMap<>();
        authTokens = new HashMap<>();
        games = new HashMap<>();
        gameID = 1;
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public AuthData addUser(UserData user) {
        user = new UserData(user.username(), user.password(), user.password());
        users.put(user.username(), user);

        String token = generateToken();
        AuthData authData = new AuthData(token, user.username());
        authTokens.put(token, user.username());
        return authData;
    }

    public UserData getUser(String username) {
        return users.get(username);
    }

    public AuthData createAuth(String username) {
        String token = generateToken();
        AuthData authData = new AuthData(token, username);
        authTokens.put(token, username);
        return authData;
    }

    public void deleteAuth(String authData) {
        authTokens.remove(authData);
    }

    public List<GameData> listGames() {
        List<GameData> gameList = new ArrayList<>();

        for (int i = 1; i < gameID; i++) {
            gameList.add(games.get(i));
        }

        return gameList;
    }

    public int addGame(NewGameRequest request) {
        GameData game = new GameData(gameID, null, null, request.gameName(), new ChessGame());
        gameID++;
        games.put(game.gameID(), game);
        return game.gameID();
    }

    public String findUser(String authToken) {
        return authTokens.get(authToken);
    }

    public void joinGame(int gameID, String username, String color) {
        GameData game = games.get(gameID);
        GameData newGame;

        if (Objects.equals(color, "WHITE")) {
            newGame = new GameData(gameID, username, game.blackUsername(), game.gameName(), game.game());
        } else {
            newGame = new GameData(gameID, game.whiteUsername(), username, game.gameName(), game.game());
        }
        games.remove(gameID);
        games.put(gameID, newGame);
    }

    public GameData findGame(int gameID) {
        return games.get(gameID);
    }
}
