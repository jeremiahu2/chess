package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.GameData;
import model.ChessGame;
import service.requests.CreateGameRequest;
import service.requests.JoinGameRequest;
import service.results.CreateGameResult;

import java.util.List;
import java.util.Optional;

public class GameService {
    private final DataAccess dao;

    public GameService(DataAccess dao) {
        this.dao = dao;
    }

    public List<GameData> listGames(String authToken) throws DataAccessException {
        if (authToken == null || dao.getAuth(authToken).isEmpty()) {
            throw new DataAccessException("unauthorized");
        }
        return dao.listGames();
    }

    public CreateGameResult createGame(String authToken, CreateGameRequest req) throws DataAccessException {
        if (authToken == null || dao.getAuth(authToken).isEmpty()) {
            throw new DataAccessException("unauthorized");
        }
        if (req == null || req.gameName() == null) {
            throw new DataAccessException("bad request");
        }
        ChessGame game = new ChessGame();
        GameData toCreate = new GameData(0, null, null, req.gameName(), game);
        GameData created = dao.createGame(toCreate);
        return new CreateGameResult(created.gameID());
    }

    public void joinGame(String authToken, JoinGameRequest req) throws DataAccessException {
        if (authToken == null || dao.getAuth(authToken).isEmpty()) {
            throw new DataAccessException("unauthorized");
        }
        if (req == null) {
            throw new DataAccessException("bad request");
        }
        Optional<GameData> gOpt = dao.getGame(req.gameID());
        if (gOpt.isEmpty()) {
            throw new DataAccessException("bad request");
        }
        GameData g = gOpt.get();
        String playerColor = req.playerColor();
        String username = dao.getAuth(authToken).get().username();
        if ("WHITE".equalsIgnoreCase(playerColor)) {
            if (g.whiteUsername() != null) {
                throw new DataAccessException("already taken");
            }
            GameData updated = new GameData(g.gameID(), username, g.blackUsername(), g.gameName(), g.game());
            dao.updateGame(updated);
        } else if ("BLACK".equalsIgnoreCase(playerColor)) {
            if (g.blackUsername() != null) {
                throw new DataAccessException("already taken");
            }
            GameData updated = new GameData(g.gameID(), g.whiteUsername(), username, g.gameName(), g.game());
            dao.updateGame(updated);
        } else {
            throw new DataAccessException("bad request");
        }
    }
}
