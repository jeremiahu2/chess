package client;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.rmi.ServerException;
import com.google.gson.reflect.TypeToken;
import java.util.Map;

public class ServerFacade {
    private final String baseUrl;
    private final HttpClient client;
    private final Gson GSON = new Gson();
    private String authToken = null;

    public ServerFacade(int port) {
        this.baseUrl = "http://localhost:" + port;
        this.client = HttpClient.newBuilder().build();
    }

    private HttpRequest.Builder jsonRequestBuilder(String path) {
        HttpRequest.Builder b = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Content-Type", "application/json");
        if (authToken != null) {
            b.header("Authorization", "Bearer " + authToken);
        }
        return b;
    }

    private <T> T sendPost(String path, Object body, Class<T> clazz) throws IOException, InterruptedException, ServerException {
        String json = GSON.toJson(body);
        HttpRequest req = jsonRequestBuilder(path)
                .POST(HttpRequest.BodyPublisher.ofString(json, StandardCharsets.UTF_8))
                .build();
        return send(req, clazz);
    }

    private <T> T sendGet(String path, Class<T> clazz) throws IOException, InterruptedException, ServerException {
        HttpRequest req = jsonRequestBuilder(path).GET().build();
        return send(req, clazz);
    }
}
