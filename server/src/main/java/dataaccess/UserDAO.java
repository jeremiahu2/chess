package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserDAO {
    public void createUser(UserData user) throws DataAccessException {
        String sql = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String hashed = BCrypt.hashpw(user.password(), BCrypt.gensalt());
            stmt.setString(1, user.username());
            stmt.setString(2, hashed);
            stmt.setString(3, user.email());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error creating user", e);
        }
    }

    public Optional<UserData> getUser(String username) throws DataAccessException {
        String sql = "SELECT * FROM user WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                UserData user = new UserData(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email")
                );
                return Optional.of(user);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DataAccessException("Error getting user", e);
        }
    }

    public void clear() throws DataAccessException {
        String sql = "DELETE FROM user";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing users", e);
        }
    }

    public boolean verifyPassword(String username, String password) throws DataAccessException {
        Optional<UserData> userOpt = getUser(username);
        if (userOpt.isEmpty()) return false;
        String storedHash = userOpt.get().password();
        return BCrypt.checkpw(password, storedHash);
    }
}
