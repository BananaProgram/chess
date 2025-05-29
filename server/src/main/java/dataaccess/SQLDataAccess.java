package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.NewGameRequest;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static dataaccess.DatabaseManager.createDatabase;
import static dataaccess.DatabaseManager.getConnection;

public class SQLDataAccess implements DataAccess{

    public static void configureDatabase() throws SQLException {
        try {
            createDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        try (var conn = getConnection()) {
            conn.setCatalog("chess");

            var createUserTable = """
            CREATE TABLE  IF NOT EXISTS users (
                username VARCHAR(255) NOT NULL,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(255) NOT NULL,
                PRIMARY KEY (username)
            )""";

            var createAuthTable = """
            CREATE TABLE  IF NOT EXISTS auth (
                token VARCHAR(255) NOT NULL,
                username VARCHAR(255) NOT NULL,
                PRIMARY KEY (token)
            )""";

            var createGameTable = """
            CREATE TABLE  IF NOT EXISTS games (
                id INT NOT NULL AUTO_INCREMENT,
                whiteusername VARCHAR(255) DEFAULT NULL,
                blackusername VARCHAR(255) DEFAULT NULL,
                gamename VARCHAR(255) NOT NULL UNIQUE,
                game longtext NOT NULL,
                PRIMARY KEY (id)
            )""";


            try (var createTableStatement = conn.prepareStatement(createUserTable)) {
                createTableStatement.executeUpdate();
            }

            try (var createTableStatement = conn.prepareStatement(createAuthTable)) {
                createTableStatement.executeUpdate();
            }

            try (var createTableStatement = conn.prepareStatement(createGameTable)) {
                createTableStatement.executeUpdate();
            }

        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

    }

    public void clear(){
        System.out.println("Reached the data access file");
        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("TRUNCATE TABLE users")) {
                preparedStatement.execute();
            } catch (SQLException e) {
                System.out.println("SQL Exception!");
                throw new RuntimeException(e);
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }

        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("TRUNCATE TABLE auth")) {
                preparedStatement.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }

        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("TRUNCATE TABLE games")) {
                preparedStatement.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public AuthData addUser(UserData user) {
        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("INSERT INTO users (username, password, email) VALUES (?, ?, ?)")) {
                preparedStatement.setString(1, user.username());
                preparedStatement.setString(2, user.password());
                preparedStatement.setString(3, user.email());

                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }

        String token = generateToken();
        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("INSERT INTO auth (token, username) VALUES (?, ?)")) {
                preparedStatement.setString(1, token);
                preparedStatement.setString(2, user.username());

                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }

        return new AuthData(token, user.username());
    }

    public UserData getUser(String username){
        UserData user = null;

        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT password, email FROM users WHERE username=?")) {
                preparedStatement.setString(1, username);
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        var password = rs.getString("password");
                        var email = rs.getString("email");

                        user = new UserData(username, password, email);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }

        return user;
    }

    public AuthData createAuth(String username) {
        String token = generateToken();

        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("INSERT INTO auth (token, username) VALUES (?, ?)")) {
                preparedStatement.setString(1, token);
                preparedStatement.setString(2, username);

                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }

        return new AuthData(token, username);
    }

    public void deleteAuth(String authData) {
        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("DELETE FROM auth WHERE token=?")) {
                preparedStatement.setString(1, authData);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public List<GameData> listGames() {
        List<GameData> gameList = new ArrayList<>();

        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT game FROM games")) {
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        var json = rs.getString("game");
                        var game = new Gson().fromJson(json, GameData.class);

                        gameList.add(game);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }

        return gameList;
    }

    public int addGame(NewGameRequest request) {
        int gameID = 0;

        try (var conn = getConnection()) {
            System.out.println("Connected!");
            try (var preparedStatement = conn.prepareStatement("INSERT INTO games (gamename, game) VALUES (?, ?)",
                    java.sql.Statement.RETURN_GENERATED_KEYS)) {
                var game = new ChessGame();
                preparedStatement.setString(1, request.gameName());
                preparedStatement.setString(2, new Gson().toJson(game));

                preparedStatement.executeUpdate();
                try (var rs = preparedStatement.getGeneratedKeys()) {
                    if (rs.next()) {
                        gameID = rs.getInt(1); // ID of inserted game
                    }
                }
            } catch (SQLException e) {
                System.out.println("SQL Error!" + e.getMessage());
                throw new RuntimeException(e);
            }

        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }

        return gameID;
    }

    public String findUser(String authToken) {
        UserData user = null;
        String username = null;

        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT username FROM auth WHERE token=?")) {
                preparedStatement.setString(1, authToken);
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        username = rs.getString("username");
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }

        return username;
    }

    public void joinGame(int gameID, String username, String color) {
        try (var conn = getConnection()) {
            if (Objects.equals(color, "WHITE")) {
                try (var preparedStatement = conn.prepareStatement("UPDATE games SET whiteusername=? WHERE id=?")) {
                    preparedStatement.setString(1, username);
                    preparedStatement.setInt(2, gameID);

                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try (var preparedStatement = conn.prepareStatement("UPDATE games SET blackusername=? WHERE id=?")) {
                    preparedStatement.setString(1, username);
                    preparedStatement.setInt(2, gameID);

                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }

    }

    public GameData findGame(int gameID) {
        GameData gameData = null;

        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT whiteusername, blackusername, gamename, game FROM games WHERE id=?")) {
                preparedStatement.setInt(1, gameID);
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        var whiteusername = rs.getString("whiteusername");
                        var blackusername = rs.getString("blackusername");
                        var gamename = rs.getString("gamename");
                        var game = new Gson().fromJson(rs.getString("game"), ChessGame.class);

                        gameData = new GameData(gameID, whiteusername, blackusername, gamename, game);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }

        return gameData;
    }
}
