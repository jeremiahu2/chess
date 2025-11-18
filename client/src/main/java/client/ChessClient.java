package client;

import java.util.*;
import model.GameData;
import service.requests.*;
import service.results.*;

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

    private void printPreLoginHelp() {
        System.out.println("Prelogin commands: register | login | quit | help");
    }

    private void doRegister() {
        try {
            System.out.print("username: ");
            String u = scanner.nextLine().trim();
            System.out.print("password: ");
            String p = scanner.nextLine().trim();
            System.out.print("email: ");
            String e = scanner.nextLine().trim();
            RegisterResult res = facade.register(new RegisterRequest(u, p, e));
            System.out.println("Registered and logged in as " + res.username());
        } catch (Exception ex) {
            System.out.println("Registration failed: Username or Password already taken ");
        }
    }

    private void doLogin() {
        try {
            System.out.print("username: ");
            String u = scanner.nextLine().trim();
            System.out.print("password: ");
            String p = scanner.nextLine().trim();
            LoginResult res = facade.login(new LoginRequest(u, p));
            System.out.println("Logged in as " + res.username());
        } catch (Exception ex) {
            System.out.println("Login failed: Invalid Username or Password.");
        }
    }

    private boolean postLoginLoop(){
        System.out.print("\npostLogin> ");
        String command = scanner.nextLine().trim().toLowerCase(Locale.ROOT);
        switch (command) {
            case "help":
                printPostLoginHelp();
                break;
            case "logout":
                doLogout();
                break;
            case "create":
                doCreate();
                break;
            case "list":
                doList();
                break;
            case "play":
                doPlay();
                break;
            case "observe":
                doObserve();
                break;
            case "quit":
                exit();
                break;
            default:
                System.out.println("Unknown command. Type 'help'.");
        }
        return isLoggedIn();
    }

    private void printPostLoginHelp() {
        System.out.println("PostLogin commands: create | list | play | observe | logout | quit | help");
    }

    private void doLogout() {
        try {
            facade.logout();
            lastListed.clear();
            System.out.println("Logged out.");
        } catch (Exception e) {
            System.out.println("Logout failed. type 'help' ");
        }
    }

    private void doCreate() {
        try {
            System.out.print("Game name: ");
            String name = scanner.nextLine().trim();
            CreateGameResult res = facade.createGame(new CreateGameRequest(name));
            System.out.println("Created game: " + name);
        } catch (Exception e) {
            System.out.println("Create failed, please try again.");
        }
    }

    private void doList() {
        try {
            GameData[] games = facade.listGames();
            lastListed.clear();
            if (games == null || games.length == 0) {
                System.out.println("No games found.");
                return;
            }
            for (int i = 0; i < games.length; i++) {
                GameData g = games[i];
                int idx = i + 1;
                lastListed.put(idx, g);
                String name = g.gameName();
                String white = g.whiteUsername() == null || g.whiteUsername().isBlank() ? "-" : g.whiteUsername();
                String black = g.blackUsername() == null || g.blackUsername().isBlank() ? "-" : g.blackUsername();
                System.out.printf("%d) %s (white: %s, black: %s)%n", idx, safe(name, "Unnamed"), white, black);
            }
        } catch (Exception e) {
            System.out.println("List failed, no games available.");
        }
    }

    private void doPlay() {
        try {
            int idx = askGameNumber();
            GameData g = lastListed.get(idx);
            if (g == null) {
                System.out.println("Invalid selection.");
                return;
            }
            System.out.print("Color (white/black): ");
            String color = scanner.nextLine().trim().toLowerCase(Locale.ROOT);
            if (!color.equals("white") && !color.equals("black")) {
                System.out.println("Invalid color.");
                return;
            }
            JoinGameRequest joinReq = makeJoinRequest(String.valueOf(g.gameID()), color);
            facade.joinGame(joinReq);
            drawer.draw(g, color.equals("white"));
        } catch (Exception e) {
            System.out.println("Play failed, type list to see available games.");
        }
    }

    private void doObserve() {
        try {
            int idx = askGameNumber();
            GameData g = lastListed.get(idx);
            if (g == null) {
                System.out.println("Invalid selection.");
                return;
            }
            drawer.draw(g, true);
            System.out.println("Displayed the game board (observer mode).");
        } catch (Exception e) {
            System.out.println("Observe failed, type list to see available games.");
        }
    }

    private JoinGameRequest makeJoinRequest(String gameIdString, String color) {
        int gameID;
        try {
            gameID = Integer.parseInt(gameIdString);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid game id: " + gameIdString);
        }
        String playerColor = null;
        if (color != null) {
            if (color.equalsIgnoreCase("white")) {
                playerColor = "WHITE";
            } else if (color.equalsIgnoreCase("black")) {
                playerColor = "BLACK";
            } else {
                throw new RuntimeException("Invalid color: " + color);
            }
        }
        return new JoinGameRequest(playerColor, gameID);
    }

    private int askGameNumber() {
        if (lastListed.isEmpty()) {
            throw new RuntimeException("No games listed. Run 'list' first.");
        }
        System.out.print("Enter game number :");
        String s = scanner.nextLine().trim();
        try {
            int n = Integer.parseInt(s);
            if (!lastListed.containsKey(n)) {
                throw new RuntimeException("Invalid Number.");
            }
            return n;
        } catch (NumberFormatException e) {
            throw new RuntimeException("Please enter a valid number.");
        }
    }

    private boolean isLoggedIn() {
        try {
            facade.listGames();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void exit() {
        System.out.println("Goodbye!");
        System.exit(0);
    }

    private String safe(String s, String fallback) {
        return (s == null || s.isBlank()) ? fallback : s;
    }
}
