package handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import service.GameService;
import service.requests.CreateGameRequest;
import service.requests.JoinGameRequest;
import service.results.CreateGameResult;
import dataaccess.DataAccessException;
import model.GameData;

import java.util.List;
import java.util.Map;

public class GameHandler {
    private final GameService gameService;
    private final Gson gson = new Gson();

    public GameHandler(GameService gameService) { this.gameService = gameService; }

    // GET /game
    public void listGames(Context ctx) {
        try {
            String token = ctx.header("authorization");
            List<GameData> games = gameService.listGames(token);
            ctx.status(200).json(Map.of("games", games));
        } catch (DataAccessException e) {
            if ("unauthorized".equals(e.getMessage())) ctx.status(401).json(Map.of("message", "Error: unauthorized"));
            else ctx.status(500).json(Map.of("message", "Error: " + e.getMessage()));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    // POST /game
    public void createGame(Context ctx) {
        try {
            String token = ctx.header("authorization");
            CreateGameRequest req = gson.fromJson(ctx.body(), CreateGameRequest.class);
            CreateGameResult res = gameService.createGame(token, req);
            ctx.status(200).json(res);
        } catch (DataAccessException e) {
            String msg = e.getMessage();
            if ("unauthorized".equals(msg)) ctx.status(401).json(Map.of("message", "Error: unauthorized"));
            else if ("bad request".equals(msg)) ctx.status(400).json(Map.of("message", "Error: bad request"));
            else ctx.status(500).json(Map.of("message", "Error: " + msg));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    // PUT /game
    public void joinGame(Context ctx) {
        try {
            String token = ctx.header("authorization");
            JoinGameRequest req = gson.fromJson(ctx.body(), JoinGameRequest.class);
            gameService.joinGame(token, req);
            ctx.status(200).json(Map.of());
        } catch (DataAccessException e) {
            String msg = e.getMessage();
            if ("unauthorized".equals(msg)) ctx.status(401).json(Map.of("message", "Error: unauthorized"));
            else if ("already taken".equals(msg)) ctx.status(403).json(Map.of("message", "Error: already taken"));
            else if ("bad request".equals(msg)) ctx.status(400).json(Map.of("message", "Error: bad request"));
            else ctx.status(500).json(Map.of("message", "Error: " + msg));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}
