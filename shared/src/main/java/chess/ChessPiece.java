package chess;

import java.util.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    /**
     * Checks if a position is diagonal from the current position
     *
     * @return Boolean
     */
    private Collection<ChessMove> checkDiagonals(ChessPosition myPosition, ChessBoard board) {
        Vector<ChessMove> diagonalMoves = new Vector<>();
        int x = myPosition.getRow() + 1;
        int y = myPosition.getColumn() + 1;

        while (x < 9 && y < 9 && x > 0 && y > 0) {
            ChessPosition endPosition = new ChessPosition(x, y);
            diagonalMoves.add(new ChessMove(myPosition, endPosition, null));
            x++;
            y++;
            if (board.getPiece(endPosition) != null) {break;}
        }

        x = myPosition.getRow() + 1;
        y = myPosition.getColumn() - 1;
        while (x < 9 && y < 9 && x > 0 && y > 0) {
            ChessPosition endPosition = new ChessPosition(x, y);
            diagonalMoves.add(new ChessMove(myPosition, endPosition, null));
            x++;
            y--;
            if (board.getPiece(endPosition) != null) {break;}
        }

        x = myPosition.getRow() - 1;
        y = myPosition.getColumn() + 1;
        while (x < 9 && y < 9 && x > 0 && y > 0) {
            ChessPosition endPosition = new ChessPosition(x, y);
            diagonalMoves.add(new ChessMove(myPosition, endPosition, null));
            x--;
            y++;
            if (board.getPiece(endPosition) != null) {break;}
        }

        x = myPosition.getRow() - 1;
        y = myPosition.getColumn() - 1;
        while (x < 9 && y < 9 && x > 0 && y > 0) {
            ChessPosition endPosition = new ChessPosition(x, y);
            diagonalMoves.add(new ChessMove(myPosition, endPosition, null));
            x--;
            y--;
            if (board.getPiece(endPosition) != null) {break;}
        }

        return diagonalMoves;
    }

    private Collection<ChessMove> checkAxes(ChessPosition myPosition, ChessBoard board, ChessGame.TeamColor color) {
        Vector<ChessMove> axialMoves = new Vector<>();

        int row = myPosition.getRow() + 1;
        int col = myPosition.getColumn();
        while (row < 9 && row > 0) {
            ChessPosition endPosition = new ChessPosition(row, col);
            if (board.getPiece(endPosition) == null || board.getPiece(endPosition).getTeamColor() != color) {
                axialMoves.add(new ChessMove(myPosition, endPosition, null));
                row++;
            } else {break;}
            if (board.getPiece(endPosition) != null && board.getPiece(endPosition).getTeamColor() != color) {break;}
        }

        row = myPosition.getRow() - 1;
        col = myPosition.getColumn();
        while (row < 9 && row > 0) {
            ChessPosition endPosition = new ChessPosition(row, col);
            if (board.getPiece(endPosition) == null || board.getPiece(endPosition).getTeamColor() != color) {
                axialMoves.add(new ChessMove(myPosition, endPosition, null));
                row--;
            } else {break;}
            if (board.getPiece(endPosition) != null && board.getPiece(endPosition).getTeamColor() != color) {break;}
        }

        row = myPosition.getRow();
        col = myPosition.getColumn() + 1;
        while (col < 9 && col > 0) {
            ChessPosition endPosition = new ChessPosition(row, col);
            if (board.getPiece(endPosition) == null || board.getPiece(endPosition).getTeamColor() != color) {
                axialMoves.add(new ChessMove(myPosition, endPosition, null));
                col++;
            } else {break;}
            if (board.getPiece(endPosition) != null && board.getPiece(endPosition).getTeamColor() != color) {break;}
        }

        row = myPosition.getRow();
        col = myPosition.getColumn() - 1;
        while (col < 9 && col > 0) {
            ChessPosition endPosition = new ChessPosition(row, col);
            if (board.getPiece(endPosition) == null || board.getPiece(endPosition).getTeamColor() != color) {
                axialMoves.add(new ChessMove(myPosition, endPosition, null));
                col--;
            } else {break;}
            if (board.getPiece(endPosition) != null && board.getPiece(endPosition).getTeamColor() != color) {break;}
        }

        return axialMoves;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Vector<ChessMove> possibleMoves = new Vector<>();

        if (this.type == PieceType.QUEEN) {
            possibleMoves.addAll(checkDiagonals(myPosition, board));
            possibleMoves.addAll(checkAxes(myPosition, board, this.pieceColor));
        } else if (this.type == PieceType.ROOK) {
            possibleMoves.addAll(checkAxes(myPosition, board, this.pieceColor));
        } else if (this.type == PieceType.BISHOP) {
            possibleMoves.addAll(checkDiagonals(myPosition, board));
        } else if (this.type == PieceType.KNIGHT) {
            return possibleMoves;
        } else if (this.type == PieceType.KING) {
            return possibleMoves;
        } else {
            return possibleMoves;
        }

        return possibleMoves;
    }
}
