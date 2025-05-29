package server;

import dataaccess.DataAccessException;
import dataaccess.SQLDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import service.*;

import java.sql.SQLException;
import java.util.List;

import static dataaccess.DatabaseManager.getConnection;

public class UnitTests {
    private SQLDataAccess dataAccess = new SQLDataAccess();

    @BeforeEach
    public void clearTables() {
        dataAccess.clear();
    }

    @Test
    @Order(1)
    @DisplayName("Clear - SUCCESS")
    public void clearSuccess() {
        dataAccess.clear();
        Assertions.assertTrue(dataAccess.listGames().isEmpty());
    }

    @Test
    @Order(2)
    @DisplayName("Register - SUCCESS")
    public void registerSuccess() {
        dataAccess.addUser(new UserData("test", "testpass", "test@test.com"));
        Assertions.assertEquals("test@test.com", dataAccess.getUser("test").email(), "Response did not have the correct username");
    }

    @Test
    @Order(3)
    @DisplayName("Register - FAIL")
    public void registerFail() {
        Assertions.assertThrows(RuntimeException.class, () -> {dataAccess.addUser(new UserData(null, null, "test@test.com"));});
    }

    @Test
    @DisplayName("Login - SUCCESS")
    public void loginSuccess() {
        dataAccess.addUser(new UserData("test", "testpass", "test@test.com"));

        AuthData authData = dataAccess.createAuth("test");
        Assertions.assertEquals("test", dataAccess.findUser(authData.authToken()), "Response did not have the correct username");
    }

    @Test
    @DisplayName("Login - FAIL")
    public void loginFail() {
        Assertions.assertThrows(RuntimeException.class, () -> {dataAccess.createAuth(null);});
    }

    @Test
    @DisplayName("Logout - SUCCESS")
    public void logoutSuccess() {
        dataAccess.addUser(new UserData("test", "testpass", "test@test.com"));
        AuthData authData = dataAccess.createAuth("test");

        dataAccess.deleteAuth(authData.authToken());
        Assertions.assertNull(dataAccess.findUser(authData.authToken()), "Authtoken still exists");
    }

    @Test
    @DisplayName("Logout - FAIL")
    public void logoutFail() {
        Assertions.assertThrows(RuntimeException.class, () -> {dataAccess.deleteAuth("blahblah");});
    }

    @Test
    @DisplayName("List Games - SUCCESS")
    public void listGamesSuccess() {
        dataAccess.addGame(new NewGameRequest("newGame"));
        List<GameData> result = dataAccess.listGames();
        Assertions.assertEquals("newGame", result.getFirst().gameName(), "Wrong game name");
    }

    @Test
    @DisplayName("List Games - FAIL")
    public void listGamesFail() {
        try (var conn = getConnection()) {
            try (var stmt = conn.prepareStatement("INSERT INTO games (gamename, game) VALUES (?, ?)")) {
                stmt.setString(1, "newGame");
                stmt.setString(2, "{invalidJson}");
                stmt.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertThrows(RuntimeException.class, () -> {dataAccess.listGames();});
    }

    @Test
    @DisplayName("Create Game - SUCCESS")
    public void createGameSuccess() {
        int id = dataAccess.addGame(new NewGameRequest("newGame"));
        Assertions.assertEquals("newGame", dataAccess.findGame(id).gameName(), "Wrong game name");
    }

    @Test
    @DisplayName("Create Game - FAIL")
    public void createGameFail() {
        Assertions.assertThrows(RuntimeException.class, () -> {dataAccess.addGame(new NewGameRequest(null));});
    }

    @Test
    @DisplayName("Join Game - SUCCESS")
    public void joinGameSuccess() {
        dataAccess.addUser(new UserData("test", "testpass", "test@test.com"));
        int id = dataAccess.addGame(new NewGameRequest("newGame"));
        dataAccess.joinGame(id, "test", "WHITE");
        Assertions.assertEquals("test", dataAccess.findGame(id).whiteUsername(), "Wrong username");
    }

    @Test
    @DisplayName("Join Game - FAIL")
    public void joinGameFail() {
        dataAccess.addUser(new UserData("test", "testpass", "test@test.com"));
        int id = dataAccess.addGame(new NewGameRequest("newGame"));
        Assertions.assertThrows(RuntimeException.class, () -> {dataAccess.joinGame(null, null, "WHITE");});
    }
}