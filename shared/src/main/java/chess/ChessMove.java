package chess;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    private final ChessPosition start, end;
    private final ChessPiece.PieceType promotion;
    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.start = startPosition;
        this.end = endPosition;
        this.promotion = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return start;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return end;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotion;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChessMove)) return false;
        ChessMove m = (ChessMove) o;
        return start.equals(m.start) && end.equals(m.end) && (promotion == null && m.promotion == null ||
                (promotion != null && promotion.equals(m.promotion)));
    }
    @Override
    public int hashCode() {
        int h = start.hashCode() * 31 + end.hashCode();
        return promotion == null ? h : h + promotion.hashCode();
    }
    @Override
    public String toString() {
        return start + "->" + end + (promotion != null ? " = " + promotion : "");
    }
}
