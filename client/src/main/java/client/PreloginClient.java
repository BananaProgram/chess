package client;

import server.EvalResult;
import server.ServerFacade;
import reqres.LoginRequest;
import reqres.RegisterRequest;

import java.util.Arrays;

public class PreloginClient {
    //Use a switch to handle the different cases, create a method for each case. All methods should return a string
    private final ServerFacade server;

    public PreloginClient(String serverURL) {
        server = new ServerFacade(serverURL);
    }

    public EvalResult eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "quit" -> new EvalResult("quit", null, null);
                case "login" -> login(params);
                case "register" -> register(params);
                default -> help();
            };
        } catch (Exception e) {
            return new EvalResult(e.getMessage(), null, null);
        }
    }

    private EvalResult login(String[] params) {
        if (params.length < 2) {
            return new EvalResult("Please enter a valid username and password", null, null);
        }
        var login = server.login(new LoginRequest(params[0], params[1]));
        if (login.message() != null) {
            return new EvalResult("Please enter a valid username and password", null, null);
        }
        return new EvalResult(String.format("Logged in as %s.", login.username()), login.authToken(), null);

    }

    private EvalResult register(String[] params) {
        if (params.length < 3) {
            return new EvalResult("Please enter a valid username, password, and email", null, null);
        }
        var register = server.register(new RegisterRequest(params[0], params[1], params[2]));
        if (register.message() != null) {
            return new EvalResult("Username already taken", null, null);
        }
        return new EvalResult(String.format("Logged in as %s.", register.username()), register.authToken(), null);
    }

    EvalResult help() {
        return new EvalResult("""
        Help: Shows information about what actions you can take.
        Quit: Exits the program.
        Login <USERNAME> <PASSWORD>: Logs you in with the provided credentials
        Register <USERNAME> <PASSWORD> <EMAIL>: Registers a new user with the provided credentials.
        """, null, null);
    }
}
