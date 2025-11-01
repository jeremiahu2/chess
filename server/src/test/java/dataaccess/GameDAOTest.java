package dataaccess;

import com.google.gson.Gson;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class GameDAOTest {

    private GameDAO gameDAO;
    private final Gson gson = new Gson();

    @BeforeEach
    public void setUp() throws DataAccessException {
        gameDAO = new GameDAO();
        gameDAO.clear();
    }

    @Test
    public void createAndGetGameSuccess() throws DataAccessException {
        GameData game = new GameData(1, "whitePlayer", "blackPlayer", "open", null);
        gameDAO.createGame(game);
        Optional<GameData> result = gameDAO.getGame(1);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().gameID());
        assertEquals("whitePlayer", result.get().whiteUsername());
    }

    @Test
    public void updateGameSuccess() throws DataAccessException {
        GameData game = new GameData(2, "w", "b", "open", null);
        gameDAO.createGame(game);
        GameData updated = new GameData(2, "w", "b", "closed", null);
        gameDAO.updateGame(updated);
        Optional<GameData> result = gameDAO.getGame(2);
        assertTrue(result.isPresent());
        assertEquals("closed", result.get().gameName());
    }

    @Test
    public void listGamesReturnsAll() throws DataAccessException {
        gameDAO.createGame(new GameData(10, "a", "b", "g1", null));
        gameDAO.createGame(new GameData(11, "x", "y", "g2", null));
        List<GameData> games = gameDAO.listGames();
        assertEquals(2, games.size());
    }

    @Test
    public void clearRemovesAllGames() throws DataAccessException {
        gameDAO.createGame(new GameData(3, "p1", "p2", "g3", null));
        gameDAO.clear();
        Optional<GameData> result = gameDAO.getGame(3);
        assertTrue(result.isEmpty());
    }
}
