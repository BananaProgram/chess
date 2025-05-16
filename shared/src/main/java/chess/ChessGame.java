package chess;

import java.util.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard board = new ChessBoard();


    public ChessGame() {
        teamTurn = TeamColor.WHITE;
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
        //System.out.println("Changed turn!");
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {return null;}
        else {
            Collection<ChessMove> possibleMoves = piece.pieceMoves(board, startPosition);
            Collection<ChessMove> validMoves = new Vector<>();
            TeamColor movingTeam = piece.getTeamColor();
            for (ChessMove move : possibleMoves) {
                ChessPiece movedPiece = board.getPiece(move.getStartPosition());
                ChessPiece capturedPiece = board.getPiece(move.getEndPosition());

                board.addPiece(move.getStartPosition(), null);
                board.addPiece(move.getEndPosition(), movedPiece);

                boolean inCheck = isInCheck(movingTeam);

                board.addPiece(move.getStartPosition(), movedPiece);
                board.addPiece(move.getEndPosition(), capturedPiece);

                if (!inCheck) {
                    validMoves.add(move);
                }
            }
            return validMoves;
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (board.getPiece(move.getStartPosition()) == null) {throw new InvalidMoveException();}
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if (!validMoves.contains(move)) {throw new InvalidMoveException();}
        ChessPiece piece = board.getPiece(move.getStartPosition());
        TeamColor color = piece.getTeamColor() == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
        if (move.getPromotionPiece() != null) {
            piece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
        }
        if (getTeamTurn() == color) {throw new InvalidMoveException();}
        board.board[8 - move.getStartPosition().getRow()][move.getStartPosition().getColumn() - 1] = null;
        board.board[8 - move.getEndPosition().getRow()][move.getEndPosition().getColumn() - 1] = piece;
        setTeamTurn(color);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = null;
        int rowN = 8;
        int colN;
        for (ChessPiece[] row : board.board) {
            colN = 1;
            for (ChessPiece piece : row) {
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    kingPos = new ChessPosition(rowN, colN);
                    break;
                }
                colN++;
            }
            rowN--;
        }


        for (rowN = 1; rowN < 9; rowN++) {
            for (colN = 1; colN < 9; colN++) {
                ChessPosition position = new ChessPosition(rowN, colN);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() != teamColor) {
                    ChessPosition startPos = new ChessPosition(rowN, colN);
                    Collection<ChessMove> moves = piece.pieceMoves(board, startPos);
                    for (ChessMove move : moves) {
                        if (move.getEndPosition().equals(kingPos)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        List<ChessPosition> pieces = new ArrayList<>();

        if (!isInCheck(teamColor)) {
            return false;
        }

        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition position = new ChessPosition(row, col);
                if (board.getPiece(position) != null && board.getPiece(position).getTeamColor() == teamColor) {
                    pieces.add(position);
                }
            }
        }

        for (ChessPosition position : pieces) {
            if (!validMoves(position).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheckmate(teamColor)) {
            return false;
        }
        List<ChessPosition> pieces = new ArrayList<>();
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition position = new ChessPosition(row, col);
                if (board.getPiece(position) != null && board.getPiece(position).getTeamColor() == teamColor) {
                    pieces.add(position);
                }
            }
        }

        for (ChessPosition position : pieces) {
            if (!validMoves(position).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board);
    }
}
