package handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import java.util.Map;

public class HandlerUtils {
    private static final Gson GSON = new Gson();

    public static String getAuthToken(Context ctx) {
        return ctx.header("authorization");
    }

    public static void handleException(Context ctx, Exception e) {
        if (ctx == null) return;
        String rawMessage = (e != null && e.getMessage() != null) ? e.getMessage() : "";
        String msg = rawMessage.toLowerCase();
        int status = 500;
        String message = "Error: " + rawMessage;
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
        try {
            ctx.status(status).result(GSON.toJson(Map.of("message", message)));
        } catch (Exception inner) {
            System.err.println("Failed to handle exception: " + inner.getMessage());
        }
    }
}
