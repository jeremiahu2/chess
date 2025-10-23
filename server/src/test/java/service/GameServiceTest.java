package service;

import dataAccess.InMemoryDataAccess;
import dataAccess.DataAccessException;
import model.GameData;
import service.requests.CreateGameRequest;
import service.requests.JoinGameRequest;
import service.results.CreateGameResult;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {
    private InMemoryDataAccess dao;
    private GameService gameService;
    private String token;

    @BeforeEach
    public void setup() throws DataAccessException {
        dao = new InMemoryDataAccess();
        gameService = new GameService(dao);
        var userService = new UserService(dao);
        var regReq = new service.requests.RegisterRequest("player1", "pw", "p1@example.com");
        token = userService.register(regReq).authToken();
    }

    @Test
    public void listGames_success() throws Exception {
        CreateGameRequest req = new CreateGameRequest("TestGame");
        gameService.createGame(token, req);
        List<GameData> games = gameService.listGames(token);
        assertNotNull(games);
        assertEquals(1, games.size());
        assertEquals("TestGame", games.get(0).gameName());
    }

    @Test
    public void listGames_unauthorized() {
        assertThrows(DataAccessException.class, () -> gameService.listGames("invalid-token"));
    }

    @Test
    public void createGame_success() throws Exception {
        CreateGameRequest req = new CreateGameRequest("ChessMatch");
        CreateGameResult res = gameService.createGame(token, req);
        assertNotNull(res);
        assertTrue(res.gameID() > 0);
        GameData stored = dao.getGame(res.gameID()).orElseThrow();
        assertEquals("ChessMatch", stored.gameName());
    }

    @Test
    public void createGame_unauthorized() {
        CreateGameRequest req = new CreateGameRequest("ChessMatch");
        assertThrows(DataAccessException.class, () -> gameService.createGame("bad-token", req));
    }

    @Test
    public void createGame_badRequest() {
        assertThrows(DataAccessException.class, () -> gameService.createGame(token, null));
        assertThrows(DataAccessException.class, () -> gameService.createGame(token, new CreateGameRequest(null)));
    }

    @Test
    public void joinGame_successWhite() throws Exception {
        CreateGameRequest req = new CreateGameRequest("Match1");
        int gameID = gameService.createGame(token, req).gameID();
        JoinGameRequest joinReq = new JoinGameRequest("WHITE", gameID);
        gameService.joinGame(token, joinReq);
        GameData game = dao.getGame(gameID).orElseThrow();
        assertEquals("player1", game.whiteUsername());
        assertNull(game.blackUsername());
    }

    @Test
    public void joinGame_successBlack() throws Exception {
        var userService = new UserService(dao);
        String token2 = userService.register(new service.requests.RegisterRequest("player2","pw","p2@example.com")).authToken();
        CreateGameRequest req = new CreateGameRequest("Match2");
        int gameID = gameService.createGame(token, req).gameID();

        gameService.joinGame(token, new JoinGameRequest("WHITE", gameID));
        gameService.joinGame(token2, new JoinGameRequest("BLACK", gameID));

        GameData game = dao.getGame(gameID).orElseThrow();
        assertEquals("player1", game.whiteUsername());
        assertEquals("player2", game.blackUsername());
    }

    @Test
    public void joinGame_alreadyTaken() throws Exception {
        CreateGameRequest req = new CreateGameRequest("Match3");
        int gameID = gameService.createGame(token, req).gameID();

        gameService.joinGame(token, new JoinGameRequest("WHITE", gameID));
        assertThrows(DataAccessException.class, () -> gameService.joinGame(token, new JoinGameRequest("WHITE", gameID)));
    }

    @Test
    public void joinGame_badRequest() {
        assertThrows(DataAccessException.class, () -> gameService.joinGame(token, null));
        assertThrows(DataAccessException.class, () -> gameService.joinGame(token, new JoinGameRequest("INVALID_COLOR", 1)));
        assertThrows(DataAccessException.class, () -> gameService.joinGame(token, new JoinGameRequest("WHITE", 999))); // non-existent game
    }

    @Test
    public void joinGame_unauthorized() throws Exception {
        CreateGameRequest req = new CreateGameRequest("Match4");
        int gameID = gameService.createGame(token, req).gameID();

        JoinGameRequest joinReq = new JoinGameRequest("WHITE", gameID);
        assertThrows(DataAccessException.class, () -> gameService.joinGame("bad-token", joinReq));
    }
}
