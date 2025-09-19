package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    public final ChessGame.TeamColor pieceColor;
    public final PieceType type;

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
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();

        switch (type) {
            case KING -> addKingMoves(moves, board, myPosition);
            case QUEEN -> addQueenMoves(moves, board, myPosition);
            case BISHOP -> addBishopMoves(moves, board, myPosition);
            case KNIGHT -> addKnightMoves(moves, board, myPosition);
            case ROOK -> addRookMoves(moves, board, myPosition);
            case PAWN -> addPawnMoves(moves, board, myPosition);
        }

        return moves;
    }

    private void addKingMoves(List<ChessMove> moves, ChessBoard board, ChessPosition pos) {
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}};
        for (int[] d : dirs) {
            tryAddMove(moves, board, pos, d[0], d[1]);
        }
    }

    private void addQueenMoves(List<ChessMove> moves, ChessBoard board, ChessPosition pos) {
        addSlidingMoves(moves, board, pos, new int[][]{{1,0},{-1,0},{0,1},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}});
    }

    private void addBishopMoves(List<ChessMove> moves, ChessBoard board, ChessPosition pos) {
        addSlidingMoves(moves, board, pos, new int[][]{{1,1},{1,-1},{-1,1},{-1,-1}});
    }

    private void addRookMoves(List<ChessMove> moves, ChessBoard board, ChessPosition pos) {
        addSlidingMoves(moves, board, pos, new int[][]{{1,0},{-1,0},{0,1},{0,-1}});
    }

    private void addKnightMoves(List<ChessMove> moves, ChessBoard board, ChessPosition pos) {
        int[][] jumps = {{2,1},{2,-1},{-2,1},{-2,-1},{1,2},{1,-2},{-1,2},{-1,-2}};
        for (int[] j : jumps) {
            tryAddMove(moves, board, pos, j[0], j[1]);
        }
    }

    private void addPawnMoves(List<ChessMove> moves, ChessBoard board, ChessPosition pos) {
        int direction = (pieceColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
        ChessPosition oneForward = pos.offset(direction, 0);
        if (board.isInBounds(oneForward) && board.isEmpty(oneForward)) {
            addPawnMove(moves, pos, oneForward);
            int startRow = (pieceColor == ChessGame.TeamColor.WHITE) ? 2 : 7;
            if (pos.getRow() == startRow) {
                ChessPosition twoForward = pos.offset(direction*2, 0);
                if (board.isInBounds(twoForward) && board.isEmpty(twoForward)) {
                    addPawnMove(moves, pos, twoForward);
                }
            }
        }

        for (int dx : new int[]{-1, 1}) {
            ChessPosition diag = pos.offset(direction, dx);
            if (board.isInBounds(diag) && board.isEnemy(diag, pieceColor)) {
                addPawnMove(moves, pos, diag);
            }
        }
    }

    private void addSlidingMoves(List<ChessMove> moves, ChessBoard board, ChessPosition pos, int[][] dirs) {
        for (int[] d : dirs) {
            int row = pos.getRow();
            int col = pos.getColumn();
            while (true) {
                row += d[0];
                col += d[1];
                ChessPosition next = new ChessPosition(row, col);
                if (!board.isInBounds(next)) break;
                if (board.isEmpty(next)) {
                    moves.add(new ChessMove(pos, next, null));
                } else {
                    if (board.isEnemy(next, pieceColor)) {
                        moves.add(new ChessMove(pos, next, null));
                    }
                    break;
                }
            }
        }
    }

    private void tryAddMove(List<ChessMove> moves, ChessBoard board, ChessPosition pos, int dr, int dc) {
        ChessPosition target = pos.offset(dr, dc);
        if (board.isInBounds(target)) {
            if (board.isEmpty(target) || board.isEnemy(target, pieceColor)) {
                moves.add(new ChessMove(pos, target, null));
            }
        }
    }

    private void addPawnMove(List<ChessMove> moves, ChessPosition from, ChessPosition to) {
        int promotionRow = (pieceColor == ChessGame.TeamColor.WHITE) ? 8 : 1;
        if (to.getRow() == promotionRow) {
            moves.add(new ChessMove(from, to, PieceType.QUEEN));
            moves.add(new ChessMove(from, to, PieceType.ROOK));
            moves.add(new ChessMove(from, to, PieceType.BISHOP));
            moves.add(new ChessMove(from, to, PieceType.KNIGHT));
        } else {
            moves.add(new ChessMove(from, to, null));
        }
    }

    @Override
    public String toString() {
        return pieceColor + " " + type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChessPiece other)) return false;
        return pieceColor == other.pieceColor && type == other.type;
    }

    @Override
    public int hashCode() {
        int result = pieceColor != null ? pieceColor.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}

