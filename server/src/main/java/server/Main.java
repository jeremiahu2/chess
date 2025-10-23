package server;

public class Main {
    public static void main(String[] args) {
        Server s = new Server();
        int port = s.run(8080);
        System.out.println("Server running on port " + port);
    }
}
