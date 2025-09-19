package chess;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    public final ChessPosition startPosition;
    public final ChessPosition endPosition;
    public final ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ChessMove)) return false;
        ChessMove other = (ChessMove) obj;
        return startPosition.equals(other.startPosition)
                && endPosition.equals(other.endPosition)
                && ((promotionPiece == null && other.promotionPiece == null)
                || (promotionPiece != null && promotionPiece.equals(other.promotionPiece)));
    }

    public int hashCode() {
        int result = startPosition.hashCode();
        result = 31 * result + endPosition.hashCode();
        result = 31 * result + (promotionPiece != null ? promotionPiece.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "Move{" +
                "from=" + startPosition +
                ", to=" + endPosition +
                (promotionPiece != null ? ", promote=" + promotionPiece : "") +
                '}';
    }
}
