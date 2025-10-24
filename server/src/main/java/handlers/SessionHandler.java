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
                ctx.status(400).json(Map.of("message", "Error: bad request"));
                return;
            }
            LoginResult res = userService.login(req);
            ctx.status(200).json(res);
        } catch (DataAccessException e) {
            String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
            if (msg.contains("bad")) {
                ctx.status(400).json(Map.of("message", "Error: bad request"));
            } else if (msg.contains("unauthorized")) {
                ctx.status(401).json(Map.of("message", "Error: unauthorized"));
            } else {
                ctx.status(500).json(Map.of("message", "Error: " + e.getMessage()));
            }
        } catch (Exception e) {
            ctx.status(500).json(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    public void logout(Context ctx) {
        try {
            String token = ctx.header("authorization");
            if (token == null || token.isEmpty()) {
                ctx.status(401).json(Map.of("message", "Error: unauthorized"));
                return;
            }
            userService.logout(token);
            ctx.status(200).json(Map.of());
        } catch (DataAccessException e) {
            String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
            if (msg.contains("unauthorized")) {
                ctx.status(401).json(Map.of("message", "Error: unauthorized"));
            } else {
                ctx.status(500).json(Map.of("message", "Error: " + e.getMessage()));
            }
        } catch (Exception e) {
            ctx.status(500).json(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}
