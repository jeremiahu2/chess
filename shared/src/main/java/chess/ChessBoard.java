package chess;

import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private final ChessPiece[][] board = new ChessPiece[8][8];

    /**
     * Clears the board and places pieces in the standard chess starting configuration
     */
    public void resetBoard() {
        clear();

        for (int col = 1; col <= 8; col++) {
            addPiece(new ChessPosition(2, col), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
            addPiece(new ChessPosition(7, col), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }

        addPiece(new ChessPosition(1, 1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(1, 8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(8, 1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(8, 8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));

        addPiece(new ChessPosition(1, 2), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));

        addPiece(new ChessPosition(1, 3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1, 6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));

        addPiece(new ChessPosition(1, 4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(8, 4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));

        addPiece(new ChessPosition(1, 5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(8, 5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
    }

    /**
     * Removes all pieces from the board
     */
    public void clear() {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                board[r][c] = null;
            }
        }
    }

    /**
     * Adds a piece to the board
     */
    public void addPiece(ChessPosition pos, ChessPiece piece) {
        board[pos.getRow() - 1][pos.getColumn() - 1] = piece;
    }

    /**
     * Gets the piece at the given position (or null if empty)
     */
    public ChessPiece getPiece(ChessPosition pos) {
        return board[pos.getRow() - 1][pos.getColumn() - 1];
    }

    /**
     * Removes and returns the piece at the given position (or null if empty)
     */
    public ChessPiece removePiece(ChessPosition pos) {
        ChessPiece piece = getPiece(pos);
        board[pos.getRow() - 1][pos.getColumn() - 1] = null;
        return piece;
    }

    /**
     * Checks if the position is empty
     */
    public boolean isEmpty(ChessPosition pos) {
        return getPiece(pos) == null;
    }

    /**
     * Checks if the square contains an enemy piece
     */
    public boolean isEnemy(ChessPosition pos, ChessGame.TeamColor myColor) {
        ChessPiece piece = getPiece(pos);
        return piece != null && piece.getTeamColor() != myColor;
    }

    /**
     * Checks if the position is within board boundaries
     */
    public boolean isInBounds(ChessPosition pos) {
        return pos.getRow() >= 1 && pos.getRow() <= 8 && pos.getColumn() >= 1 && pos.getColumn() <= 8;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChessBoard other)) return false;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                ChessPiece p1 = this.board[r][c];
                ChessPiece p2 = other.board[r][c];
                if (!(p1 == null ? p2 == null : p1.equals(p2))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                ChessPiece piece = board[r][c];
                result = 31 * result + (piece == null ? 0 : piece.hashCode());
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int r = 8; r >= 1; r--) {
            for (int c = 1; c <= 8; c++) {
                ChessPiece piece = getPiece(new ChessPosition(r, c));
                sb.append(piece == null ? "." : piece.toString().charAt(0)).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}

