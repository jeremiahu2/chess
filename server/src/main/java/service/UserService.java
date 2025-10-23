package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.UserData;
import model.AuthData;
import service.requests.RegisterRequest;
import service.requests.LoginRequest;
import service.results.RegisterResult;
import service.results.LoginResult;

import java.util.Optional;

public class UserService {
    private final DataAccess dao;

    public UserService(DataAccess dao) {
        this.dao = dao;
    }

    public RegisterResult register(RegisterRequest req) throws DataAccessException {
        if (req == null || req.username() == null || req.password() == null || req.email() == null)
            throw new DataAccessException("bad request");

        // username taken?
        if (dao.getUser(req.username()).isPresent())
            throw new DataAccessException("already taken");

        UserData u = new UserData(req.username(), req.password(), req.email());
        dao.createUser(u);

        String token = TokenUtil.generateToken();
        AuthData auth = new AuthData(token, req.username());
        dao.createAuth(auth);

        return new RegisterResult(req.username(), token);
    }

    public LoginResult login(LoginRequest req) throws DataAccessException {
        if (req == null || req.username() == null || req.password() == null)
            throw new DataAccessException("bad request");

        Optional<UserData> uOpt = dao.getUser(req.username());
        if (uOpt.isEmpty()) throw new DataAccessException("unauthorized");

        UserData u = uOpt.get();
        if (!u.password().equals(req.password())) throw new DataAccessException("unauthorized");

        String token = TokenUtil.generateToken();
        AuthData a = new AuthData(token, u.username());
        dao.createAuth(a);

        return new LoginResult(u.username(), token);
    }

    public void logout(String authToken) throws DataAccessException {
        if (authToken == null) throw new DataAccessException("unauthorized");
        dao.deleteAuth(authToken);
    }
}
