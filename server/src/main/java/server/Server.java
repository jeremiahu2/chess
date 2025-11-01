package server;

import dataaccess.*;
import io.javalin.Javalin;
import handlers.GameHandler;
import handlers.SessionHandler;
import handlers.UserHandler;
import handlers.ClearHandler;
import service.GameService;
import service.UserService;
import service.ClearService;
import com.google.gson.Gson;

public class Server {

    private Javalin javalin;
    private final DataAccess dao;
    private final UserService userService;
    private final GameService gameService;
    private final ClearService clearService;
    private final Gson gson = new Gson();

    public Server() {
        DataAccess tempDao;
        try {
            DatabaseManager.initialize();
            tempDao = new DatabaseDataAccess();
        } catch (DataAccessException e) {
            System.err.println("Warning: Failed to create database — running in memory mode.");
            tempDao = new InMemoryDataAccess();
        }
        this.dao = tempDao;
        this.userService = new UserService(dao);
        this.gameService = new GameService(dao);
        this.clearService = new ClearService(dao);
    }

    private void registerEndpoints() {
        UserHandler userHandler = new UserHandler(userService);
        SessionHandler sessionHandler = new SessionHandler(userService);
        GameHandler gameHandler = new GameHandler(gameService);
        ClearHandler clearHandler = new ClearHandler(clearService);
        javalin.delete("/db", clearHandler::clear);
        javalin.post("/user", userHandler::register);
        javalin.post("/session", sessionHandler::login);
        javalin.delete("/session", sessionHandler::logout);
        javalin.get("/game", gameHandler::listGames);
        javalin.post("/game", gameHandler::createGame);
        javalin.put("/game", gameHandler::joinGame);
    }

    public int run(int desiredPort) {
        javalin = Javalin.create(config -> {
            config.staticFiles.add(staticFileConfig -> {
                staticFileConfig.directory = "/web";
                staticFileConfig.hostedPath = "/";
                staticFileConfig.location = io.javalin.http.staticfiles.Location.CLASSPATH;
            });
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
