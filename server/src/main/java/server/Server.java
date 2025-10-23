package server;

import io.javalin.Javalin;
import dataaccess.InMemoryDataAccess;
import dataaccess.DataAccess;
import service.UserService;
import service.GameService;
import handlers.UserHandler;
import handlers.SessionHandler;
import handlers.GameHandler;

import java.util.Map;

public class Server {
    private final Javalin javalin;
    private final DataAccess dao;
    private final UserService userService;
    private final GameService gameService;

    public Server() {
        this.javalin = Javalin.create(config -> config.staticFiles.add("web"));
        this.dao = new InMemoryDataAccess();
        this.userService = new UserService(dao);
        this.gameService = new GameService(dao);

        registerEndpoints();
    }

    private void registerEndpoints() {
        var userHandler = new UserHandler(userService);
        var sessionHandler = new SessionHandler(userService);
        var gameHandler = new GameHandler(gameService);

        // Clear DB
        javalin.delete("/db", ctx -> {
            dao.clear();
            ctx.status(200).json(Map.of());
        });

        // Register
        javalin.post("/user", userHandler::register);

        // Login & Logout
        javalin.post("/session", sessionHandler::login);
        javalin.delete("/session", sessionHandler::logout);

        // Games
        javalin.get("/game", gameHandler::listGames);
        javalin.post("/game", gameHandler::createGame);
        javalin.put("/game", gameHandler::joinGame);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
