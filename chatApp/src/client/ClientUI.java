package client;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.List;
import javax.swing.*;
import service.*;

public class ClientUI {

    static PrintWriter out;
    static String username;
    static String currentUser;
    static JPanel chatPanel;

    static int myID;
    static int currentConversationID = -1;

    public static void start(String user) throws Exception {

        username = user;
        myID = AuthService.getUserID(username);

        Socket socket = new Socket("localhost", 9999);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        out.println(username);

        JFrame frame = new JFrame("Messenger - " + username);
        frame.setSize(950, 600);
        frame.setLayout(new BorderLayout());

        // ===== TOP =====
        JButton btnLogout = new JButton("Đăng xuất");
        JButton btnAvatar = new JButton("Đổi ảnh");

        JPanel top = new JPanel(new BorderLayout());
        top.add(new JLabel("💬 " + username), BorderLayout.WEST);

        JPanel right = new JPanel();
        right.add(btnAvatar);
        right.add(btnLogout);

        top.add(right, BorderLayout.EAST);
        frame.add(top, BorderLayout.NORTH);

        // ===== USER LIST =====
        DefaultListModel<String> model = new DefaultListModel<>();
        List<String> allUsers = AuthService.getAllUsers();

        for (String u : allUsers) {
            if (!u.equalsIgnoreCase(username)) model.addElement(u);
        }

        JList<String> list = new JList<>(model);

        // ===== MENU =====
        JPopupMenu menu = new JPopupMenu();
        JMenuItem block = new JMenuItem("Chặn");
        JMenuItem unblock = new JMenuItem("Gỡ chặn");

        menu.add(block);
        menu.add(unblock);
        list.setComponentPopupMenu(menu);

        // ===== AVATAR LIST =====
        list.setCellRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                JPanel panel = new JPanel(new BorderLayout());

                JLabel avatar = new JLabel();
                avatar.setPreferredSize(new Dimension(40,40));

                String path = AuthService.getAvatar(value.toString());

                if (path != null && new File(path).exists()) {
                    ImageIcon icon = new ImageIcon(path);
                    Image img = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                    avatar.setIcon(new ImageIcon(img));
                } else {
                    avatar.setOpaque(true);
                    avatar.setBackground(Color.GRAY);
                }

                panel.add(avatar, BorderLayout.WEST);
                panel.add(new JLabel(value.toString()), BorderLayout.CENTER);

                return panel;
            }
        });

        frame.add(new JScrollPane(list), BorderLayout.WEST);

        // ===== CHAT =====
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));

        frame.add(new JScrollPane(chatPanel), BorderLayout.CENTER);

        // ===== INPUT =====
        JTextField input = new JTextField();
        JButton send = new JButton("Gửi");

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(input, BorderLayout.CENTER);
        bottom.add(send, BorderLayout.EAST);

        frame.add(bottom, BorderLayout.SOUTH);

        frame.setVisible(true);

        // ===== CHỌN USER =====
        list.addListSelectionListener(e -> {
            currentUser = list.getSelectedValue();

            try {
                int uid = AuthService.getUserID(currentUser);

                currentConversationID =
                        ChatService.getOrCreateConversation(myID, uid);

                loadMessages();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // ===== GỬI =====
        Runnable sendMsg = () -> {
            String msg = input.getText();

            if (msg.isEmpty() || currentUser == null) return;

            try {
                int uid = AuthService.getUserID(currentUser);

                if (AuthService.isBlocked(myID, uid) ||
                    AuthService.isBlocked(uid, myID)) {

                    JOptionPane.showMessageDialog(null, "Không thể gửi!");
                    return;
                }

                if (currentConversationID == -1) {
                    currentConversationID =
                        ChatService.getOrCreateConversation(myID, uid);
                }

                // 🔥 LƯU DB
                ChatService.sendMessage(currentConversationID, myID, msg);

                // 🔥 GỬI SOCKET
                out.println(currentUser + ":" + msg);

                addBubble("Me: " + msg, true);

                input.setText("");

            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        send.addActionListener(e -> sendMsg.run());
        input.addActionListener(e -> sendMsg.run());

        // ===== NHẬN =====
        new Thread(() -> {
            try {
                String msg;
                while ((msg = in.readLine()) != null) {
                    addBubble(msg, false);
                }
            } catch (Exception e) {}
        }).start();

        // ===== BLOCK =====
        block.addActionListener(e -> {
            try {
                int uid = AuthService.getUserID(currentUser);
                AuthService.blockUser(myID, uid);
                JOptionPane.showMessageDialog(null, "Đã chặn!");
            } catch (Exception ex) {}
        });

        // ===== UNBLOCK =====
        unblock.addActionListener(e -> {
            try {
                int uid = AuthService.getUserID(currentUser);
                AuthService.unblockUser(myID, uid);
                JOptionPane.showMessageDialog(null, "Đã gỡ chặn!");
            } catch (Exception ex) {}
        });

        // ===== ĐỔI AVATAR =====
        btnAvatar.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();

            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = chooser.getSelectedFile();

                    File dir = new File("avatars");
                    if (!dir.exists()) dir.mkdir();

                    File newFile = new File("avatars/" + file.getName());

                    java.nio.file.Files.copy(
                        file.toPath(),
                        newFile.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                    );

                    Connection conn = database.DBConnection.getConnection();

                    PreparedStatement ps = conn.prepareStatement(
                        "UPDATE Users SET Avatar=? WHERE Username=?"
                    );

                    ps.setString(1, newFile.getPath());
                    ps.setString(2, username);
                    ps.executeUpdate();

                    JOptionPane.showMessageDialog(null, "Đổi avatar thành công!");

                    list.repaint();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        // ===== LOGOUT =====
        btnLogout.addActionListener(e -> {
            frame.dispose();
            try { socket.close(); } catch (Exception ex) {}
            LoginUI.main(null);
        });
    }

    static void loadMessages() {
        try {
            chatPanel.removeAll();

            if (currentConversationID == -1) return;

            ResultSet rs = ChatService.getMessages(currentConversationID);

            while (rs.next()) {
                String sender = rs.getString("Username");
                String content = rs.getString("Content");

                addBubble(sender + ": " + content,
                        sender.equals(username));
            }

            chatPanel.revalidate();
            chatPanel.repaint();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void addBubble(String msg, boolean me) {
        JPanel p = new JPanel(new FlowLayout(
                me ? FlowLayout.RIGHT : FlowLayout.LEFT));

        JLabel label = new JLabel(msg);
        label.setOpaque(true);

        if (me) {
            label.setBackground(Color.BLUE);
            label.setForeground(Color.WHITE);
        } else {
            label.setBackground(Color.LIGHT_GRAY);
        }

        p.add(label);
        chatPanel.add(p);
        chatPanel.revalidate();
    }
}