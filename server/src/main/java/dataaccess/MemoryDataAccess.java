package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.HashMap;

public class MemoryDataAccess implements DataAccess {
    private HashMap<String, UserData> users = new HashMap<>();
    private HashMap<String, String> authTokens = new HashMap<>();
    private HashMap<Integer, GameData> games = new HashMap<>();

    private int gameID = 0;

    public void clear() {
        users = new HashMap<>();
        authTokens = new HashMap<>();
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public AuthData addUser(UserData user) {
        user = new UserData(user.username(), user.password(), user.password());
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

        for (int i = 0; i < gameID; i++) {
            gameList.add(games.get(i));
        }

        return gameList;
    }
}
