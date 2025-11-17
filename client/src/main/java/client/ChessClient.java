package client;

import model.GameData;

import java.util.*;

public class ChessClient {
    private final ServerFacade facade;
    private final Scanner scanner = new Scanner(System.in);
    private final ChessBoardDrawer drawer = new ChessBoardDrawer();
    private final Map<Integer, GameData> lastListed = new HashMap<>();

    public ChessClient(ServerFacade facade) {
        this.facade = facade;
    }
}
