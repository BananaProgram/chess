package client;

import server.ServerFacade;
import service.LoginRequest;
import service.RegisterRequest;

import java.util.Arrays;

public class PreloginClient {
    //Use a switch to handle the different cases, create a method for each case. All methods should return a string
    private final ServerFacade server;

    public PreloginClient(String serverURL) {
        server = new ServerFacade(serverURL);
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "quit" -> "quit";
                case "login" -> login(params);
                case "register" -> register(params);
                default -> help();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String login(String[] params) {
        var login = server.login(new LoginRequest(params[0], params[1]));
        return String.format("Logged in as %s.", login.username());
    }

    private String register(String[] params) {
        var register = server.register(new RegisterRequest(params[0], params[1], params[2]));
        return String.format("Logged in as %s.", register.username());
    }

    String help() {
        return """
        Help: Shows information about what actions you can take.
        Quit: Exits the program.
        Login <USERNAME> <PASSWORD>: Logs you in with the provided credentials
        Register <USERNAME> <PASSWORD> <EMAIL>: Registers a new user with the provided credentials.
        """;
    }
}
