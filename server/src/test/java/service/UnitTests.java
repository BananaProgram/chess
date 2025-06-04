package service;

import dataaccess.SQLDataAccess;
import model.GameData;
import org.junit.jupiter.api.*;
import reqres.*;

public class UnitTests {

    private SQLDataAccess dataAccess = new SQLDataAccess();
    private UserService userService = new UserService(dataAccess);
    private GameService gameService = new GameService(dataAccess);

    @Test
    @DisplayName("Clear - SUCCESS")
    public void clearSuccess() {
        userService.clear();
        Assertions.assertTrue(dataAccess.listGames().isEmpty());
    }

    @Test
    @DisplayName("Register - SUCCESS")
    public void registerSuccess() {
        RegisterResult result = userService.register(new RegisterRequest("test", "testpassword", "test@test.com"));
        Assertions.assertEquals("test", result.username(), "Response did not have the correct username");
        Assertions.assertNotNull(result.authToken(), "Response did not contain an authentication token");
    }

    @Test
    @DisplayName("Register - FAIL")
    public void registerFail() {
        RegisterResult result = userService.register(new RegisterRequest("failtest", null, ""));
        Assertions.assertEquals("Error: bad request", result.message(), "Response did not have the correct error");
    }

    @Test
    @DisplayName("Login - SUCCESS")
    public void loginSuccess() {
        userService.register(new RegisterRequest("test", "testpassword", "test@test.com"));
        LoginResult result = userService.login(new LoginRequest("test", "testpassword"));
        Assertions.assertEquals("test", result.username(), "Response did not have the correct username");
        Assertions.assertNotNull(result.authToken(), "Response did not contain an authentication token");
    }

    @Test
    @DisplayName("Login - FAIL")
    public void loginFail() {
        userService.register(new RegisterRequest("test", "testpassword", "test@test.com"));
        LoginResult result = userService.login(new LoginRequest("failtest", null));
        Assertions.assertEquals("Error: bad request", result.message(), "Response did not have the correct error");
    }

    @Test
    @DisplayName("Logout - SUCCESS")
    public void logoutSuccess() {
        RegisterResult registered = userService.register(new RegisterRequest("test", "testpassword", "test@test.com"));
        ErrorMessage result = userService.logout(registered.authToken());
        Assertions.assertNull(dataAccess.findUser(registered.authToken()), "Authtoken still exists");
    }

    @Test
    @DisplayName("Logout - FAIL")
    public void logoutFail() {
        RegisterResult registered = userService.register(new RegisterRequest("test", "testpassword", "test@test.com"));
        ErrorMessage result = userService.logout(null);
        Assertions.assertEquals("Error: unauthorized", result.message(), "Wrong error message");
    }

    @Test
    @DisplayName("List Games - SUCCESS")
    public void listGamesSuccess() {
        RegisterResult registered = userService.register(new RegisterRequest("test", "testpassword", "test@test.com"));
        gameService.createGame(registered.authToken(), new NewGameRequest("newGame"));
        ListGamesResult result = gameService.listGames(registered.authToken());
        Assertions.assertEquals("newGame", result.games().getFirst().gameName(), "Wrong game name");
    }

    @Test
    @DisplayName("List Games - FAIL")
    public void listGamesFail() {
        RegisterResult registered = userService.register(new RegisterRequest("test", "testpassword", "test@test.com"));
        gameService.createGame(registered.authToken(), new NewGameRequest("newGame"));
        ListGamesResult result = gameService.listGames(null);
        Assertions.assertEquals("Error: unauthorized", result.message(), "Wrong error message");
    }

    @Test
    @DisplayName("Create Game - SUCCESS")
    public void createGameSuccess() {
        RegisterResult registered = userService.register(new RegisterRequest("test", "testpassword", "test@test.com"));
        NewGameResult result = gameService.createGame(registered.authToken(), new NewGameRequest("newGame"));
        GameData game = dataAccess.findGame(result.gameID());
        Assertions.assertEquals("newGame", game.gameName(), "Wrong game name");
    }

    @Test
    @DisplayName("Create Game - FAIL")
    public void createGameFail() {
        RegisterResult registered = userService.register(new RegisterRequest("test", "testpassword", "test@test.com"));
        NewGameResult result = gameService.createGame(registered.authToken(), new NewGameRequest(null));
        Assertions.assertEquals("Error: bad request", result.message(), "Wrong error message");
    }

    @Test
    @DisplayName("Join Game - SUCCESS")
    public void joinGameSuccess() {
        RegisterResult registered = userService.register(new RegisterRequest("test", "testpassword", "test@test.com"));
        NewGameResult result = gameService.createGame(registered.authToken(), new NewGameRequest("newGame"));
        ErrorMessage joinResult = gameService.joinGame(registered.authToken(), new JoinRequest("WHITE", result.gameID()));
        Assertions.assertEquals("test", dataAccess.findGame(result.gameID()).whiteUsername(), "Wrong username");
        Assertions.assertNull(joinResult.message());
    }

    @Test
    @DisplayName("Join Game - FAIL")
    public void joinGameFail() {
        RegisterResult registered = userService.register(new RegisterRequest("test", "testpassword", "test@test.com"));
        NewGameResult result = gameService.createGame(registered.authToken(), new NewGameRequest("newGame"));
        gameService.joinGame(registered.authToken(), new JoinRequest("WHITE", result.gameID()));
        ErrorMessage joinResult = gameService.joinGame(registered.authToken(), new JoinRequest("WHITE", result.gameID()));
        Assertions.assertEquals("Error: already taken", joinResult.message(), "Wrong error message");
    }
}
