package dataaccess;

import model.AuthData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class AuthTokenDAO {

    public void createAuth(AuthData auth_token) throws DataAccessException {
        String sql = "INSERT INTO auth_token (token, username) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, auth_token.authToken());
            stmt.setString(2, auth_token.username());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error creating auth token", e);
        }
    }

    public Optional<AuthData> getAuth(String token) throws DataAccessException {
        String sql = "SELECT * FROM auth_token WHERE token = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                AuthData authToken = new AuthData(
                        rs.getString("token"),
                        rs.getString("username")
                );
                return Optional.of(authToken);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DataAccessException("Error getting auth token", e);
        }
    }

    public void deleteAuth(String token) throws DataAccessException {
        String sql = "DELETE FROM auth_token WHERE token = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting auth token", e);
        }
    }

    public void clear() throws DataAccessException {
        String sql = "DELETE FROM auth_token";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing auth tokens", e);
        }
    }
}
