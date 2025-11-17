package service;

import dataaccess.InMemoryDataAccess;
import dataaccess.DataAccessException;
import org.junit.jupiter.api.*;
import service.requests.RegisterRequest;
import service.requests.LoginRequest;
import service.results.RegisterResult;
import service.results.LoginResult;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    private InMemoryDataAccess dao;
    private UserService userService;

    @BeforeEach
    public void setup() {
        dao = new InMemoryDataAccess();
        userService = new UserService(dao);
    }

    @Test
    public void registerSuccess() throws Exception {
        var req = new RegisterRequest("alice", "pw", "a@example.com");
        RegisterResult res = userService.register(req);
        assertEquals("alice", res.username());
        assertNotNull(res.authToken());
    }

    @Test
    public void registerAlreadyTaken() throws Exception {
        var req = new RegisterRequest("bob", "pw", "b@example.com");
        userService.register(req);
        assertThrows(DataAccessException.class, () -> userService.register(req));
    }

    @Test
    public void loginSuccessAndLogout() throws Exception {
        var r = new RegisterRequest("carol", "pw", "c@example.com");
        RegisterResult rr = userService.register(r);

        var loginReq = new LoginRequest("carol", "pw");
        LoginResult lr = userService.login(loginReq);
        assertEquals("carol", lr.username());
        assertNotNull(lr.authToken());

        userService.logout(lr.authToken());
        assertThrows(DataAccessException.class, () -> userService.logout(lr.authToken()));
    }

    @Test
    public void loginBadPasswordUnauthorized() throws Exception {
        var r = new RegisterRequest("dan", "pw", "d@example.com");
        userService.register(r);
        var loginReq = new LoginRequest("dan", "wrongpw");
        assertThrows(DataAccessException.class, () -> userService.login(loginReq));
    }
}
