package handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import service.UserService;
import service.requests.LoginRequest;
import service.results.LoginResult;
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
            ctx.status(200).result(gson.toJson(res));
        } catch (DataAccessException e) {
            String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
            if (msg.contains("sql") || e.getCause() != null) {
                ctx.status(500).result(gson.toJson(Map.of("message", "Error: database failure")));
            } else {
                ctx.status(401).result(gson.toJson(Map.of("message", "Error: unauthorized")));
            }
        } catch (Exception e) {
            ctx.status(500).result(gson.toJson(Map.of("message", "Error: internal server error")));
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
            if (msg.contains("sql") || e.getCause() != null) {
                ctx.status(500).result(gson.toJson(Map.of("message", "Error: database failure")));
            } else {
                ctx.status(401).result(gson.toJson(Map.of("message", "Error: unauthorized")));
            }
        } catch (Exception e) {
            ctx.status(500).result(gson.toJson(Map.of("message", "Error: internal server error")));
        }
    }
}

