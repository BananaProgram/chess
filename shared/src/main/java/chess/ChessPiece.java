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
    private Collection<ChessMove> checkDiagonals(ChessPosition myPosition, ChessBoard board, ChessGame.TeamColor color) {
        Vector<ChessMove> diagonalMoves = new Vector<>();
        int x = myPosition.getRow() + 1;
        int y = myPosition.getColumn() + 1;

        while (x < 9 && y < 9 && x > 0 && y > 0) {
            ChessPosition endPosition = new ChessPosition(x, y);
            if (board.getPiece(endPosition) == null || board.getPiece(endPosition).getTeamColor() != color) {
                diagonalMoves.add(new ChessMove(myPosition, endPosition, null));
                x++;
                y++;
            } else {break;}
            if (board.getPiece(endPosition) != null && board.getPiece(endPosition).getTeamColor() != color) {break;}
        }

        x = myPosition.getRow() + 1;
        y = myPosition.getColumn() - 1;
        while (x < 9 && y < 9 && x > 0 && y > 0) {
            ChessPosition endPosition = new ChessPosition(x, y);
            if (board.getPiece(endPosition) == null || board.getPiece(endPosition).getTeamColor() != color) {
                diagonalMoves.add(new ChessMove(myPosition, endPosition, null));
                x++;
                y--;
            } else {break;}
            if (board.getPiece(endPosition) != null && board.getPiece(endPosition).getTeamColor() != color) {break;}
        }

        x = myPosition.getRow() - 1;
        y = myPosition.getColumn() + 1;
        while (x < 9 && y < 9 && x > 0 && y > 0) {
            ChessPosition endPosition = new ChessPosition(x, y);
            if (board.getPiece(endPosition) == null || board.getPiece(endPosition).getTeamColor() != color) {
                diagonalMoves.add(new ChessMove(myPosition, endPosition, null));
                x--;
                y++;
            } else {break;}
            if (board.getPiece(endPosition) != null && board.getPiece(endPosition).getTeamColor() != color) {break;}
        }

        x = myPosition.getRow() - 1;
        y = myPosition.getColumn() - 1;
        while (x < 9 && y < 9 && x > 0 && y > 0) {
            ChessPosition endPosition = new ChessPosition(x, y);
            if (board.getPiece(endPosition) == null || board.getPiece(endPosition).getTeamColor() != color) {
                diagonalMoves.add(new ChessMove(myPosition, endPosition, null));
                x--;
                y--;
            } else {break;}
            if (board.getPiece(endPosition) != null && board.getPiece(endPosition).getTeamColor() != color) {break;}
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

    private Collection<ChessMove> knightMoves(ChessPosition myPosition, ChessBoard board, ChessGame.TeamColor color) {
        Vector<ChessMove> knightMoves = new Vector<>();

        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        int row = startRow + 2;
        int col = startCol + 1;
        if (row < 9 && row > 0 && col < 9 && col > 0) {
            ChessPosition endPosition = new ChessPosition(row, col);
            if (board.getPiece(endPosition) == null || board.getPiece(endPosition).getTeamColor() != color) {
                knightMoves.add(new ChessMove(myPosition, endPosition, null));
            }
        }
        col -= 2;
        if (row < 9 && row > 0 && col < 9 && col > 0) {
            ChessPosition endPosition = new ChessPosition(row, col);
            if (board.getPiece(endPosition) == null || board.getPiece(endPosition).getTeamColor() != color) {
                knightMoves.add(new ChessMove(myPosition, endPosition, null));
            }
        }

        row = startRow - 2;
        col = startCol + 1;
        if (row < 9 && row > 0 && col < 9 && col > 0) {
            ChessPosition endPosition = new ChessPosition(row, col);
            if (board.getPiece(endPosition) == null || board.getPiece(endPosition).getTeamColor() != color) {
                knightMoves.add(new ChessMove(myPosition, endPosition, null));
            }
        }
        col -= 2;
        if (row < 9 && row > 0 && col < 9 && col > 0) {
            ChessPosition endPosition = new ChessPosition(row, col);
            if (board.getPiece(endPosition) == null || board.getPiece(endPosition).getTeamColor() != color) {
                knightMoves.add(new ChessMove(myPosition, endPosition, null));
            }
        }

        row = startRow + 1;
        col = startCol + 2;
        if (row < 9 && row > 0 && col < 9 && col > 0) {
            ChessPosition endPosition = new ChessPosition(row, col);
            if (board.getPiece(endPosition) == null || board.getPiece(endPosition).getTeamColor() != color) {
                knightMoves.add(new ChessMove(myPosition, endPosition, null));
            }
        }
        row -= 2;
        if (row < 9 && row > 0 && col < 9 && col > 0) {
            ChessPosition endPosition = new ChessPosition(row, col);
            if (board.getPiece(endPosition) == null || board.getPiece(endPosition).getTeamColor() != color) {
                knightMoves.add(new ChessMove(myPosition, endPosition, null));
            }
        }

        row = startRow + 1;
        col = startCol - 2;
        if (row < 9 && row > 0 && col < 9 && col > 0) {
            ChessPosition endPosition = new ChessPosition(row, col);
            if (board.getPiece(endPosition) == null || board.getPiece(endPosition).getTeamColor() != color) {
                knightMoves.add(new ChessMove(myPosition, endPosition, null));
            }
        }
        row -= 2;
        if (row < 9 && row > 0 && col < 9 && col > 0) {
            ChessPosition endPosition = new ChessPosition(row, col);
            if (board.getPiece(endPosition) == null || board.getPiece(endPosition).getTeamColor() != color) {
                knightMoves.add(new ChessMove(myPosition, endPosition, null));
            }
        }

        return knightMoves;
    }

    private Collection<ChessMove> kingMoves(ChessPosition myPosition, ChessBoard board, ChessGame.TeamColor color) {
        Vector<ChessMove> kingMoves = new Vector<>();
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        for (int row = startRow - 1; row < startRow + 2; row++) {
            for (int col = startCol - 1; col < startCol + 2; col++) {
                if (row < 9 && row > 0 && col < 9 && col > 0 && !(row == startRow && col == startCol)) {
                    ChessPosition endPosition = new ChessPosition(row, col);
                    if (board.getPiece(endPosition) == null || board.getPiece(endPosition).getTeamColor() != color) {
                        kingMoves.add(new ChessMove(myPosition, endPosition, null));
                    }
                }
            }
        }

        return kingMoves;
    }

    private Collection<ChessMove> pawnMoves(ChessPosition myPosition, ChessBoard board, ChessGame.TeamColor color) {
        Vector<ChessMove> pawnMoves = new Vector<>();
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();
        boolean promotion = (color == ChessGame.TeamColor.WHITE && startRow == 7) || (color == ChessGame.TeamColor.BLACK && startRow == 2);

        int direction = (color == ChessGame.TeamColor.WHITE) ? 1 : -1;

        if ((startRow == 2 && color == ChessGame.TeamColor.WHITE) || (startRow == 7 && color == ChessGame.TeamColor.BLACK)) {
            //System.out.println(direction);
            ChessPosition endPosition = new ChessPosition(startRow + (2 * direction), startCol);
            ChessPosition midPosition = new ChessPosition(startRow + direction, startCol);
            if (board.getPiece(midPosition) == null) {
                pawnMoves.add(new ChessMove(myPosition, midPosition, null));
            }
            if (board.getPiece(endPosition) == null && board.getPiece(midPosition) == null) {
                pawnMoves.add(new ChessMove(myPosition, endPosition, null));
            }
        } else if (startRow < 8 && startRow > 1) {
            ChessPosition endPosition = new ChessPosition(startRow + direction, startCol);
            if (board.getPiece(endPosition) == null) {
                if (promotion) {
                    pawnMoves.add(new ChessMove(myPosition, endPosition, PieceType.QUEEN));
                    pawnMoves.add(new ChessMove(myPosition, endPosition, PieceType.ROOK));
                    pawnMoves.add(new ChessMove(myPosition, endPosition, PieceType.BISHOP));
                    pawnMoves.add(new ChessMove(myPosition, endPosition, PieceType.KNIGHT));
                } else {
                    pawnMoves.add(new ChessMove(myPosition, endPosition, null));
                }
            }
        }

        if (startCol < 8) {
            ChessPosition east = new ChessPosition(startRow + direction, startCol + 1);

            if (board.getPiece(east) != null && board.getPiece(east).getTeamColor() != color) {
                if (promotion) {
                    pawnMoves.add(new ChessMove(myPosition, east, PieceType.QUEEN));
                    pawnMoves.add(new ChessMove(myPosition, east, PieceType.ROOK));
                    pawnMoves.add(new ChessMove(myPosition, east, PieceType.BISHOP));
                    pawnMoves.add(new ChessMove(myPosition, east, PieceType.KNIGHT));
                } else {
                    pawnMoves.add(new ChessMove(myPosition, east, null));
                }

            }
        }

        if (startCol > 1) {
            ChessPosition west = new ChessPosition(startRow + direction, startCol - 1);

            if (board.getPiece(west) != null && board.getPiece(west).getTeamColor() != color) {
                if (promotion) {
                    pawnMoves.add(new ChessMove(myPosition, west, PieceType.QUEEN));
                    pawnMoves.add(new ChessMove(myPosition, west, PieceType.ROOK));
                    pawnMoves.add(new ChessMove(myPosition, west, PieceType.BISHOP));
                    pawnMoves.add(new ChessMove(myPosition, west, PieceType.KNIGHT));
                } else {
                    pawnMoves.add(new ChessMove(myPosition, west, null));
                }
            }
        }

        return pawnMoves;
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
            possibleMoves.addAll(checkDiagonals(myPosition, board, this.getTeamColor()));
            possibleMoves.addAll(checkAxes(myPosition, board, this.pieceColor));
        } else if (this.type == PieceType.ROOK) {
            possibleMoves.addAll(checkAxes(myPosition, board, this.pieceColor));
        } else if (this.type == PieceType.BISHOP) {
            possibleMoves.addAll(checkDiagonals(myPosition, board, this.getTeamColor()));
        } else if (this.type == PieceType.KNIGHT) {
            possibleMoves.addAll(knightMoves(myPosition, board, this.pieceColor));
        } else if (this.type == PieceType.KING) {
            possibleMoves.addAll(kingMoves(myPosition, board,this.getTeamColor()));
        } else {
            possibleMoves.addAll(pawnMoves(myPosition, board, this.getTeamColor()));
        }

        return possibleMoves;
    }
}