package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class Auth_TokenDAOTest {

    private Auth_TokenDAO authDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        authDAO = new Auth_TokenDAO();
        authDAO.clear();
    }

    @Test
    public void createAndGetAuth_success() throws DataAccessException {
        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, "alice");
        authDAO.createAuth(auth);
        Optional<AuthData> result = authDAO.getAuth(token);
        assertTrue(result.isPresent());
        assertEquals("alice", result.get().username());
    }

    @Test
    public void getAuth_notFound_returnsEmpty() throws DataAccessException {
        Optional<AuthData> result = authDAO.getAuth("nonexistent");
        assertTrue(result.isEmpty());
    }

    @Test
    public void deleteAuth_removesToken() throws DataAccessException {
        String token = UUID.randomUUID().toString();
        authDAO.createAuth(new AuthData(token, "bob"));
        authDAO.deleteAuth(token);
        Optional<AuthData> result = authDAO.getAuth(token);
        assertTrue(result.isEmpty());
    }

    @Test
    public void clear_removesAllTokens() throws DataAccessException {
        authDAO.createAuth(new AuthData(UUID.randomUUID().toString(), "u1"));
        authDAO.createAuth(new AuthData(UUID.randomUUID().toString(), "u2"));
        authDAO.clear();
        Optional<AuthData> result = authDAO.getAuth("anything");
        assertTrue(result.isEmpty());
    }
}
