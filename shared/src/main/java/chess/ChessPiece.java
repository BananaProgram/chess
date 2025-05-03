package chess;

import java.util.Collection;
import java.util.Objects;
import java.util.Vector;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    ChessGame.TeamColor pieceColor;
    ChessPiece.PieceType type;

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
    private boolean isDiagonal(ChessPosition myPosition, int endX, int endY) {
        return (endX - myPosition.getRow()) == (endY - myPosition.getColumn());
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Vector<ChessMove> possibleMoves = new Vector<ChessMove>();

        if (this.type == PieceType.QUEEN) {
            for (int i = 1; i < 9; i++) {
                for (int j = 1; j < 9; j++) {
                    if ((i == myPosition.getRow()) || (j == myPosition.getColumn()) || (isDiagonal(myPosition, i, j))) {
                        ChessPosition endPosition = new ChessPosition(i, j);
                        if (board.getPiece(endPosition) == null) {
                            possibleMoves.addElement(new ChessMove(myPosition, endPosition, null));
                        }
                    }
                }
            }
        } else if (this.type == PieceType.ROOK) {
            return possibleMoves;
        } else if (this.type == PieceType.BISHOP) {
            return possibleMoves;
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
