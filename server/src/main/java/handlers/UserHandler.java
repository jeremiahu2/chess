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
            RegisterResult res = userService.register(req);
            ctx.status(200).json(res);
        } catch (DataAccessException e) {
            String msg = e.getMessage();
            if ("already taken".equals(msg)) ctx.status(403).json(Map.of("message", "Error: already taken"));
            else if ("bad request".equals(msg)) ctx.status(400).json(Map.of("message", "Error: bad request"));
            else ctx.status(500).json(Map.of("message", "Error: " + msg));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}
