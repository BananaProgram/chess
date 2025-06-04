package client;

import model.GameData;
import server.EvalResult;

import java.util.Objects;
import java.util.Scanner;

public class Repl {
    //just take in the input and use a try bracket to attempt to use Client to evaluate it. repl but e is handled separately
    //Client will return a result, print out that result
    //Switch between different clients (there should be 3) as necessary
    private final PreloginClient preLoginClient;
    private final String serverURL;
    private String authToken = null;
    private GameData game = null;

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
                System.out.println("Implement this Elena. You know it needs to happen >:(");
                game = null;
            }
        }
    }

    public void preLogin() {
        System.out.print(preLoginClient.help().result());

        Scanner scanner = new Scanner(System.in);
        EvalResult result = new EvalResult("", null, null);
        while (!Objects.equals(result.result(), "quit") && authToken == null) {
            System.out.print("Chess >> ");
            String line = scanner.nextLine();

            try {
                result = preLoginClient.eval(line);
                System.out.print(result.result());
                authToken = result.authToken();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            System.out.println();
        }
    }

    public void postLogin(String authToken) {
        var postLoginClient = new PostloginClient(this.serverURL, authToken);
        System.out.print(PostloginClient.help().result());

        Scanner scanner = new Scanner(System.in);
        EvalResult result = new EvalResult("", null, null);
        while (result.result() != "quit" && !result.result().contains("Logged") && game == null) {
            System.out.print("Chess >> ");
            String line = scanner.nextLine();

            try {
                result = postLoginClient.eval(line);
                System.out.println(result.result());
                game = result.game();
                authToken = result.authToken();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            System.out.println();
        }
    }

    public String gameplay(String authToken, GameData game) {
        return "Gameplay not yet implemented. Try again next week.";
    }
}
