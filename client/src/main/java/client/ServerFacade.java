package client;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import service.requests.*;
import service.results.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ServerFacade {

    private final String baseUrl;
    private final Gson GSON = new Gson();

    private String authToken;

    public ServerFacade(int port) {
        this.baseUrl = "http://localhost:" + port;
    }

    public AuthData register(String username, String password, String email) throws Exception {
        var request = new RegisterRequest(username, password, email);
        var response = sendRequest("/user", "POST", request, null, RegisterResponse.class);

        if (response == null || response.authToken() == null) {
            throw new Exception("Registration failed.");
        }

        this.authToken = response.authToken();
        return new AuthData(response.username(), response.authToken());
    }

    public AuthData login(String username, String password) throws Exception {
        var request = new LoginRequest(username, password);
        var response = sendRequest("/session", "POST", request, null, LoginResponse.class);

        if (response == null || response.authToken() == null) {
            throw new Exception("Login failed.");
        }

        this.authToken = response.authToken();
        return new AuthData(response.username(), response.authToken());
    }

    public void logout() throws Exception {
        ensureLoggedIn();
        sendRequest("/session", "DELETE", null, authToken, null);
        authToken = null;
    }

    public int createGame(String gameName) throws Exception {
        ensureLoggedIn();
        var request = new CreateGameRequest(gameName);
        var response = sendRequest("/game", "POST", request, authToken, CreateGameResponse.class);
        return response.gameID();
    }

    public List<GameData> listGames() throws Exception {
        ensureLoggedIn();
        var response = sendRequest("/game", "GET", null, authToken, ListGamesResponse.class);
        return response.games();
    }

    public void joinGame(int gameId, String color) throws Exception {
        ensureLoggedIn();
        var request = new JoinGameRequest(color, gameId);
        sendRequest("/game", "PUT", request, authToken, null);
    }

    public void observeGame(int gameId) throws Exception {
        ensureLoggedIn();
        var request = new JoinGameRequest(null, gameId);  // Observers have no color
        sendRequest("/game", "PUT", request, authToken, null);
    }

    // ----------------------------------------------------------
    // Internal HTTP Helper
    // ----------------------------------------------------------

    private <T> T sendRequest(String path,
                              String method,
                              Object body,
                              String authToken,
                              Class<T> responseClass) throws Exception {

        URL url = new URL(baseUrl + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setDoOutput(body != null);
        connection.setRequestProperty("Content-Type", "application/json");

        if (authToken != null) {
            connection.setRequestProperty("Authorization", authToken);
        }

        // Send JSON body if needed
        if (body != null) {
            try (OutputStream os = connection.getOutputStream()) {
                os.write(GSON.toJson(body).getBytes());
            }
        }

        int status = connection.getResponseCode();

        if (status != 200) {
            String error = readStream(connection.getErrorStream());
            ErrorResponse err = GSON.fromJson(error, ErrorResponse.class);
            String message = (err != null && err.message() != null) ?
                    err.message() : "An error occurred.";
            throw new Exception(message);
        }

        if (responseClass == null) {
            return null; // methods that return nothing
        }

        String json = readStream(connection.getInputStream());
        return GSON.fromJson(json, responseClass);
    }

    private String readStream(InputStream stream) throws IOException {
        if (stream == null) return "";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }
            return out.toString();
        }
    }

    private void ensureLoggedIn() throws Exception {
        if (authToken == null) {
            throw new Exception("You must be logged in first.");
        }
    }
}

