package server;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.*;
import service.RegisterRequest;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Spark;
import com.google.gson.Gson;

public class Server {
    private final UserService userService = new UserService(new MemoryDataAccess());

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
        return new Gson().toJson(result);
    }

    private Object clear(Request req, Response res) {
        userService.clear();
        res.status(200);
        return "{}";
    }
}
