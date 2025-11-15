package client;

import model.GameData;
import chess.ChessBoard;

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
}
