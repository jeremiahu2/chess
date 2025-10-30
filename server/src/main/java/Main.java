import chess.ChessGame;
import chess.*;
import dataaccess.DatabaseManager;


public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
        try {
            DatabaseManager.example(); // 👈 add this line
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}