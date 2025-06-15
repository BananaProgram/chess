package client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;
import server.EvalResult;
import server.ServerFacade;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import static ui.EscapeSequences.*;

public class GameplayClient {
    private final ServerFacade server;
    private final String authToken;
    private final GameData gameData;
    private final WSClient ws;
    private final ChessGame.TeamColor color;
    private final String username;
    private final boolean observer;

    public GameplayClient(String serveruRL, String authToken, GameData gameData, NotificationHandler notificationHandler, String username) {
        server = new ServerFacade(serveruRL);
        this.authToken = authToken;
        this.gameData = gameData;
        this.username = username;

        if (gameData.whiteUsername() != null && gameData.whiteUsername().equals(username)) {
            color = ChessGame.TeamColor.WHITE;
            observer = false;
        } else if (gameData.blackUsername() != null && gameData.blackUsername().equals(username)){
            color = ChessGame.TeamColor.BLACK;
            observer = false;
        } else {
            color = ChessGame.TeamColor.BLACK;
            observer = true;
        }

        try {
            ws = new WSClient(notificationHandler);
            if (observer) {
                ws.observe(username);
            } else {
                ws.joinGame(username, color.toString());
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public EvalResult eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "redraw" -> redraw();
                case "leave" -> leave();
                case "makemove" -> makeMove(params);
                case "resign" -> resign();
                //case "highlight" ->
                default -> help();
            };
        } catch (Exception e) {
            return new EvalResult(e.getMessage(), null, null, null);
        }
    }

    public EvalResult redraw() {
        String letters = Objects.equals(color, ChessGame.TeamColor.WHITE) ? "abcdefgh" : "hgfedcba";
        String numbers = Objects.equals(color, ChessGame.TeamColor.WHITE) ? "87654321" : "12345678";
        StringBuilder pieces = new StringBuilder();
        StringBuilder board = new StringBuilder();

        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                String pieceLetter = "";
                if (gameData.game().getBoard().getPiece(new ChessPosition(i, j)) == null) {
                    pieceLetter = " ";
                } else {
                    ChessPiece piece = gameData.game().getBoard().getPiece(new ChessPosition(i, j));
                    if (piece.getPieceType().equals(ChessPiece.PieceType.KING)) {
                        pieceLetter = "k";
                    } else if (piece.getPieceType().equals(ChessPiece.PieceType.QUEEN)) {
                        pieceLetter = "q";
                    } else if (piece.getPieceType().equals(ChessPiece.PieceType.BISHOP)) {
                        pieceLetter = "b";
                    } else if (piece.getPieceType().equals(ChessPiece.PieceType.KNIGHT)) {
                        pieceLetter = "n";
                    } else if (piece.getPieceType().equals(ChessPiece.PieceType.ROOK)) {
                        pieceLetter = "r";
                    } else if (piece.getPieceType().equals(ChessPiece.PieceType.PAWN)) {
                        pieceLetter = "p";
                    }
                    if (piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
                        pieceLetter = pieceLetter.toUpperCase();
                    }
                }
                pieces.append(pieceLetter);
            }
        }


        board.append(SET_BG_COLOR_BLACK + "    ").append(SET_TEXT_COLOR_WHITE).append(letters.charAt(0)).append("  ")
                .append(letters.charAt(1)).append("  ").append(letters.charAt(2)).append("  ").append(letters.charAt(3))
                .append("  ").append(letters.charAt(4)).append("  ").append(letters.charAt(5)).append("  ")
                .append(letters.charAt(6)).append("  ").append(letters.charAt(7)).append("    ");
        board.append(RESET_BG_COLOR).append("\n").append(SET_BG_COLOR_BLACK);

        for (int n = 0; n < 4; n++) {
            board.append(" ").append(numbers.charAt(0)).append(" ").append(SET_BG_COLOR_LIGHT_GREY);
            for (int m = 0; m < 4; m++) {
                board.append(printPiece(pieces)).append(SET_BG_COLOR_DARK_GREEN);
                board.append(printPiece(pieces)).append(SET_BG_COLOR_LIGHT_GREY);
            }
            board.append(SET_BG_COLOR_BLACK).append(SET_TEXT_COLOR_WHITE).append(" ").append(numbers.charAt(0)).append(" ");
            numbers = numbers.substring(1);
            board.append(RESET_BG_COLOR).append("\n").append(SET_BG_COLOR_BLACK);
            board.append(" ").append(numbers.charAt(0)).append(" ").append(SET_BG_COLOR_DARK_GREEN);
            for (int m = 0; m < 4; m++) {
                board.append(printPiece(pieces)).append(SET_BG_COLOR_LIGHT_GREY);
                board.append(printPiece(pieces)).append(SET_BG_COLOR_DARK_GREEN);
            }
            board.append(SET_BG_COLOR_BLACK).append(SET_TEXT_COLOR_WHITE).append(" ").append(numbers.charAt(0)).append(" ");
            board.append(RESET_BG_COLOR).append("\n").append(SET_BG_COLOR_BLACK);
            numbers = numbers.substring(1);
        }

        board.append(SET_BG_COLOR_BLACK + "    ").append(SET_TEXT_COLOR_WHITE).append(letters.charAt(0)).append("  ")
                .append(letters.charAt(1)).append("  ").append(letters.charAt(2)).append("  ").append(letters.charAt(3))
                .append("  ").append(letters.charAt(4)).append("  ").append(letters.charAt(5)).append("  ")
                .append(letters.charAt(6)).append("  ").append(letters.charAt(7)).append("    ");
        board.append(RESET_BG_COLOR).append("\n");

        return new EvalResult(board.toString(), authToken, gameData, username);
    }

    private String printPiece(StringBuilder pieces) {
        StringBuilder square = new StringBuilder();
        String color = !Objects.equals(pieces.substring(0, 1), pieces.substring(0, 1).toUpperCase()) ? SET_TEXT_COLOR_BLUE : SET_TEXT_COLOR_RED;
        square.append(color).append(" ").append(pieces.substring(0, 1).toUpperCase()).append(" ");
        pieces.deleteCharAt(0);
        return square.toString();
    }

    public EvalResult leave() throws IOException {
        ws.leaveGame(username);
        return new EvalResult("Leaving game", authToken, null, username);
    }

    public EvalResult makeMove(String[] params) {
        ws.makeMove(authToken, gameData.gameID(), parseMove(params));
        redraw();
        return new EvalResult("Move executed.", authToken, gameData, username);
    }

    private ChessMove parseMove(String[] rawMove) {
        ChessPosition start;
        ChessPosition end;
        String letters = "abcdefgh";
        start = new ChessPosition(letters.indexOf(rawMove[0].charAt(0)), rawMove[0].charAt(1));
        end = new ChessPosition(letters.indexOf(rawMove[2].charAt(0)), rawMove[2].charAt(1));
        return new ChessMove(start, end, null);
    }

    public EvalResult resign() {
        ws.resign(authToken, gameData.gameID());
        return new EvalResult("Resigning from game", authToken, null, username);
    }

    public EvalResult help() {
        return new EvalResult("""
                Help: Shows information about what actions you can take.
                Redraw: Redraws the board.
                Leave: Leaves the game.
                Makemove <MOVE>: Makes the provided move.
                Resign: Forfeits the game and leaves.
                Hightlight <POSITION>: Highlights all valid moves for the piece at the position provided.
                """, authToken, gameData, username);
    }
}
