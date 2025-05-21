package server;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.*;
import service.*;
import spark.Request;
import spark.Response;
import spark.Spark;
import com.google.gson.Gson;

public class Server {
    MemoryDataAccess dataAccess = new MemoryDataAccess();
    private final UserService userService = new UserService(dataAccess);
    private final GameService gameService = new GameService(dataAccess);

    public Server() {
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

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

    private Object register(Request req, Response res) throws DataAccessException {
        var user = new Gson().fromJson(req.body(), RegisterRequest.class);
        var result = userService.register(user);
        if (result.message() != null) {
            if (result.message().equals("Error: already taken")) {
                res.status(403);
            } else if (result.message().equals("Error: bad request")) {
                res.status(400);
            }
        }
        return new Gson().toJson(result);
    }

    private Object clear(Request req, Response res) {
        userService.clear();
        //res.status(200);
        return "{}";
    }

    private Object login(Request req, Response res) {
        var user = new Gson().fromJson(req.body(), LoginRequest.class);
        var result = userService.login(user);
        if (result.message() != null) {
            if (result.message().equals("Error: unauthorized")) {
                res.status(401);
            } else if (result.message().equals("Error: bad request")) {
                res.status(400);
            }
        }
        return new Gson().toJson(result);
    }

    private Object logout(Request req, Response res) {
        var authToken = req.headers("authorization");
        var result = userService.logout(authToken);
        if (result.message() != null) {
            if (result.message().equals("Error: unauthorized")) {
                res.status(401);
            }
        }
        return new Gson().toJson(result);
    }

    private Object listGames(Request req, Response res) {
        var authToken = req.headers("authorization");
        var result = gameService.listGames(authToken);
        if (result.message() != null) {
            if (result.message().equals("Error: unauthorized")) {
                res.status(401);
            }
        }
        return new Gson().toJson(result);
    }

    private Object createGame(Request req, Response res) {
        var authToken = req.headers("authorization");
        var gameRequest = new Gson().fromJson(req.body(), NewGameRequest.class);
        var result = gameService.createGame(authToken, gameRequest);
        if (result.message() != null) {
            if (result.message().equals("Error: unauthorized")) {
                res.status(401);
            } else if (result.message().equals("Error: bad request")) {
                res.status(400);
            }
        }
        return new Gson().toJson(result);
    }

    private Object joinGame(Request req, Response res) {
        var authToken = req.headers("authorization");
        var joinRequest = new Gson().fromJson(req.body(), JoinRequest.class);
        var result = gameService.joinGame(authToken, joinRequest);
        if (result.message() != null) {
            if (result.message().equals("Error: already taken")) {
                res.status(403);
            } else if (result.message().equals("Error: bad request")) {
                res.status(400);
            } else if (result.message().equals("Error: unauthorized")) {
                res.status(401);
            }
        }
        return new Gson().toJson(result);
    }
}
