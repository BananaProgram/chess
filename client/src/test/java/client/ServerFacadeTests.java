package client;

import org.junit.jupiter.api.*;
import reqres.JoinRequest;
import reqres.LoginRequest;
import reqres.NewGameRequest;
import reqres.RegisterRequest;
import server.Server;
import server.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clear() {
        facade.clear();
    }


    @Test
    @DisplayName("Register - SUCCESS")
    public void registerSuccess() {
        var result = facade.register(new RegisterRequest("test", "test", "test@test.test"));
        Assertions.assertEquals("test", result.username());
    }

    @Test
    @DisplayName("Register - FAIL")
    public void registerFail() {
        var result = facade.register(new RegisterRequest(null, "test", "test@test.test"));
        Assertions.assertTrue(result.message().contains("400"));
    }

    @Test
    @DisplayName("Login - SUCCESS")
    public void loginSuccess() {
        var register = facade.register(new RegisterRequest("test", "test", "test@test.test"));
        facade.logout(register.authToken());
        var result = facade.login(new LoginRequest("test", "test"));
        Assertions.assertEquals("test", result.username());
    }

    @Test
    @DisplayName("Login - FAIL")
    public void loginFail() {
        var register = facade.register(new RegisterRequest("test", "test", "test@test.test"));
        facade.logout(register.authToken());
        var result = facade.login(new LoginRequest("test", "t"));
        Assertions.assertTrue(result.message().contains("401"));
    }

    @Test
    @DisplayName("Logout - SUCCESS")
    public void logoutSuccess() {
        var register = facade.register(new RegisterRequest("test", "test", "test@test.test"));
        facade.logout(register.authToken());
        var result = facade.create(new NewGameRequest("testgame"), register.authToken());
        Assertions.assertTrue(result.message().contains("401"));
    }

    @Test
    @DisplayName("Logout - FAIL")
    public void logoutFail() {
        facade.register(new RegisterRequest("test", "test", "test@test.test"));
        var result = facade.logout(null);
        System.out.println(result.message());
        Assertions.assertTrue(result.message().contains("401"));
    }

    @Test
    @DisplayName("List Games - SUCCESS")
    public void listSuccess() {
        var register = facade.register(new RegisterRequest("test", "test", "test@test.test"));
        facade.create(new NewGameRequest("testgame"), register.authToken());
        var result = facade.list(register.authToken());
        Assertions.assertEquals("testgame", result.games().getFirst().gameName());
    }

    @Test
    @DisplayName("List Games - FAIL")
    public void listFail() {
        var result = facade.list(null);
        Assertions.assertTrue(result.message().contains("401"));
    }

    @Test
    @DisplayName("Create Game - SUCCESS")
    public void createSuccess() {
        var register = facade.register(new RegisterRequest("test", "test", "test@test.test"));
        facade.create(new NewGameRequest("testgame"), register.authToken());
        var response = facade.list(register.authToken());
        Assertions.assertEquals("testgame", response.games().getFirst().gameName());
    }

    @Test
    @DisplayName("Create Game - FAIL")
    public void createFail() {
        var result = facade.create(new NewGameRequest("testgame"), null);
        Assertions.assertTrue(result.message().contains("401"));
    }

    @Test
    @DisplayName("Play Game - SUCCESS")
    public void playSuccess() {
        var register = facade.register(new RegisterRequest("test", "test", "test@test.test"));
        var game = facade.create(new NewGameRequest("testgame"), register.authToken());
        facade.join(new JoinRequest("WHITE", game.gameID()), register.authToken());
        var result = facade.list(register.authToken());
        Assertions.assertEquals("test", result.games().getFirst().whiteUsername());
    }

    @Test
    @DisplayName("Play Game - FAIL")
    public void playFail() {
        var register = facade.register(new RegisterRequest("test", "test", "test@test.test"));
        facade.create(new NewGameRequest("testgame"), register.authToken());
        var result = facade.join(new JoinRequest("WHITE", 5), register.authToken());
        Assertions.assertTrue(result.message().contains("400"));
    }

    @Test
    @DisplayName("Clear - SUCCESS")
    public void clearSuccess() {
        var register = facade.register(new RegisterRequest("test", "test", "test@test.test"));
        facade.create(new NewGameRequest("testgame"), register.authToken());
        facade.clear();
        var registerAfter = facade.register(new RegisterRequest("test", "test", "test@test.test"));
        var result = facade.list(registerAfter.authToken());
        Assertions.assertTrue(result.games().isEmpty());
    }
}