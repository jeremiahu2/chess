package server;

import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import dataaccess.InMemoryDataAccess;
import dataaccess.DataAccess;
import service.UserService;
import service.GameService;
import handlers.UserHandler;
import handlers.SessionHandler;
import handlers.GameHandler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class Server {
    private Javalin javalin;
    private final DataAccess dao;
    private final UserService userService;
    private final GameService gameService;

    public Server() {
        this.dao = new InMemoryDataAccess();
        this.userService = new UserService(dao);
        this.gameService = new GameService(dao);
    }

    private void registerEndpoints() {
        var userHandler = new UserHandler(userService);
        var sessionHandler = new SessionHandler(userService);
        var gameHandler = new GameHandler(gameService);
        javalin.delete("/db", ctx -> {
            dao.clear();
            ctx.status(200).json(Map.of());
        });
        javalin.post("/user", userHandler::register);
        javalin.post("/session", sessionHandler::login);
        javalin.delete("/session", sessionHandler::logout);
        javalin.get("/game", gameHandler::listGames);
        javalin.post("/game", gameHandler::createGame);
        javalin.put("/game", gameHandler::joinGame);
    }

    public int run(int desiredPort) {
        javalin = Javalin.create(config -> {
            // Configure static files
            Path webDir = Path.of("web");
            if (Files.exists(webDir)) {
                config.staticFiles.add(webDir.toString());
            }

            // Configure Jackson for JSON
            config.jsonMapper(new JavalinJackson());
        }).start(desiredPort);

        registerEndpoints();
        return javalin.port();
    }

    public void stop() {
        if (javalin != null) {
            javalin.stop();
        }
    }
}
