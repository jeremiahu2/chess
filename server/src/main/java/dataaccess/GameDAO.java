package dataaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.GameData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameDAO {
    private final Gson gson;
    public GameDAO() {
        gson = new GsonBuilder().create();
    }

    public GameData createGame(GameData game) throws DataAccessException {
        String sql = "INSERT INTO game (id, gameState) VALUES (?, ?)";
        String json = gson.toJson(game);
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, game.gameID());
            stmt.setString(2, json);
            stmt.executeUpdate();
            return game;
        } catch (SQLException e) {
            throw new DataAccessException("Error creating game", e);
        }
    }

    public Optional<GameData> getGame(int gameID) throws DataAccessException {
        String sql = "SELECT gameState FROM game WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String json = rs.getString("gameState");
                GameData game = gson.fromJson(json, GameData.class);
                return Optional.of(game);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DataAccessException("Error getting game", e);
        }
    }

    public void updateGame(GameData game) throws DataAccessException {
        String sql = "UPDATE game SET gameState = ? WHERE id = ?";
        String json = gson.toJson(game);
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, json);
            stmt.setInt(2, game.gameID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error updating game", e);
        }
    }

    public List<GameData> listGames() throws DataAccessException {
        String sql = "SELECT gameState FROM game";
        List<GameData> games = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String json = rs.getString("gameState");
                GameData game = gson.fromJson(json, GameData.class);
                games.add(game);
            }
            return games;
        } catch (SQLException e) {
            throw new DataAccessException("Error listing games", e);
        }
    }

    public void clear() throws DataAccessException {
        String sql = "DELETE FROM game";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing games", e);
        }
    }
}
