package server;

import java.net.*;
import java.util.*;

public class Server {

    public static Map<String, ClientHandler> clients = new HashMap<>();

    public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(9999);
        System.out.println("Server đang chạy...");

        while (true) {
            Socket socket = server.accept();
            new ClientHandler(socket).start();
        }
    }
}