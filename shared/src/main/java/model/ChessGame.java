package model;

public class ChessGame {
    private String fen = "startpos";

    public ChessGame() {}

    public String getFen() { return fen; }
    public void setFen(String fen) { this.fen = fen; }
}
