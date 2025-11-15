package client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.GameData;
import service.requests.*;
import service.results.*;
import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerFacade {

    private final String serverUrl;
    private String authToken;   // stored after login/register
    private final Gson GSON = new Gson();

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public RegisterResult register(RegisterRequest request) throws Exception {
        RegisterResult result =
                makeRequest("/user", "POST", request, RegisterResult.class, null);
        if (result != null && result.authToken() != null) {
            this.authToken = result.authToken();
        }
        return result;
    }

    public LoginResult login(LoginRequest request) throws Exception {
        LoginResult result =
                makeRequest("/session", "POST", request, LoginResult.class, null);
        if (result != null && result.authToken() != null) {
            this.authToken = result.authToken();
        }
        return result;
    }

    public void logout() throws Exception {
        makeRequest("/session", "DELETE", null, null, authToken);
        this.authToken = null;
    }

    public CreateGameResult createGame(CreateGameRequest request) throws Exception {
        return makeRequest("/game", "POST", request, CreateGameResult.class, authToken);
    }

    public GameData[] listGames() throws Exception {
        Type listType = TypeToken.getArray(GameData.class).getType();
        return makeRequest("/game", "GET", null, listType, authToken);
    }

    public void joinGame(JoinGameRequest request) throws Exception {
        makeRequest("/game", "PUT", request, null, authToken);
    }

    private <T> T makeRequest(String path,
                              String method,
                              Object requestObj,
                              Object responseType,
                              String token) throws Exception {
        URL url = new URL(serverUrl + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-Type", "application/json");
        if (token != null) {
            connection.setRequestProperty("Authorization", token);
        }
        if (requestObj != null) {
            connection.setDoOutput(true);
            try (OutputStream os = connection.getOutputStream()) {
                os.write(GSON.toJson(requestObj).getBytes());
            }
        }
        int status = connection.getResponseCode();
        InputStream stream = (status >= 200 && status < 300)
                ? connection.getInputStream()
                : connection.getErrorStream();
        if (status >= 400) {
            throw new Exception("Server returned error " + status);
        }
        if (responseType == null) {
            return null;
        }
        try (InputStreamReader reader = new InputStreamReader(stream)) {
            if (responseType instanceof Class<?>) {
                return GSON.fromJson(reader, (Class<T>) responseType);
            } else {
                return GSON.fromJson(reader, (Type) responseType);
            }
        }
    }
}
