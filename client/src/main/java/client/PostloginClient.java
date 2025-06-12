package client;

import model.GameData;
import reqres.JoinRequest;
import reqres.NewGameRequest;
import server.EvalResult;
import server.ServerFacade;

import java.util.Arrays;
import java.util.HashMap;

public class PostloginClient {

    private final ServerFacade server;
    private static String authToken = "";
    private final HashMap<Integer, GameData> currentGames;

    public PostloginClient(String serverURL, String authToken) {
        server = new ServerFacade(serverURL);
        this.authToken = authToken;
        this.currentGames = new HashMap<>();
    }

    public EvalResult eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "logout" -> logout();
                case "create" -> create(params);
                case "list" -> list();
                case "play" -> play(params);
                case "observe" -> observe(params);
                default -> help();
            };
        } catch (Exception e) {
            return new EvalResult(e.getMessage(), authToken, null);
        }
    }

    private EvalResult logout() {
        server.logout(authToken);
        return new EvalResult("Logged out", null, null);
    }

    private EvalResult create(String[] params) {
        if (params.length < 1) {
            return new EvalResult("Please enter a name for the game", null, null);
        }
        server.create(new NewGameRequest(params[0]), authToken);
        return new EvalResult(String.format("Created new game: %s", params[0]), authToken, null);
    }

    private EvalResult list() {
        var rawGames = server.list(authToken);
        StringBuilder games = new StringBuilder("");
        int i = 1;
        for (GameData game : rawGames.games()) {
            games.append(i).append(" ").append(game.gameName()).append(" White: ").append(game.whiteUsername()).
                    append(" Black: ").append(game.blackUsername()).append("\n");
            currentGames.put(i, game);
            i++;
        }
        return new EvalResult(games.toString(), authToken, null);
    }

    private EvalResult play(String[] params) {
        if (params.length < 2) {
            return new EvalResult("Please enter a valid game number and color", null, null);
        }
        int num;
        try {
            num = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            return new EvalResult("Please enter a valid game number and color", null, null);
        }
        if (num > currentGames.size() || !(params[1].equalsIgnoreCase("WHITE") ||
                params[1].equalsIgnoreCase("BLACK")) || num < 1) {
            return new EvalResult("Please enter a valid game number and color", null, null);
        }
        GameData game = currentGames.get(num);
        var color = params[1].toUpperCase();
        if ((color.equals("WHITE") && game.whiteUsername() != null) || (color.equals("BLACK") && game.blackUsername() != null)) {
            return new EvalResult("Sorry, someone is already playing that color", null, null);
        }
        int id = game.gameID();
        server.join(new JoinRequest(color, id), authToken);

        return new EvalResult("Joining " + params[0], authToken + " " + color, game);
    }

    private EvalResult observe(String[] params) {
        if (params.length < 1) {
            return new EvalResult("Please enter a valid game number", null, null);
        }
        int num;
        try {
            num = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            return new EvalResult("Please enter a valid game number", null, null);
        }
        if (num > currentGames.size() || num < 1) {
            return new EvalResult("Please enter a valid game number", null, null);
        }
        GameData game = currentGames.get(Integer.parseInt(params[0]));
        return new EvalResult("Observing " + params[0], authToken, game);
    }

    static EvalResult help() {
        return new EvalResult("""
                Help: Shows information about what actions you can take.
                Logout:	Logs you out.
                Create <GAMENAME>: Creates a new game with the desired name.
                List Games:	Lists all the games that currently exist.
                Play <ID> <WHITE|BLACK>: Joins the game correlated with the given number.
                Observe <ID>: Joins that game as an observer.
                """, authToken, null);
    }
}
