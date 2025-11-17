import client.*;
import chess.ChessGame;

public class Main {
    public static void main(String[] args) {
        String serverUrl = "http://localhost:8080";

        ServerFacade facade = new ServerFacade(serverUrl);
        ChessClient client = new ChessClient(facade);

        System.out.println("♕ 240 Chess Client");
        client.run();
    }
}