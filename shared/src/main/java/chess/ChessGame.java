package chess;

import java.util.*;

/**
 * Manages a chess game, making moves on a board.
 * <p>
 * You may add to this class, but do not alter existing method signatures.
 */
public class ChessGame {
    private TeamColor turn;
    private ChessBoard board;
    private boolean whiteKingMoved = false;
    private boolean blackKingMoved = false;
    private boolean whiteRookA1Moved = false;
    private boolean whiteRookH1Moved = false;
    private boolean blackRookA8Moved = false;
    private boolean blackRookH8Moved = false;
    private ChessMove lastMove = null;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        turn = TeamColor.WHITE;
    }

    public TeamColor getTeamTurn() {
        return turn;
    }

    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    public enum TeamColor {
        WHITE, BLACK
    }

    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) return null;

        Collection<ChessMove> valid = new ArrayList<>();
        Collection<ChessMove> all = piece.pieceMoves(board, startPosition);
        if (all == null) all = new ArrayList<>();

        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            all.addAll(getCastlingMoves(startPosition, piece.getTeamColor()));
        }

        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            all.addAll(getEnPassantMoves(startPosition, piece.getTeamColor()));
        }

        for (ChessMove m : all) {
            ChessBoard copy = cloneBoard();
            ChessPiece moved = copy.getPiece(m.getStartPosition());
            if (moved == null) continue;

            if (moved.getPieceType() == ChessPiece.PieceType.PAWN && m.getPromotionPiece() != null) {
                copy.addPiece(m.getEndPosition(), new ChessPiece(moved.getTeamColor(), m.getPromotionPiece()));
            } else {
                copy.addPiece(m.getEndPosition(), moved);
            }

            copy.addPiece(m.getStartPosition(), null);

            if (!inCheck(copy, moved.getTeamColor())) {
                valid.add(m);
            }
        }
        return valid;
    }

    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null || piece.getTeamColor() != turn) {
            throw new InvalidMoveException();
        }

        Collection<ChessMove> v = validMoves(move.getStartPosition());
        if (v == null || !v.contains(move)) throw new InvalidMoveException();

        handleCastling(move, piece);
        handleEnPassant(move, piece);

        ChessPiece toPlace = piece;
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN && move.getPromotionPiece() != null) {
            toPlace = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
        }

        board.addPiece(move.getEndPosition(), toPlace);
        board.addPiece(move.getStartPosition(), null);

        markPieceMoved(piece, move);

        lastMove = move;
        turn = (turn == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
    }

    public boolean isInCheck(TeamColor teamColor) {
        return inCheck(board, teamColor);
    }

    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && noMoves(teamColor);
    }

    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) && noMoves(teamColor);
    }

    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    public ChessBoard getBoard() {
        return board;
    }

    private boolean inCheck(ChessBoard b, TeamColor team) {
        ChessPosition king = findKing(b, team);
        if (king == null) return true;
        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                ChessPosition pos = new ChessPosition(r, c);
                ChessPiece piece = b.getPiece(pos);
                if (piece != null && piece.getTeamColor() != team) {
                    Collection<ChessMove> moves = piece.pieceMoves(b, pos);
                    if (moves == null) continue;
                    for (ChessMove m : moves) {
                        if (m.getEndPosition().equals(king)) return true;
                    }
                }
            }
        }
        return false;
    }

    private ChessPosition findKing(ChessBoard b, TeamColor team) {
        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                ChessPosition pos = new ChessPosition(r, c);
                ChessPiece p = b.getPiece(pos);
                if (p != null && p.getTeamColor() == team && p.getPieceType() == ChessPiece.PieceType.KING)
                    return pos;
            }
        }
        return null;
    }

    private ChessBoard cloneBoard() {
        ChessBoard copy = new ChessBoard();
        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                ChessPosition pos = new ChessPosition(r, c);
                ChessPiece p = board.getPiece(pos);
                if (p != null) copy.addPiece(pos, new ChessPiece(p.getTeamColor(), p.getPieceType()));
            }
        }
        return copy;
    }

    private boolean noMoves(TeamColor team) {
        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                ChessPosition pos = new ChessPosition(r, c);
                ChessPiece p = board.getPiece(pos);
                if (p != null && p.getTeamColor() == team) {
                    Collection<ChessMove> moves = validMoves(pos);
                    if (moves != null && !moves.isEmpty()) return false;
                }
            }
        }
        return true;
    }

    private Collection<ChessMove> getCastlingMoves(ChessPosition kingPos, TeamColor team) {
        List<ChessMove> castles = new ArrayList<>();
        ChessPiece king = board.getPiece(kingPos);
        if (king == null || king.getPieceType() != ChessPiece.PieceType.KING) return castles;
        if (inCheck(board, team)) return castles;

        int row = kingPos.getRow();

        ChessPiece rookK = board.getPiece(new ChessPosition(row, 8));
        if (canCastle(kingPos, team, rookK, 8, new int[]{6, 7})) {
            castles.add(new ChessMove(kingPos, new ChessPosition(row, 7), null));
        }

        ChessPiece rookQ = board.getPiece(new ChessPosition(row, 1));
        if (canCastle(kingPos, team, rookQ, 1, new int[]{2, 3, 4})) {
            castles.add(new ChessMove(kingPos, new ChessPosition(row, 3), null));
        }

        return castles;
    }

    private boolean canCastle(ChessPosition kingPos, TeamColor team, ChessPiece rook, int rookCol, int[] emptyCols) {
        if (rook == null || rook.getPieceType() != ChessPiece.PieceType.ROOK || rook.getTeamColor() != team)
            return false;
        if (isKingMoved(team) || isRookMoved(team, rookCol)) return false;

        int row = kingPos.getRow();
        for (int col : emptyCols) {
            if (board.getPiece(new ChessPosition(row, col)) != null) return false;
        }

        for (int i = 0; i < emptyCols.length; i++) {
            int col = (i == emptyCols.length - 1) ? emptyCols[emptyCols.length - 1] : emptyCols[i];
            if (isSquareAttacked(board, new ChessPosition(row, col), team)) return false;
        }

        return true;
    }

    private boolean isKingMoved(TeamColor team) {
        return team == TeamColor.WHITE ? whiteKingMoved : blackKingMoved;
    }

    private boolean isSquareAttacked(ChessBoard board, ChessPosition square, TeamColor team) {
        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                ChessPosition pos = new ChessPosition(r, c);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() != team) {
                    for (ChessMove move : piece.pieceMoves(board, pos)) {
                        if (move.getEndPosition().equals(square)) return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isRookMoved(TeamColor team, int rookColumn) {
        if (team == TeamColor.WHITE) {
            if (rookColumn == 1) return whiteRookA1Moved;
            if (rookColumn == 8) return whiteRookH1Moved;
        } else {
            if (rookColumn == 1) return blackRookA8Moved;
            if (rookColumn == 8) return blackRookH8Moved;
        }
        return true;
    }

    private Collection<ChessMove> getEnPassantMoves(ChessPosition pawnPos, TeamColor team) {
        List<ChessMove> res = new ArrayList<>();
        if (lastMove == null) return res;
        ChessPiece pawn = board.getPiece(pawnPos);
        if (pawn == null || pawn.getPieceType() != ChessPiece.PieceType.PAWN) return res;

        ChessPiece lastMovedPiece = board.getPiece(lastMove.getEndPosition());
        if (lastMovedPiece == null || lastMovedPiece.getPieceType() != ChessPiece.PieceType.PAWN) return res;
        if (lastMovedPiece.getTeamColor() == team) return res;

        int movedDistance = Math.abs(lastMove.getStartPosition().getRow() - lastMove.getEndPosition().getRow());
        if (movedDistance != 2) return res;

        if (lastMove.getEndPosition().getRow() == pawnPos.getRow() &&
                Math.abs(lastMove.getEndPosition().getColumn() - pawnPos.getColumn()) == 1) {
            int dir = (team == TeamColor.WHITE) ? 1 : -1;
            ChessPosition target = new ChessPosition(pawnPos.getRow() + dir, lastMove.getEndPosition().getColumn());

            ChessBoard copy = cloneBoard();
            ChessPiece copyPawn = copy.getPiece(pawnPos);
            if (copyPawn == null) return res;

            copy.addPiece(target, copyPawn);
            copy.addPiece(pawnPos, null);
            copy.addPiece(lastMove.getEndPosition(), null);
            if (!inCheck(copy, team)) {
                res.add(new ChessMove(pawnPos, target, null));
            }
        }
        return res;
    }

    private void handleCastling(ChessMove move, ChessPiece piece) {
        if (piece.getPieceType() != ChessPiece.PieceType.KING) return;
        int row = move.getStartPosition().getRow();
        int diff = move.getEndPosition().getColumn() - move.getStartPosition().getColumn();
        if (Math.abs(diff) == 2) {
            if (diff > 0) {
                ChessPiece rook = board.getPiece(new ChessPosition(row, 8));
                board.addPiece(new ChessPosition(row, 6), rook);
                board.addPiece(new ChessPosition(row, 8), null);
            } else {
                ChessPiece rook = board.getPiece(new ChessPosition(row, 1));
                board.addPiece(new ChessPosition(row, 4), rook);
                board.addPiece(new ChessPosition(row, 1), null);
            }
        }
    }

    private void handleEnPassant(ChessMove move, ChessPiece piece) {
        if (piece.getPieceType() != ChessPiece.PieceType.PAWN) return;
        if (move.getStartPosition().getColumn() != move.getEndPosition().getColumn() &&
                board.getPiece(move.getEndPosition()) == null) {
            ChessPosition captured = new ChessPosition(move.getStartPosition().getRow(), move.getEndPosition().getColumn());
            board.addPiece(captured, null);
        }
    }

    private void markPieceMoved(ChessPiece piece, ChessMove move) {
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            if (piece.getTeamColor() == TeamColor.WHITE) whiteKingMoved = true;
            else blackKingMoved = true;
        } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            ChessPosition start = move.getStartPosition();
            if (start.equals(new ChessPosition(1, 1))) whiteRookA1Moved = true;
            else if (start.equals(new ChessPosition(1, 8))) whiteRookH1Moved = true;
            else if (start.equals(new ChessPosition(8, 1))) blackRookA8Moved = true;
            else if (start.equals(new ChessPosition(8, 8))) blackRookH8Moved = true;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChessGame)) return false;
        ChessGame other = (ChessGame) o;
        return Objects.equals(this.board, other.board) && this.turn == other.turn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, turn);
    }
}
