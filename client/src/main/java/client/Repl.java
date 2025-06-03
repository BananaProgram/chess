package client;

import java.util.Scanner;

public class Repl {
    //just take in the input and use a try bracket to attempt to use Client to evaluate it. repl but e is handled separately
    //Client will return a result, print out that result
    //Switch between different clients (there should be 3) as necessary
    private final PreloginClient client;

    public Repl(String serverURL) {
        client = new PreloginClient(serverURL);
    }

    public void run() {
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (result != "quit") {
            System.out.print("Chess >> ");
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(result);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            System.out.println();
        }
    }
}
