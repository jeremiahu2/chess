package handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import java.util.Map;

public class HandlerUtils {
    private static final Gson gson = new Gson();

    public static String getAuthToken(Context ctx) {
        return ctx.header("authorization");
    }

    public static void handleException(Context ctx, Exception e) {
        String msg = (e.getMessage() != null) ? e.getMessage().toLowerCase() : "";
        int status = 500;
        String message = "Error: " + e.getMessage();
        if (msg.contains("unauthorized")) {
            status = 401;
            message = "Error: unauthorized";
        } else if (msg.contains("bad")) {
            status = 400;
            message = "Error: bad request";
        } else if (msg.contains("taken")) {
            status = 403;
            message = "Error: already taken";
        }
        ctx.status(status).result(gson.toJson(Map.of("message", message)));
    }
}
