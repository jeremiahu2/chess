package handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import service.GameService;
import service.requests.CreateGameRequest;
import service.requests.JoinGameRequest;
import service.results.CreateGameResult;
import model.GameData;

import java.util.List;
import java.util.Map;

public class GameHandler {
    private final GameService gameService;
    private final Gson gson = new Gson();

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void listGames(Context ctx) {
        try {
            String token = HandlerUtils.getAuthToken(ctx);
            if (token == null || token.isEmpty()) {
                ctx.status(401).result(gson.toJson(Map.of("message", "Error: unauthorized")));
                return;
            }
            List<GameData> games = gameService.listGames(token);
            ctx.status(200).result(gson.toJson(Map.of("games", games)));
        } catch (Exception e) {
            HandlerUtils.handleException(ctx, e);
        }
    }

    public void createGame(Context ctx) {
        try {
            String token = HandlerUtils.getAuthToken(ctx);
            if (token == null || token.isEmpty()) {
                ctx.status(401).result(gson.toJson(Map.of("message", "Error: unauthorized")));
                return;
            }
            CreateGameRequest req = gson.fromJson(ctx.body(), CreateGameRequest.class);
            if (req == null || req.gameName() == null) {
                ctx.status(400).result(gson.toJson(Map.of("message", "Error: bad request")));
                return;
            }
            CreateGameResult res = gameService.createGame(token, req);
            ctx.status(200).result(gson.toJson(res));
        } catch (Exception e) {
            HandlerUtils.handleException(ctx, e);
        }
    }

    public void joinGame(Context ctx) {
        try {
            String token = HandlerUtils.getAuthToken(ctx);
            if (token == null || token.isEmpty()) {
                ctx.status(401).result(gson.toJson(Map.of("message", "Error: unauthorized")));
                return;
            }
            JoinGameRequest req = gson.fromJson(ctx.body(), JoinGameRequest.class);
            if (req == null || req.gameID() == 0) {
                ctx.status(400).result(gson.toJson(Map.of("message", "Error: bad request")));
                return;
            }
            gameService.joinGame(token, req);
            ctx.status(200).result(gson.toJson(Map.of()));
        } catch (Exception e) {
            HandlerUtils.handleException(ctx, e);
        }
    }
}
