package server;

import io.javalin.Javalin;
import handlers.GameHandler;
import handlers.SessionHandler;
import handlers.UserHandler;
import service.GameService;
import service.UserService;
import dataaccess.DataAccess;
import dataaccess.InMemoryDataAccess;
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
        UserHandler userHandler = new UserHandler(userService);
        SessionHandler sessionHandler = new SessionHandler(userService);
        GameHandler gameHandler = new GameHandler(gameService);

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
            config.staticFiles.add("web");
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
