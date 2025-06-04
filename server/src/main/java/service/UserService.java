package service;

import dataaccess.SQLDataAccess;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import reqres.*;

public class UserService {

    private final SQLDataAccess dataAccess;

    public UserService(SQLDataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public ErrorMessage clear() {
        try {
            dataAccess.clear();
        } catch (Exception e) {
            return new ErrorMessage("Internal Server Error");
        }
        return new ErrorMessage(null);
    }

    public RegisterResult register(RegisterRequest registerRequest) {
        if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null) {
            return new RegisterResult(null, null, "Error: bad request");
        }
        try {
            if (dataAccess.getUser(registerRequest.username()) != null) {
                return new RegisterResult(null, null, "Error: already taken");
            }
            UserData user = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
            AuthData response = dataAccess.addUser(user);
            return new RegisterResult(response.username(), response.authToken(), null);
        } catch (Exception e) {
            return new RegisterResult(null, null, "Internal Server Error");
        }

    }

    public LoginResult login(LoginRequest loginRequest) {
        try {
            UserData existingUser = dataAccess.getUser(loginRequest.username());
            if (loginRequest.username() == null || loginRequest.password() == null) {
                return new LoginResult(null, null, "Error: bad request");
            }
            if (existingUser == null) {
                return new LoginResult(null, null, "Error: unauthorized");
            }
            if (BCrypt.checkpw(loginRequest.password(), existingUser.password())) {
                AuthData response = dataAccess.createAuth(loginRequest.username());
                return new LoginResult(response.username(), response.authToken(), null);
            } else {
                return new LoginResult(null, null, "Error: unauthorized");
            }
        } catch (Exception e) {
            return new LoginResult(null, null, "Internal Server Error");
        }

    }

    public ErrorMessage logout(String authToken) {
        try {
            if (dataAccess.findUser(authToken) == null) {
                return new ErrorMessage("Error: unauthorized");
            }
            dataAccess.deleteAuth(authToken);
            return new ErrorMessage(null);
        } catch (Exception e) {
            return new ErrorMessage("Internal Server Error");
        }

    }
}
