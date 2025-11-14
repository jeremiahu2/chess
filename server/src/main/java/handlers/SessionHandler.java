package handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import service.UserService;
import requests.LoginRequest;
import results.LoginResult;
import dataaccess.DataAccessException;
import java.util.Map;

public class SessionHandler {
    private final UserService userService;
    private final Gson gson = new Gson();

    public SessionHandler(UserService userService) {
        this.userService = userService;
    }

    public void login(Context ctx) {
        try {
            LoginRequest req = gson.fromJson(ctx.body(), LoginRequest.class);
            if (req == null || req.username() == null || req.password() == null) {
                ctx.status(400).result(gson.toJson(Map.of("message", "Error: bad request")));
                return;
            }
            LoginResult res = userService.login(req);
            ctx.status(200).result(gson.toJson(Map.of(
                    "username", res.username(),
                    "authToken", res.authToken()
            )));
        } catch (DataAccessException e) {
            String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
            if (msg.contains("unauthorized")) {
                ctx.status(401).result(gson.toJson(Map.of("message", "Error: unauthorized")));
            } else if (msg.contains("bad request")) {
                ctx.status(400).result(gson.toJson(Map.of("message", "Error: bad request")));
            } else {
                ctx.status(500).result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
            }
        } catch (Exception e) {
            HandlerUtils.handleException(ctx, e);
        }
    }

    public void logout(Context ctx) {
        try {
            String token = HandlerUtils.getAuthToken(ctx);
            if (token == null || token.isEmpty()) {
                ctx.status(401).result(gson.toJson(Map.of("message", "Error: unauthorized")));
                return;
            }
            userService.logout(token);
            ctx.status(200).result(gson.toJson(Map.of()));
        } catch (DataAccessException e) {
            String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
            if (msg.contains("unauthorized") || msg.contains("auth token")) {
                ctx.status(401).result(gson.toJson(Map.of("message", "Error: unauthorized")));
            } else if (msg.contains("bad request")) {
                ctx.status(400).result(gson.toJson(Map.of("message", "Error: bad request")));
            } else {
                ctx.status(500).result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
            }
        } catch (Exception e) {
            HandlerUtils.handleException(ctx, e);
        }
    }
}
