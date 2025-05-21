package dataaccess;

import model.AuthData;
import model.UserData;
import java.util.UUID;
import java.util.HashMap;

public class MemoryDataAccess implements DataAccess {
    private HashMap<String, UserData> users = new HashMap<>();
    private HashMap<String, String> authTokens = new HashMap<>();

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
        users.put(token, user);
        authTokens.put(token, user.username());
        return authData;
    }
}
