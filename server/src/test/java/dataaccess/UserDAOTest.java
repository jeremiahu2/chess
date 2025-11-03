package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {

    private UserDAO userDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        userDAO = new UserDAO();
        userDAO.clear();
    }

    @Test
    public void createAndGetUserSuccess() throws DataAccessException {
        UserData user = new UserData("alice", "password123", "alice@example.com");
        userDAO.createUser(user);
        Optional<UserData> retrieved = userDAO.getUser("alice");
        assertTrue(retrieved.isPresent());
        UserData result = retrieved.get();
        assertEquals("alice", result.username());
        assertEquals("alice@example.com", result.email());
        assertTrue(BCrypt.checkpw("password123", result.password()),
                "Stored hash should match the original password");
    }

    @Test
    public void createUserDuplicateThrowsException() throws DataAccessException {
        UserData user = new UserData("bob", "pass", "b@b.com");
        userDAO.createUser(user);
        assertThrows(DataAccessException.class, () -> userDAO.createUser(user),
                "Inserting a duplicate username should throw DataAccessException");
    }

    @Test
    public void clearRemovesAllUsers() throws DataAccessException {
        userDAO.createUser(new UserData("chris", "pw", "c@c.com"));
        userDAO.clear();

        Optional<UserData> result = userDAO.getUser("chris");
        assertTrue(result.isEmpty(), "All users should be removed after clear()");
    }

    @Test
    public void getUserNotFoundReturnsEmpty() throws DataAccessException {
        Optional<UserData> result = userDAO.getUser("nonexistent");
        assertTrue(result.isEmpty(), "Getting a nonexistent user should return empty");
    }

    @Test
    public void clearEmptyTable() throws DataAccessException {
        userDAO.clear();
        assertDoesNotThrow(() -> userDAO.clear(), "Clearing an already empty table should not throw");
    }
}
