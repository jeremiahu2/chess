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

    private boolean preLoginLoop() {
        System.out.print("\nprelogin> ");
        String command = scanner.nextLine().trim().toLowerCase(Locale.ROOT);
        switch (command) {
            case "help":
                printPreLoginHelp();
                break;
            case "register":
                doRegister();
                break;
            case "login":
                doLogin();
                break;
            case "quit":
                exit();
                break;
            default:
                System.out.println("Unknown command. Type 'help'.");
        }
        return isLoggedIn();
    }
}

