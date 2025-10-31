package handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import service.ClearService;
import dataaccess.DataAccessException;

import java.util.Map;

public class ClearHandler {
    private final ClearService clearService;
    private final Gson gson = new Gson();

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    public void clear(Context ctx) {
        try {
            clearService.clear();
            ctx.status(200).result(gson.toJson(Map.of())); // return empty JSON object
        } catch (DataAccessException e) {
            ctx.status(500).result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }
}
