package client;

import org.junit.jupiter.api.*;
import reqres.LoginRequest;
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
        Assertions.assertEquals("Error: bad request", result.message());
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
        Assertions.assertEquals("Error: unauthorized", result.username());
    }
}