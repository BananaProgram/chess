package server;

import dataaccess.DataAccessException;
import dataaccess.SQLDataAccess;
import reqres.JoinRequest;
import reqres.LoginRequest;
import reqres.NewGameRequest;
import reqres.RegisterRequest;
import service.*;
import spark.Request;
import spark.Response;
import spark.Spark;
import com.google.gson.Gson;

import java.sql.SQLException;

public class Server {
    SQLDataAccess dataAccess = new SQLDataAccess();
    private final UserService userService = new UserService(dataAccess);
    private final GameService gameService = new GameService(dataAccess);

    public Server() {
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        try {
            SQLDataAccess.configureDatabase(true);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to configure database", e);
        }

        Spark.webSocket("/ws", WSHandler.class);

        Spark.post("/user", this::register);
        Spark.delete("/db", this::clear);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void handleErrors(Response res, String message) {
        if (message != null) {
            if (message.equals("Error: already taken")) {
                res.status(403);
            } else if (message.equals("Error: bad request")) {
                res.status(400);
            } else if (message.equals("Error: unauthorized")) {
                res.status(401);
            } else if (message.equals("Internal Server Error")) {
                res.status(500);
            }
        }
    }

    private Object register(Request req, Response res) throws DataAccessException {
        var user = new Gson().fromJson(req.body(), RegisterRequest.class);
        var result = userService.register(user);
        handleErrors(res, result.message());
        return new Gson().toJson(result);
    }

    private Object clear(Request req, Response res) {
        var result = userService.clear();
        handleErrors(res, result.message());
        return new Gson().toJson(result);
    }

    private Object login(Request req, Response res) {
        var user = new Gson().fromJson(req.body(), LoginRequest.class);
        var result = userService.login(user);
        handleErrors(res, result.message());
        return new Gson().toJson(result);
    }

    private Object logout(Request req, Response res) {
        var authToken = req.headers("authorization");
        var result = userService.logout(authToken);
        handleErrors(res, result.message());
        return new Gson().toJson(result);
    }

    private Object listGames(Request req, Response res) {
        var authToken = req.headers("authorization");
        var result = gameService.listGames(authToken);
        handleErrors(res, result.message());
        return new Gson().toJson(result);
    }

    private Object createGame(Request req, Response res) {
        var authToken = req.headers("authorization");
        var gameRequest = new Gson().fromJson(req.body(), NewGameRequest.class);
        var result = gameService.createGame(authToken, gameRequest);
        handleErrors(res, result.message());
        return new Gson().toJson(result);
    }

    private Object joinGame(Request req, Response res) {
        var authToken = req.headers("authorization");
        var joinRequest = new Gson().fromJson(req.body(), JoinRequest.class);
        var result = gameService.joinGame(authToken, joinRequest);
        handleErrors(res, result.message());
        return new Gson().toJson(result);
    }
}
