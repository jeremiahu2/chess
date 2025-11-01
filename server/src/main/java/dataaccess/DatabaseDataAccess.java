package dataaccess;

import model.UserData;
import model.GameData;
import model.AuthData;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DatabaseDataAccess implements DataAccess {
    private final Gson gson = new GsonBuilder().create();

    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM user");
            stmt.executeUpdate("DELETE FROM auth_token");
            stmt.executeUpdate("DELETE FROM game");
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing database", e);
        }
    }

    @Override
    public void createUser(UserData u) throws DataAccessException {
        if (u == null || u.username() == null || u.password() == null) {
            throw new DataAccessException("Invalid user data");
        }

        String sql = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(u.password(), BCrypt.gensalt());
        String emailValue = (u.email() != null) ? u.email() : "";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, u.username());
            stmt.setString(2, hashedPassword);
            stmt.setString(3, emailValue);

            int rows = stmt.executeUpdate();
            if (rows != 1) {
                throw new DataAccessException("Failed to insert user");
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error creating user", e);
        }
    }

    @Override
    public Optional<UserData> getUser(String username) throws DataAccessException {
        String sql = "SELECT username, password, email FROM user WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                UserData u = new UserData(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email")
                );
                return Optional.of(u);
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving user", e);
        }
    }

    @Override
    public void createAuth(AuthData a) throws DataAccessException {
        String sql = "INSERT INTO auth_token (token, username) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, a.authToken());
            stmt.setString(2, a.username());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error creating auth", e);
        }
    }

    @Override
    public Optional<AuthData> getAuth(String token) throws DataAccessException {
        String sql = "SELECT token, username FROM auth_token WHERE token = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                AuthData a = new AuthData(rs.getString("token"), rs.getString("username"));
                return Optional.of(a);
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving auth", e);
        }
    }

    @Override
    public void deleteAuth(String token) throws DataAccessException {
        String sql = "DELETE FROM auth_token WHERE token = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, token);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new DataAccessException("Auth token not found");
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error deleting auth", e);
        }
    }

    @Override
    public GameData createGame(GameData g) throws DataAccessException {
        String sql = "INSERT INTO game (whiteUsername, blackUsername, gameName, gameState) VALUES (?, ?, ?, ?)";
        String json = gson.toJson(g);

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, g.whiteUsername());
            stmt.setString(2, g.blackUsername());
            stmt.setString(3, g.gameName());
            stmt.setString(4, json);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                return new GameData(id, g.whiteUsername(), g.blackUsername(), g.gameName(), g.game());
            } else {
                throw new DataAccessException("Failed to get generated game ID");
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error creating game", e);
        }
    }

    @Override
    public Optional<GameData> getGame(int gameID) throws DataAccessException {
        String sql = "SELECT whiteUsername, blackUsername, gameName, gameState, id FROM game WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String white = rs.getString("whiteUsername");
                String black = rs.getString("blackUsername");
                String name  = rs.getString("gameName");
                String json  = rs.getString("gameState");
                int id       = rs.getInt("id");

                GameData g = gson.fromJson(json, GameData.class);
                return Optional.of(new GameData(id, white, black, name, g.game()));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving game", e);
        }
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        List<GameData> games = new ArrayList<>();
        String sql = "SELECT id, whiteUsername, blackUsername, gameName, gameState FROM game";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id       = rs.getInt("id");
                String white = rs.getString("whiteUsername");
                String black = rs.getString("blackUsername");
                String name  = rs.getString("gameName");
                String json  = rs.getString("gameState");

                GameData g = gson.fromJson(json, GameData.class);
                games.add(new GameData(id, white, black, name, g.game()));
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error listing games", e);
        }
        return games;
    }

    @Override
    public void updateGame(GameData g) throws DataAccessException {
        String sql = "UPDATE game SET whiteUsername = ?, blackUsername = ?, gameName = ?, gameState = ? WHERE id = ?";
        String json = gson.toJson(g);

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, g.whiteUsername());
            stmt.setString(2, g.blackUsername());
            stmt.setString(3, g.gameName());
            stmt.setString(4, json);
            stmt.setInt(5, g.gameID());

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new DataAccessException("Game not found");
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error updating game", e);
        }
    }
}
