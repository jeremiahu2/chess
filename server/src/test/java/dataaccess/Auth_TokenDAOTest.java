package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class Auth_TokenDAOTest {

    private Auth_TokenDAO authDAO;
    private UserDAO userDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        authDAO = new Auth_TokenDAO();
        userDAO = new UserDAO();
        authDAO.clear();
        userDAO.clear();
        userDAO.createUser(new UserData("alice", "password", "alice@example.com"));
        userDAO.createUser(new UserData("bob", "password", "bob@example.com"));
        userDAO.createUser(new UserData("u1", "password", "u1@example.com"));
        userDAO.createUser(new UserData("u2", "password", "u2@example.com"));
    }

    @Test
    public void createAndGetAuth_success() throws DataAccessException {
        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, "alice");
        authDAO.createAuth(auth);
        Optional<AuthData> result = authDAO.getAuth(token);
        assertTrue(result.isPresent(), "Auth token should be found");
        assertEquals("alice", result.get().username(), "Username should match");
    }

    @Test
    public void getAuth_notFound_returnsEmpty() throws DataAccessException {
        Optional<AuthData> result = authDAO.getAuth("nonexistent");
        assertTrue(result.isEmpty(), "Nonexistent token should return empty");
    }

    @Test
    public void deleteAuth_removesToken() throws DataAccessException {
        String token = UUID.randomUUID().toString();
        authDAO.createAuth(new AuthData(token, "bob"));
        authDAO.deleteAuth(token);
        Optional<AuthData> result = authDAO.getAuth(token);
        assertTrue(result.isEmpty(), "Deleted token should not be found");
    }

    @Test
    public void clear_removesAllTokens() throws DataAccessException {
        authDAO.createAuth(new AuthData(UUID.randomUUID().toString(), "u1"));
        authDAO.createAuth(new AuthData(UUID.randomUUID().toString(), "u2"));
        authDAO.clear();
        Optional<AuthData> result1 = authDAO.getAuth("anything");
        assertTrue(result1.isEmpty(), "Auth table should be empty after clear()");
    }
}
