package client;

import chess.ChessGame;
import model.GameData;
import server.EvalResult;
import websocket.messages.ServerMessage;

import javax.management.Notification;
import java.util.Objects;
import java.util.Scanner;

public class Repl implements NotificationHandler{
    private final PreloginClient preLoginClient;
    private final String serverURL;
    private String authToken = null;
    private String username = null;
    private GameData game = null;
    private ChessGame.TeamColor color = null;

    public Repl(String serverURL) {
        preLoginClient = new PreloginClient(serverURL);
        this.serverURL = serverURL;
    }

    public void run() {
        while (true) {
            while (authToken == null) {
                preLogin();
            }
            while (authToken != null && game == null) {
                postLogin(authToken);
            }
            while (authToken != null && game != null) {
                gameplay(authToken, game, username);
                game = null;
            }
        }
    }

    public void preLogin() {
        System.out.print(preLoginClient.help().result());

        Scanner scanner = new Scanner(System.in);
        EvalResult result = new EvalResult("", null, null, null);
        while (!Objects.equals(result.result(), "quit") && authToken == null) {
            System.out.print("Chess >> ");
            String line = scanner.nextLine();

            try {
                result = preLoginClient.eval(line);
                System.out.print(result.result());
                authToken = result.authToken();
                username = result.username();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            System.out.println();
        }
        if (result.result() == "quit") {
            System.exit(0);
        }
    }

    public void postLogin(String authToken) {
        var postLoginClient = new PostloginClient(this.serverURL, authToken);
        System.out.print(PostloginClient.help().result());

        Scanner scanner = new Scanner(System.in);
        EvalResult result = new EvalResult("", null, null, null);
        while (result.result() != "quit" && !result.result().contains("Logged") && game == null) {
            System.out.print("Chess >> ");
            String line = scanner.nextLine();

            try {
                result = postLoginClient.eval(line);
                System.out.println(result.result());
                game = result.game();
                if (result.authToken() != null) {
                    this.authToken = result.authToken().split(" ")[0];
                    if (result.authToken().split(" ").length > 1) {
                        color = result.authToken().split(" ")[1].contains("B") ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
                    } else if (game != null) {
                        color = ChessGame.TeamColor.WHITE;
                    }
                } else {
                    this.authToken = null;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            System.out.println();
        }
        this.username = result.username();
    }

    public void gameplay(String authToken, GameData game, String username) {
        var gameplayClient = new GameplayClient(serverURL, authToken, game, this, username);
        System.out.print(gameplayClient.redraw().result());
        System.out.print(gameplayClient.help().result());

        Scanner scanner = new Scanner(System.in);
        EvalResult result = new EvalResult("", null, null, null);
        while (this.game != null) {
            System.out.print("Chess >> ");
            String line = scanner.nextLine();
            try {
                result = gameplayClient.eval(line);
                System.out.println(result.result());
                this.game = result.game();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void notify(ServerMessage notification) {
        System.out.println(notification.getServerMessageType());
    }
}
