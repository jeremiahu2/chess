package handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import service.UserService;
import service.requests.RegisterRequest;
import service.results.RegisterResult;
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
                ctx.status(400).json(Map.of("message", "Error: bad request"));
                return;
            }
            RegisterResult res = userService.register(req);
            ctx.status(200).json(res);
        } catch (DataAccessException e) {
            String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
            if (msg.contains("already") || msg.contains("taken")) {
                ctx.status(403).json(Map.of("message", "Error: already taken"));
            } else if (msg.contains("bad") || msg.contains("request")) {
                ctx.status(400).json(Map.of("message", "Error: bad request"));
            } else {
                ctx.status(500).json(Map.of("message", "Error: " + e.getMessage()));
            }
        } catch (Exception e) {
            ctx.status(500).json(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}
