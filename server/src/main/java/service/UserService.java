package service;

import dataaccess.*;
import model.*;
import org.mindrot.jbcrypt.BCrypt;
import service.requests.*;
import service.results.*;
import java.util.Optional;

public class UserService {
    private final DataAccess dao;

    public UserService(DataAccess dao) {
        this.dao = dao;
    }

    public RegisterResult register(RegisterRequest req) throws DataAccessException {
        if (req == null || req.username() == null || req.password() == null || req.email() == null) {
            throw new DataAccessException("bad request");
        }
        if (dao.getUser(req.username()).isPresent()) {
            throw new DataAccessException("already taken");
        }
        String hashedPassword = BCrypt.hashpw(req.password(), BCrypt.gensalt());
        UserData u = new UserData(req.username(), hashedPassword, req.email());
        dao.createUser(u);
        String token = TokenUtil.generateToken();
        AuthData auth = new AuthData(token, req.username());
        dao.createAuth(auth);
        return new RegisterResult(req.username(), token);
    }

    public LoginResult login(LoginRequest req) throws DataAccessException {
        if (req == null || req.username() == null || req.password() == null) {
            throw new DataAccessException("bad request");
        }
        Optional<UserData> uOpt = dao.getUser(req.username());
        if (uOpt.isEmpty()) {
            throw new DataAccessException("unauthorized");
        }
        UserData u = uOpt.get();
        if (!BCrypt.checkpw(req.password(), u.password())) {
            throw new DataAccessException("unauthorized");
        }
        String token = TokenUtil.generateToken();
        AuthData a = new AuthData(token, u.username());
        dao.createAuth(a);
        return new LoginResult(u.username(), token);
    }

    public void logout(String authToken) throws DataAccessException {
        if (authToken == null) {
            throw new DataAccessException("unauthorized");
        }
        dao.deleteAuth(authToken);
    }
}
