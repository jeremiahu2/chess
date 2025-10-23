package dataaccess;
import model.UserData;
import model.GameData;
import model.AuthData;
import java.util.List;
import java.util.Optional;

public interface DataAccess {
    void clear() throws DataAccessException;
    void createUser(UserData u) throws DataAccessException;
    Optional<UserData> getUser(String username) throws DataAccessException;
    GameData createGame(GameData g) throws DataAccessException;
    Optional<GameData> getGame(int gameID) throws DataAccessException;
    List<GameData> listGames() throws DataAccessException;
    void updateGame(GameData g) throws DataAccessException;
    void createAuth(AuthData a) throws DataAccessException;
    Optional<AuthData> getAuth(String token) throws DataAccessException;
    void deleteAuth(String token) throws DataAccessException;
}
