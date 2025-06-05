package client;

import chess.ChessGame;
import model.GameData;
import server.EvalResult;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Objects;

import static ui.EscapeSequences.*;

public class GameplayClient {
    private final ServerFacade server;
    private final String authToken;
    private final GameData gameData;

    public GameplayClient(String serveruRL, String authToken, GameData gameData) {
        server = new ServerFacade(serveruRL);
        this.authToken = authToken;
        this.gameData = gameData;
    }

    public String drawStartBoard(ChessGame.TeamColor color) {
        System.out.println(color);
        String letters = Objects.equals(color, ChessGame.TeamColor.WHITE) ? "abcdefgh" : "hgfedcba";
        String numbers = Objects.equals(color, ChessGame.TeamColor.WHITE) ? "87654321" : "12345678";
        StringBuilder board = new StringBuilder();

        board.append(SET_BG_COLOR_BLACK + "    ").append(SET_TEXT_COLOR_WHITE).append(letters.charAt(0)).append("  ")
                .append(letters.charAt(1)).append("  ").append(letters.charAt(2)).append("  ").append(letters.charAt(3))
                .append("  ").append(letters.charAt(4)).append("  ").append(letters.charAt(5)).append("  ")
                .append(letters.charAt(6)).append("  ").append(letters.charAt(7)).append("    ");
        board.append("\n");
        board.append(" ").append(numbers.charAt(0)).append(" ").append(SET_BG_COLOR_LIGHT_GREY);
        if (Objects.equals(color, ChessGame.TeamColor.WHITE)) {
            board.append(SET_TEXT_COLOR_BLUE);
        } else {
            board.append(SET_TEXT_COLOR_RED);
        }
        board.append(" R ").append(SET_BG_COLOR_DARK_GREEN).append(" N ").append(SET_BG_COLOR_LIGHT_GREY).append(" B ")
                .append(SET_BG_COLOR_DARK_GREEN).append(" K ").append(SET_BG_COLOR_LIGHT_GREY).append(" Q ")
                .append(SET_BG_COLOR_DARK_GREEN).append(" B ").append(SET_BG_COLOR_LIGHT_GREY)
                .append(" N ").append(SET_BG_COLOR_DARK_GREEN).append(" R ");
        board.append(SET_BG_COLOR_BLACK).append(SET_TEXT_COLOR_WHITE).append(" ").append(numbers.charAt(0)).append(" ");
        board.append("\n");
        board.append(" ").append(numbers.charAt(1)).append(" ").append(SET_BG_COLOR_DARK_GREEN);
        if (Objects.equals(color, ChessGame.TeamColor.WHITE)) {
            board.append(SET_TEXT_COLOR_BLUE);
        } else {
            board.append(SET_TEXT_COLOR_RED);
        }
        board.append(" P ").append(SET_BG_COLOR_LIGHT_GREY).append(" P ").append(SET_BG_COLOR_DARK_GREEN).append(" P ")
                .append(SET_BG_COLOR_LIGHT_GREY).append(" P ").append(SET_BG_COLOR_DARK_GREEN).append(" P ")
                .append(SET_BG_COLOR_LIGHT_GREY).append(" P ").append(SET_BG_COLOR_DARK_GREEN)
                .append(" P ").append(SET_BG_COLOR_LIGHT_GREY).append(" P ");
        board.append(SET_BG_COLOR_BLACK).append(SET_TEXT_COLOR_WHITE).append(" ").append(numbers.charAt(1)).append(" ");
        board.append("\n");
        board.append(" ").append(numbers.charAt(2)).append(" ").append(SET_BG_COLOR_LIGHT_GREY);
        board.append("   ").append(SET_BG_COLOR_DARK_GREEN).append("   ").append(SET_BG_COLOR_LIGHT_GREY).append("   ")
                .append(SET_BG_COLOR_DARK_GREEN).append("   ").append(SET_BG_COLOR_LIGHT_GREY).append("   ").append(SET_BG_COLOR_DARK_GREEN)
                .append("   ").append(SET_BG_COLOR_LIGHT_GREY).append("   ").append(SET_BG_COLOR_DARK_GREEN).append("   ");
        board.append(SET_BG_COLOR_BLACK).append(SET_TEXT_COLOR_WHITE).append(" ").append(numbers.charAt(2)).append(" ");
        board.append("\n");
        board.append(" ").append(numbers.charAt(3)).append(" ").append(SET_BG_COLOR_DARK_GREEN);
        board.append("   ").append(SET_BG_COLOR_LIGHT_GREY).append("   ").append(SET_BG_COLOR_DARK_GREEN).append("   ")
                .append(SET_BG_COLOR_LIGHT_GREY).append("   ").append(SET_BG_COLOR_DARK_GREEN).append("   ").append(SET_BG_COLOR_LIGHT_GREY)
                .append("   ").append(SET_BG_COLOR_DARK_GREEN).append("   ").append(SET_BG_COLOR_LIGHT_GREY).append("   ");
        board.append(SET_BG_COLOR_BLACK).append(SET_TEXT_COLOR_WHITE).append(" ").append(numbers.charAt(3)).append(" ");
        board.append("\n");
        board.append(" ").append(numbers.charAt(4)).append(" ").append(SET_BG_COLOR_LIGHT_GREY);
        board.append("   ").append(SET_BG_COLOR_DARK_GREEN).append("   ").append(SET_BG_COLOR_LIGHT_GREY).append("   ")
                .append(SET_BG_COLOR_DARK_GREEN).append("   ").append(SET_BG_COLOR_LIGHT_GREY).append("   ").append(SET_BG_COLOR_DARK_GREEN)
                .append("   ").append(SET_BG_COLOR_LIGHT_GREY).append("   ").append(SET_BG_COLOR_DARK_GREEN).append("   ");
        board.append(SET_BG_COLOR_BLACK).append(SET_TEXT_COLOR_WHITE).append(" ").append(numbers.charAt(4)).append(" ");
        board.append("\n");
        board.append(" ").append(numbers.charAt(5)).append(" ").append(SET_BG_COLOR_DARK_GREEN);
        board.append("   ").append(SET_BG_COLOR_LIGHT_GREY).append("   ").append(SET_BG_COLOR_DARK_GREEN).append("   ")
                .append(SET_BG_COLOR_LIGHT_GREY).append("   ").append(SET_BG_COLOR_DARK_GREEN).append("   ").append(SET_BG_COLOR_LIGHT_GREY)
                .append("   ").append(SET_BG_COLOR_DARK_GREEN).append("   ").append(SET_BG_COLOR_LIGHT_GREY).append("   ");
        board.append(SET_BG_COLOR_BLACK).append(SET_TEXT_COLOR_WHITE).append(" ").append(numbers.charAt(5)).append(" ");
        board.append("\n");
        board.append(" ").append(numbers.charAt(6)).append(" ").append(SET_BG_COLOR_LIGHT_GREY);
        if (Objects.equals(color, ChessGame.TeamColor.WHITE)) {
            board.append(SET_TEXT_COLOR_RED);
        } else {
            board.append(SET_TEXT_COLOR_BLUE);
        }
        board.append(" P ").append(SET_BG_COLOR_DARK_GREEN).append(" P ").append(SET_BG_COLOR_LIGHT_GREY).append(" P ")
                .append(SET_BG_COLOR_DARK_GREEN).append(" P ").append(SET_BG_COLOR_LIGHT_GREY).append(" P ")
                .append(SET_BG_COLOR_DARK_GREEN).append(" P ").append(SET_BG_COLOR_LIGHT_GREY)
                .append(" P ").append(SET_BG_COLOR_DARK_GREEN).append(" P ");
        board.append(SET_BG_COLOR_BLACK).append(SET_TEXT_COLOR_WHITE).append(" ").append(numbers.charAt(6)).append(" ");
        board.append("\n");
        board.append(" ").append(numbers.charAt(7)).append(" ").append(SET_BG_COLOR_DARK_GREEN);
        if (Objects.equals(color, ChessGame.TeamColor.WHITE)) {
            board.append(SET_TEXT_COLOR_RED);
        } else {
            board.append(SET_TEXT_COLOR_BLUE);
        }
        board.append(" R ").append(SET_BG_COLOR_LIGHT_GREY).append(" N ").append(SET_BG_COLOR_DARK_GREEN).append(" B ")
                .append(SET_BG_COLOR_LIGHT_GREY).append(" K ").append(SET_BG_COLOR_DARK_GREEN).append(" Q ")
                .append(SET_BG_COLOR_LIGHT_GREY).append(" B ").append(SET_BG_COLOR_DARK_GREEN)
                .append(" N ").append(SET_BG_COLOR_LIGHT_GREY).append(" R ");
        board.append(SET_BG_COLOR_BLACK).append(SET_TEXT_COLOR_WHITE).append(" ").append(numbers.charAt(7)).append(" ");
        board.append("\n");
        board.append(SET_BG_COLOR_BLACK + "    ").append(SET_TEXT_COLOR_WHITE).append(letters.charAt(0)).append("  ").append(letters.charAt(1))
                .append("  ").append(letters.charAt(2)).append("  ").append(letters.charAt(3)).append("  ").append(letters.charAt(4)).append("  ")
                .append(letters.charAt(5)).append("  ").append(letters.charAt(6)).append("  ").append(letters.charAt(7)).append("    ");

        return board.toString();
    }
}
