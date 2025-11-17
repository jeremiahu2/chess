package client;

import org.junit.jupiter.api.TestInstance;
import server.Server;
import org.junit.jupiter.api.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;
import service.requests.*;
import service.results.*;
import static org.junit.jupiter.api.Assertions.*;
import model.GameData;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ServerFacadeTests {

    private Server server;
    private ServerFacade facade;
    private String baseUrl;

    @BeforeAll
    public void startServer() {
        server = new Server();
        int port = server.run(0);
        baseUrl = "http://localhost:" + port;
        facade = new ServerFacade(baseUrl);
        System.out.println("Started test HTTP server on port " + port);
    }

    @AfterAll
    public void stopServer() {
        if (server != null) server.stop();
    }

    @BeforeEach
    public void clearDatabase() throws Exception {
        if (!tryHttp("/clear", "POST")) {
            tryHttp("/db", "DELETE");
        }
    }

    private boolean tryHttp(String path, String method) throws Exception {
        URL url = new URL(baseUrl + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setDoInput(true);
        if (method.equals("POST") || method.equals("PUT")) {
            conn.setDoOutput(true);
        }
        conn.connect();
        int code = conn.getResponseCode();
        try (InputStream is = (code >= 200 && code < 400) ? conn.getInputStream() : conn.getErrorStream()) {
            if (is != null) {
                byte[] buf = new byte[1024];
                while (is.read(buf) > 0) { /* drain */ }
            }
        } catch (Exception Ignored) {}
        return code >= 200 && code < 300;
    }

    @Test
    public void registerPositive() throws Exception {
        RegisterRequest req = new RegisterRequest("user_reg_pos", "pw", "u_reg_pos@example.cpm");
        RegisterResult r = facade.register(req);
        assertNotNull(r);
        assertEquals("user_reg_pos", r.username());
        assertNotNull(r.authToken());
        assertTrue(r.authToken().length() > 5);
    }

    @Test
    public void registerNegative() throws Exception {
        RegisterRequest req = new RegisterRequest("user_dup", "pw", "dup@example.com");
        facade.register(req);
        assertThrows(Exception.class, () -> facade.register(req));
    }

    @Test
    public void loginPositive() throws Exception {
        facade.register(new RegisterRequest("login_ok", "pw", "login_ok@example.com"));
        LoginResult res = facade.login(new LoginRequest("login_ok", "pw"));
        assertNotNull(res);
        assertEquals("login_ok", res.username());
        assertNotNull(res.authToken());
    }

    @Test
    public void loginNegative() throws Exception {
        facade.register(new RegisterRequest("login_bad", "correct", "login_bad@example.com"));
        assertThrows(Exception.class, () -> facade.login(new LoginRequest("login_bad", "incorrect")));
    }

    @Test
    public void logoutPositive() throws Exception {
        facade.register(new RegisterRequest("logout_ok", "pw", "logout_ok@example.com"));
        assertDoesNotThrow(() -> facade.logout());
        assertThrows(Exception.class, () -> facade.listGames());
    }

    @Test
    public void logoutNegative() {
        assertThrows(Exception.class, () -> facade.logout());
    }

    @Test
    public void createGamePositive() throws Exception {
        facade.register(new RegisterRequest("create_ok", "pw", "create_ok@example.com"));
        CreateGameResult res = facade.createGame(new CreateGameRequest("Test Game"));
        assertNotNull(res);
        assertTrue(res.gameID() > 0);
    }

    @Test
    public void createGameNegative() throws Exception {
        assertThrows(Exception.class, () -> facade.createGame(new CreateGameRequest("NoAuthGame")));
    }

    @Test
    public void listGamesPositive() throws Exception {
        facade.register(new RegisterRequest("list_ok", "pw", "list_ok@example.com"));
        facade.createGame(new CreateGameRequest("ListG1"));
        facade.createGame(new CreateGameRequest("ListG2"));
        GameData[] games = facade.listGames();
        assertNotNull(games);
        assertTrue(games.length >= 2);
    }

    @Test
    public void listGamesNegative() throws Exception {
        assertThrows(Exception.class, () -> facade.listGames());
    }
}

