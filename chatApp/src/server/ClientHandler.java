package server;

import java.io.*;
import java.net.*;
import service.AuthService;

class ClientHandler extends Thread {

    Socket socket;
    BufferedReader in;
    PrintWriter out;
    String username;

    public ClientHandler(Socket socket) throws Exception {
        this.socket = socket;

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void run() {
        try {
            username = in.readLine();
            Server.clients.put(username, this);

            String msg;

            while ((msg = in.readLine()) != null) {

                String[] parts = msg.split(":", 2);

                if (parts.length < 2) continue;

                String receiver = parts[0];
                String content = parts[1];

                ClientHandler target = Server.clients.get(receiver);

                if (target != null) {

                    int senderID = AuthService.getUserID(username);
                    int receiverID = AuthService.getUserID(receiver);

                    // 🔥 CHECK BLOCK 2 CHIỀU
                    if (AuthService.isBlocked(senderID, receiverID) ||
                        AuthService.isBlocked(receiverID, senderID)) {

                        System.out.println("Tin nhắn bị chặn giữa " + username + " và " + receiver);
                        continue;
                    }

                    target.out.println(username + ":" + content);
                }
            }

        } catch (Exception e) {
            System.out.println(username + " thoát");
        }
    }
}