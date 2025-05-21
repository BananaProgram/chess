package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import org.junit.jupiter.api.*;

public class UnitTests {

    private MemoryDataAccess dataAccess = new MemoryDataAccess();
    private UserService userService = new UserService(dataAccess);

    @Test
    @DisplayName("Register - SUCCESS")
    public void registerSuccess() {
        RegisterResult result = userService.register(new RegisterRequest("test", "testpassword", "test@test.com"));
        Assertions.assertEquals("test", result.username(), "Response did not have the correct username");
        Assertions.assertNotNull(result.authToken(), "Response did not contain an authentication token");
    }
}
