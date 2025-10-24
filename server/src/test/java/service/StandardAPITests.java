package service;

import dataaccess.InMemoryDataAccess;
import dataaccess.DataAccessException;
import model.GameData;
import org.junit.jupiter.api.*;
import service.requests.*;
import service.results.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StandardAPITests {

    @Nested
    class UserServiceTest {
        private InMemoryDataAccess dao;
        private UserService userService;

        @BeforeEach
        public void setup() {
            dao = new InMemoryDataAccess();
            userService = new UserService(dao);
        }

        @Test
        public void registerSuccess() throws Exception {
            RegisterRequest req = new RegisterRequest("alice", "pw", "a@example.com");
            RegisterResult res = userService.register(req);
            assertEquals("alice", res.username());
            assertNotNull(res.authToken());
        }

        @Test
        public void registerAlreadyTaken() throws Exception {
            RegisterRequest req = new RegisterRequest("bob", "pw", "b@example.com");
            userService.register(req);
            assertThrows(DataAccessException.class, () -> userService.register(req));
        }

        @Test
        public void loginSuccessAndLogout() throws Exception {
            RegisterRequest r = new RegisterRequest("carol", "pw", "c@example.com");
            userService.register(r);

            LoginRequest loginReq = new LoginRequest("carol", "pw");
            LoginResult lr = userService.login(loginReq);
            assertEquals("carol", lr.username());
            assertNotNull(lr.authToken());
            userService.logout(lr.authToken());
            assertThrows(DataAccessException.class, () -> userService.logout(lr.authToken()));
        }

        @Test
        public void loginBadPasswordUnauthorized() throws Exception {
            RegisterRequest r = new RegisterRequest("dan", "pw", "d@example.com");
            userService.register(r);
            LoginRequest loginReq = new LoginRequest("dan", "wrongpw");
            assertThrows(DataAccessException.class, () -> userService.login(loginReq));
        }
    }

    @Nested
    class GameServiceTest {
        private InMemoryDataAccess dao;
        private GameService gameService;
        private String token;

        @BeforeEach
        public void setup() throws DataAccessException {
            dao = new InMemoryDataAccess();
            gameService = new GameService(dao);
            UserService userService = new UserService(dao);
            RegisterRequest regReq = new RegisterRequest("player1", "pw", "p1@example.com");
            token = userService.register(regReq).authToken();
        }

        @Test
        public void listGamesSuccess() throws Exception {
            CreateGameRequest req = new CreateGameRequest("TestGame");
            gameService.createGame(token, req);
            List<GameData> games = gameService.listGames(token);
            assertNotNull(games);
            assertEquals(1, games.size());
            assertEquals("TestGame", games.get(0).gameName());
        }

        @Test
        public void listGamesUnauthorized() {
            assertThrows(DataAccessException.class, () -> gameService.listGames("invalid-token"));
        }

        @Test
        public void createGameSuccess() throws Exception {
            CreateGameRequest req = new CreateGameRequest("ChessMatch");
            CreateGameResult res = gameService.createGame(token, req);
            assertNotNull(res);
            assertTrue(res.gameID() > 0);
            GameData stored = dao.getGame(res.gameID()).orElseThrow();
            assertEquals("ChessMatch", stored.gameName());
        }

        @Test
        public void createGameUnauthorized() {
            CreateGameRequest req = new CreateGameRequest("ChessMatch");
            assertThrows(DataAccessException.class, () -> gameService.createGame("bad-token", req));
        }

        @Test
        public void createGameBadRequest() {
            assertThrows(DataAccessException.class, () -> gameService.createGame(token, null));
            assertThrows(DataAccessException.class, () -> gameService.createGame(token, new CreateGameRequest(null)));
        }

        @Test
        public void joinGameSuccessWhite() throws Exception {
            CreateGameRequest req = new CreateGameRequest("Match1");
            int gameID = gameService.createGame(token, req).gameID();
            JoinGameRequest joinReq = new JoinGameRequest("WHITE", gameID);
            gameService.joinGame(token, joinReq);
            GameData game = dao.getGame(gameID).orElseThrow();
            assertEquals("player1", game.whiteUsername());
            assertNull(game.blackUsername());
        }

        @Test
        public void joinGameSuccessBlack() throws Exception {
            UserService userService = new UserService(dao);
            String token2 = userService.register(new RegisterRequest("player2","pw","p2@example.com")).authToken();
            CreateGameRequest req = new CreateGameRequest("Match2");
            int gameID = gameService.createGame(token, req).gameID();

            gameService.joinGame(token, new JoinGameRequest("WHITE", gameID));
            gameService.joinGame(token2, new JoinGameRequest("BLACK", gameID));

            GameData game = dao.getGame(gameID).orElseThrow();
            assertEquals("player1", game.whiteUsername());
            assertEquals("player2", game.blackUsername());
        }

        @Test
        public void joinGameAlreadyTaken() throws Exception {
            CreateGameRequest req = new CreateGameRequest("Match3");
            int gameID = gameService.createGame(token, req).gameID();

            gameService.joinGame(token, new JoinGameRequest("WHITE", gameID));
            assertThrows(DataAccessException.class, () -> gameService.joinGame(token, new JoinGameRequest("WHITE", gameID)));
        }

        @Test
        public void joinGameBadRequest() {
            assertThrows(DataAccessException.class, () -> gameService.joinGame(token, null));
            assertThrows(DataAccessException.class, () -> gameService.joinGame(token, new JoinGameRequest("INVALID_COLOR", 1)));
            assertThrows(DataAccessException.class, () -> gameService.joinGame(token, new JoinGameRequest("WHITE", 999)));
        }

        @Test
        public void joinGameUnauthorized() throws Exception {
            CreateGameRequest req = new CreateGameRequest("Match4");
            int gameID = gameService.createGame(token, req).gameID();

            JoinGameRequest joinReq = new JoinGameRequest("WHITE", gameID);
            assertThrows(DataAccessException.class, () -> gameService.joinGame("bad-token", joinReq));
        }
    }
}
