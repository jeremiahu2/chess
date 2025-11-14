package client;

import com.google.gson.Gson;
import java.net.http.*;

public class ServerFacade {
    private final String baseUrl;
    private final HttpClient client;
    private final Gson GSON = new Gson();
    private String authToken = null;

    public ServerFacade(int port) {
        this.baseUrl = "http://localhost:" + port;
        this.client = HttpClient.newBuilder().build();
    }
}
