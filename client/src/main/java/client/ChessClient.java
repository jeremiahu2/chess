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
            System.out.println("Registration failed: " + safeMsg(ex))
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
            System.out.println("Login failed: " + safeMsg(ex));
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
            System.out.println("Logout failed: " + safeMsg(e));
        }
    }

    private void doCreate() {
        try {
            System.out.print("Game name: ");
            String name = scanner.nextLine().trim();
            CreateGameResult res = facade.createGame(new CreateGameRequest(name));
            System.out.println("Created game: " + name);
        } catch (Exception e) {
            System.out.println("Create failed: " + safeMsg(e));
        }
    }


}

