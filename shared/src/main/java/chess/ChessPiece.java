package chess;

import model.ChessGame;

import java.util.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor color;
    private final PieceType type;
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
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
        return color;
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
        switch(type) {
            case KING -> kingMoves(board, myPosition, moves);
            case QUEEN -> slideMoves(board, myPosition, moves, new int [][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}});
            case ROOK -> slideMoves(board, myPosition, moves, new int [][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}});
            case BISHOP -> slideMoves(board, myPosition, moves, new int [][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}});
            case KNIGHT -> knightMoves(board, myPosition, moves);
            case PAWN -> pawnMoves(board, myPosition, moves);
        }
        return moves;
    }
    private void slideMoves(ChessBoard b, ChessPosition p, List<ChessMove> m, int[][]dirs) {
        for (int[]d:dirs) {
            int r = p.getRow(), c = p.getColumn();
            while(true) {
                r += d[0]; c += d[1];
                if (r < 1 || r > 8 || c < 1 || c > 8) {
                    break;
                }
                ChessPosition np = new ChessPosition(r, c);
                ChessPiece other = b.getPiece(np);
                if (other == null) {
                    m.add(new ChessMove(p, np, null));
                } else { if (other.color != color) {
                    m.add(new ChessMove(p, np, null));
                } break; }
            }
        }
    }
    private void kingMoves(ChessBoard b, ChessPosition p, List<ChessMove> m) {
        int[][]d = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        addMoves(b, p, m, d);
    }
    private void knightMoves(ChessBoard b, ChessPosition p, List<ChessMove> m) {
        int [][]j = {{2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}};
        addMoves(b, p, m, j);
    }
    private void pawnMoves(ChessBoard b, ChessPosition p, List<ChessMove> m) {
        int dir = color == ChessGame.TeamColor.WHITE ? 1 : -1;
        int start = color == ChessGame.TeamColor.WHITE ? 2 : 7;
        int promo = color == ChessGame.TeamColor.WHITE ? 8 : 1;
        int r = p.getRow(),c = p.getColumn();
        ChessPosition one = new ChessPosition(r + dir, c);
        if (r + dir >= 1 &&  r + dir <= 8 && b.getPiece(one) == null) {
            promoteOrAdd(p, one, m, promo);
            if (r == start) {
                ChessPosition two = new ChessPosition(r + 2 * dir, c);
                if (b.getPiece(two) == null) {
                    m.add(new ChessMove(p, two, null));
                }
            }
        }
        for (int dc:new int[]{-1, 1}) {
            int nc = c +dc;
            if (nc >= 1 && nc <= 8 && r + dir >= 1 && r + dir <= 8) {
                ChessPosition diag = new ChessPosition(r + dir, nc);
                ChessPiece o = b.getPiece(diag);
                if (o != null && o.color != color) {
                    promoteOrAdd(p, diag, m, promo);
                }
            }
        }
    }
    private void promoteOrAdd(ChessPosition s, ChessPosition e, List<ChessMove> m, int promo) {
        if (e.getRow() == promo) {
            m.add(new ChessMove(s, e, PieceType.QUEEN));
            m.add(new ChessMove(s, e, PieceType.ROOK));
            m.add(new ChessMove(s, e, PieceType.BISHOP));
            m.add(new ChessMove(s, e, PieceType.KNIGHT));
        } else {
            m.add(new ChessMove(s, e, null));
        }
    }
    private void addMoves(ChessBoard b, ChessPosition p, List<ChessMove> m, int[][] deltas) {
        for (int[] delta : deltas) {
            int r = p.getRow() + delta[0], c = p.getColumn() + delta[1];
            if (r >= 1 && r <= 8 && c >= 1 && c <= 8) {
                ChessPosition np = new ChessPosition(r, c);
                ChessPiece o = b.getPiece(np);
                if (o == null || o.color != color) {
                    m.add(new ChessMove(p, np, null));
                }
            }
        }
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChessPiece)) {
            return false;
        }
        ChessPiece other = (ChessPiece) o;
        return this.color == other.color && this.type == other.type;
    }
    @Override
    public int hashCode() {
        return Objects.hash(color, type);
    }
}
