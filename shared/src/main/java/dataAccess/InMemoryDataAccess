package dataAccess;

import model.UserData;
import model.GameData;
import model.AuthData;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryDataAccess implements DataAccess {
    private final Map<String, UserData> users = new ConcurrentHashMap<>();
    private final Map<Integer, GameData> games = new ConcurrentHashMap<>();
    private final Map<String, AuthData> auths = new ConcurrentHashMap<>();
    private final AtomicInteger nextGameId = new AtomicInteger(1);

    @Override
    public void clear() {
        users.clear();
        games.clear();
        auths.clear();
        nextGameId.set(1);
    }

    @Override
    public void createUser(UserData u) throws DataAccessException {
        if (u == null || u.username() == null) throw new DataAccessException("bad user");
        if (users.putIfAbsent(u.username(), u) != null) throw new DataAccessException("already taken");
    }

    @Override
    public Optional<UserData> getUser(String username) {
        return Optional.ofNullable(users.get(username));
    }

    @Override
    public GameData createGame(GameData g) {
        int id = nextGameId.getAndIncrement();
        GameData created = new GameData(id, g.whiteUsername(), g.blackUsername(), g.gameName(), g.game());
        games.put(id, created);
        return created;
    }

    @Override
    public Optional<GameData> getGame(int gameID) {
        return Optional.ofNullable(games.get(gameID));
    }

    @Override
    public List<GameData> listGames() {
        return new ArrayList<>(games.values());
    }

    @Override
    public void updateGame(GameData g) throws DataAccessException {
        if (g == null) throw new DataAccessException("bad game");
        if (!games.containsKey(g.gameID())) throw new DataAccessException("game not found");
        games.put(g.gameID(), g);
    }

    @Override
    public void createAuth(AuthData a) {
        auths.put(a.authToken(), a);
    }

    @Override
    public Optional<AuthData> getAuth(String token) {
        return Optional.ofNullable(auths.get(token));
    }

    @Override
    public void deleteAuth(String token) throws DataAccessException {
        if (token == null || auths.remove(token) == null) throw new DataAccessException("unauthorized");
    }
}
