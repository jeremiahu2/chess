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

    public void run() {
        System.out.println("Welcome to CS240 Chess Game! (Phase 5)");
        System.out.println("Type 'help' for commands.");
        boolean loggedIn = false;
        while (true) {
            try {
                if (!loggedIn) {
                    loggedIn = preLoginLoop();
                } else {
                    loggedIn = postLoginLoop();
                }
            } catch (Exception e) {
                System.out.println("Error: " + (e.getMessage() == null ? "An error occured." : e.getMessage()));
            }
        }
    }
}

