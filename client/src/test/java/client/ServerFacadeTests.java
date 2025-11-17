package client;

import org.junit.jupiter.api.TestInstance;
import server.Server;
import org.junit.jupiter.api.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;

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
}

