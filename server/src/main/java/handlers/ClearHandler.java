package handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import service.ClearService;
import java.util.Map;

public class ClearHandler {
    private final ClearService clearService;
    private static final Gson GSON = new Gson();

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    public void clear(Context ctx) {
        try {
            clearService.clear();
            ctx.status(200).result(GSON.toJson(Map.of()));
        } catch (Exception e) {
            HandlerUtils.handleException(ctx, e);
        }
    }
}
