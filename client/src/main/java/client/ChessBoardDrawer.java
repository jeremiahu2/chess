package client;

import chess.ChessGame;
import chess.ChessPosition;
import chess.ChessPiece;
import model.GameData;
import chess.ChessBoard;
import ui.EscapeSequences;

public class ChessBoardDrawer {

    public void draw(GameData data, boolean whitePerspective) {
        ChessBoard board = extractBoard(data);
        if (board == null) {
            board = new ChessBoard();
            board.resetBoard();
        }
        render(board, whitePerspective);
    }

    private ChessBoard extractBoard(GameData data) {
        if (data == null) {
            return null;
        }
        try {
            var game = data.game();
            if (game == null) {
                return null;
            }
            try {
                var m = game.getClass().getMethod("getBoard");
                Object res = m.invoke(game);
                if (res instanceof ChessBoard) {
                    return (ChessBoard) res;
                }
            } catch (NoSuchMethodException ignored) {}
            try {
                var m = game.getClass().getMethod("board");
                Object res = m.invoke(game);
                if (res instanceof ChessBoard) {
                    return(ChessBoard) res;
                }
            } catch (NoSuchMethodException ignored) {}
        } catch (Exception ignored) {}
        return null;
    }

    private void render(ChessBoard board, boolean whitePerspective) {
        final String reset = EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR;
        final String lightBg = EscapeSequences.SET_BG_COLOR_WHITE;
        final String darkBg = EscapeSequences.SET_BG_COLOR_DARK_GREY;
        System.out.print("    ");
        if (whitePerspective) {
            for (char f = 'a'; f <= 'h'; f++) {
                System.out.print("  " + f + " ");
            }
        } else {
            for (char f = 'h'; f >= 'a'; f--) {
                System.out.print("  " + f + " ");
            }
        }
        System.out.println();
        if (whitePerspective) {
            for (int rank = 8; rank >=1; rank--) {
                System.out.printf(" %d  ", rank);
                for (int file1 = 1; file1 <= 8; file1++) {
                    boolean lightSquare = squareIsLight(rank, file1);
                    String bg = lightSquare ? lightBg : darkBg;
                    String cell = cellString(board, rank, file1);
                    System.out.print(bg + cell + reset);
                }
                System.out.printf("  %d%n", rank);
            }
        } else {
            for (int rank = 1; rank <=8; rank++) {
                System.out.printf(" %d  ", rank);
                for (int file2 = 8; file2 >= 1; file2--) {
                    boolean lightSquare = squareIsLight(rank, file2);
                    String bg = lightSquare ? lightBg : darkBg;
                    String cell = cellString(board, rank, file2);
                    System.out.print(bg + cell + reset);
                }
                System.out.printf("  %d%n", rank);
            }
        }
        System.out.print("     ");
        if (whitePerspective) {
            for (char f = 'a'; f <= 'h'; f++) {
                System.out.print("  " + f + " ");
            }
        } else {
            for (char f = 'h'; f >= 'a'; f--) {
                System.out.print("  " + f + " ");
            }
        }
        System.out.println();
    }

    private boolean squareIsLight(int rank, int file) {
        int rIndex = 8 - rank;
        int cIndex = file - 1;
        return ((rIndex + cIndex) % 2 == 0);
    }

    private String cellString(ChessBoard board, int rank, int file) {
        try {
            ChessPosition pos = new ChessPosition(rank, file);
            ChessPiece p = board.getPiece(pos);
            if (p == null) {
                return EscapeSequences.EMPTY;
            }
            ChessPiece.PieceType type = p.getPieceType();
            var color = p.getTeamColor();
            boolean white = color == ChessGame.TeamColor.WHITE;
            return switch (type) {
                case KING -> white ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
                case QUEEN -> white ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
                case ROOK -> white ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
                case BISHOP -> white ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
                case KNIGHT -> white ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
                case PAWN -> white ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
                default -> " ? ";
            };
        } catch (Exception e) {
            return " ? ";
        }
    }
}
