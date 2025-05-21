package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;

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
}
