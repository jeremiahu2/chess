package dataaccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseManager {
    private static String databaseName;
    private static String dbUsername;
    private static String dbPassword;
    private static String connectionUrl;

    static {
        loadPropertiesFromResources();
    }

    public static void initialize() throws DataAccessException {
        createDatabase();
        createTables();
    }

    static void createDatabase() throws DataAccessException {
        var statement = "CREATE DATABASE IF NOT EXISTS " + databaseName;
        try (var conn = DriverManager.getConnection(connectionUrl.replace("/" + databaseName, ""), dbUsername, dbPassword);
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("failed to create database", ex);
        }
    }

    private static void createTables() throws DataAccessException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS user (
                    username VARCHAR(255) PRIMARY KEY,
                    password VARCHAR(255) NOT NULL,
                    email VARCHAR(255) NOT NULL
                )
            """);
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS auth_token (
                    token VARCHAR(255) PRIMARY KEY,
                    username VARCHAR(255) NOT NULL,
                    FOREIGN KEY (username) REFERENCES user(username)
                )
            """);
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS game (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    gameName VARCHAR(255) NOT NULL,
                    whiteUsername VARCHAR(255),
                    blackUsername VARCHAR(255),
                    gameState TEXT,
                    FOREIGN KEY (whiteUsername) REFERENCES user(username),
                    FOREIGN KEY (blackUsername) REFERENCES user(username)
                 )
            """);
        } catch (SQLException e) {
            throw new DataAccessException("Error creating tables", e);
        }
    }

    static Connection getConnection() throws DataAccessException {
        try {
            return DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
        } catch (SQLException ex) {
            throw new DataAccessException("failed to get connection", ex);
        }
    }

    private static void loadPropertiesFromResources() {
        try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
            if (propStream == null) {
                throw new Exception("Unable to load db.properties");
            }
            Properties props = new Properties();
            props.load(propStream);
            loadProperties(props);
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties", ex);
        }
    }

    private static void loadProperties(Properties props) {
        databaseName = props.getProperty("db.name");
        dbUsername = props.getProperty("db.user");
        dbPassword = props.getProperty("db.password");

        var host = props.getProperty("db.host");
        var port = Integer.parseInt(props.getProperty("db.port"));
        connectionUrl = String.format("jdbc:mysql://%s:%d/%s", host, port, databaseName);
    }
}
