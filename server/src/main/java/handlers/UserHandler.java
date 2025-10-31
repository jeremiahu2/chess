package handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import service.UserService;
import service.requests.RegisterRequest;
import service.requests.LoginRequest;
import service.results.RegisterResult;
import service.results.LoginResult;
import dataaccess.DataAccessException;

import java.util.Map;

public class UserHandler {
    private final UserService userService;
    private final Gson gson = new Gson();

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public void register(Context ctx) {
        try {
            RegisterRequest req = gson.fromJson(ctx.body(), RegisterRequest.class);
            if (req == null || req.username() == null || req.password() == null || req.email() == null) {
                ctx.status(400).result(gson.toJson(Map.of("message", "Error: bad request")));
                return;
            }
            RegisterResult res = userService.register(req);
            ctx.status(200).result(gson.toJson(res));
        } catch (DataAccessException e) {
            String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
            if (msg.contains("already")) {
                ctx.status(403).result(gson.toJson(Map.of("message", "Error: already taken")));
            } else {
                ctx.status(500).result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
            }
        } catch (Exception e) {
            ctx.status(500).result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }

    public void login(Context ctx) {
        try {
            LoginRequest req = gson.fromJson(ctx.body(), LoginRequest.class);
            if (req == null || req.username() == null || req.password() == null) {
                ctx.status(400).result(gson.toJson(Map.of("message", "Error: bad request")));
                return;
            }
            LoginResult res = userService.login(req);
            ctx.status(200).result(gson.toJson(res));
        } catch (DataAccessException e) {
            ctx.status(401).result(gson.toJson(Map.of("message", "Error: unauthorized")));
        } catch (Exception e) {
            ctx.status(500).result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
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
            ctx.status(401).result(gson.toJson(Map.of("message", "Error: unauthorized")));
        } catch (Exception e) {
            ctx.status(500).result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }
}
