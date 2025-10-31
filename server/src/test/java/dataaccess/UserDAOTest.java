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
    public void createAndGetUser_success() throws DataAccessException {
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
    public void createUser_duplicate_throwsException() throws DataAccessException {
        UserData user = new UserData("bob", "pass", "b@b.com");
        userDAO.createUser(user);
        assertThrows(DataAccessException.class, () -> userDAO.createUser(user),
                "Inserting a duplicate username should throw DataAccessException");
    }

    @Test
    public void clear_removesAllUsers() throws DataAccessException {
        userDAO.createUser(new UserData("chris", "pw", "c@c.com"));
        userDAO.clear();

        Optional<UserData> result = userDAO.getUser("chris");
        assertTrue(result.isEmpty(), "All users should be removed after clear()");
    }
}
