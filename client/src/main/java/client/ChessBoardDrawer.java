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
}
