package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import dataaccess.SQLDataAccess;
import model.AuthData;
import model.UserData;

import java.util.Objects;

public class UserService {

    private final SQLDataAccess dataAccess;

    public UserService(SQLDataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void clear() {
        dataAccess.clear();
    }

    public RegisterResult register(RegisterRequest registerRequest) {
        if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null) {
            return new RegisterResult(null, null, "Error: bad request");
        }
        if (dataAccess.getUser(registerRequest.username()) != null) {
            return new RegisterResult(null, null, "Error: already taken");
        }
        UserData user = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        AuthData response = dataAccess.addUser(user);
        return new RegisterResult(response.username(), response.authToken(), null);
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
            AuthData response = dataAccess.createAuth(loginRequest.username());
            return new LoginResult(response.username(), response.authToken(), null);
        } else {
            return new LoginResult(null, null, "Error: unauthorized");
        }
    }

    public ErrorMessage logout(String authToken) {
        if (dataAccess.findUser(authToken) == null) {
            return new ErrorMessage("Error: unauthorized");
        }
        dataAccess.deleteAuth(authToken);
        return new ErrorMessage(null);
    }
}
