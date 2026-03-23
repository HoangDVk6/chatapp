import client.LoginUI;

public class Main {

    public static void main(String[] args) {

        // chạy server nền
        new Thread(() -> {
            try {
                server.Server.main(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // mở login
        LoginUI.main(null);
    }
}