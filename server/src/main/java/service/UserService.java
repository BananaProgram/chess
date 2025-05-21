package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;

import java.util.Objects;

public class UserService {

    private final MemoryDataAccess dataAccess;

    public UserService(MemoryDataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void clear() {
        dataAccess.clear();
    }

    public RegisterResult register(RegisterRequest registerRequest) {
        UserData user = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        AuthData response = dataAccess.addUser(user);
        return new RegisterResult(response.username(), response.authToken());
    }

    public LoginResult login(LoginRequest loginRequest) {
        UserData existingUser = dataAccess.getUser(loginRequest.username());
        if (loginRequest.username() == null || loginRequest.password() == null) {
            return new LoginResult(null, null, "Error: bad request");
        }
        if (existingUser == null) {
            return new LoginResult(null, null, "Error: unauthorized");
        }
        if (Objects.equals(loginRequest.password(), existingUser.password())) {
            System.out.println(loginRequest);
            AuthData response = dataAccess.createAuth(loginRequest.username());
            return new LoginResult(response.username(), response.authToken(), null);
        } else {
            return new LoginResult(null, null, "Error: unauthorized");
        }
    }

    public void logout(String authToken) {
        dataAccess.deleteAuth(authToken);
    }
}
