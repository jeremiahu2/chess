package client;

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
        final String RESET = EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR;
        final String LIGHT_BG = EscapeSequences.SET_BG_COLOR_WHITE;
        final String DARK_BG = EscapeSequences.SET_BG_COLOR_DARK_GREY;
        System.out.print("    ");
        if (whitePerspective) {
            for (char f = 'a'; f <= 'h'; f++) {
                System.out.print("  " + f + " ");
            }
        } else {
            for (char f = 'h'; f >= 'a'; f--) System.out.print("  " + f + " ");
        }
        System.out.println();
        if (whitePerspective) {
            for (int rank = 8; rank >=1; rank--) {
                System.out.printf(" %d  ", rank);
                for (int file = 1; file <= 8; file++) {
                    boolean lightSquare = squareIsLight(rank, file);
                    String bg = lightSquare ? LIGHT_BG : DARK_BG;
                    String cell = cellString(board, rank, file);
                    System.out.print(bg + cell + RESET);
                }
                System.out.printf("  %d%n", rank);
            }
        } else {
            for (int rank = 1; rank <=8; rank++) {
                System.out.printf(" %d  ", rank);
                for (int file = 8; file >= 1; file--) {
                    boolean lightSquare = squareIsLight(rank, file);
                    String bg = lightSquare ? LIGHT_BG : DARK_BG;
                    String cell = cellString(board, rank, file);
                    System.out.print(bg + cell + RESET);
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


}

