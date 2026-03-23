package client;

import java.awt.*;
import javax.swing.*;
import service.AuthService;

public class LoginUI {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Login");
        frame.setSize(300, 250);
        frame.setLayout(new GridLayout(5, 1));

        JTextField txtUser = new JTextField();
        JPasswordField txtPass = new JPasswordField();

        JButton btnLogin = new JButton("Login");
        JButton btnRegister = new JButton("Register");

        frame.add(new JLabel("Username"));
        frame.add(txtUser);
        frame.add(new JLabel("Password"));
        frame.add(txtPass);

        JPanel panelBtn = new JPanel();
        panelBtn.add(btnLogin);
        panelBtn.add(btnRegister);

        frame.add(panelBtn);

        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // LOGIN
        btnLogin.addActionListener(e -> {
            String user = txtUser.getText();
            String pass = new String(txtPass.getPassword());

            int id = AuthService.login(user, pass);

            if (id != -1) {
                JOptionPane.showMessageDialog(frame, "Đăng nhập thành công!");
                frame.dispose();

                try {
                    ClientUI.start(user);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            } else {
                JOptionPane.showMessageDialog(frame, "Sai tài khoản!");
            }
        });

        // REGISTER
        btnRegister.addActionListener(e -> {
            String user = txtUser.getText();
            String pass = new String(txtPass.getPassword());

            boolean ok = AuthService.register(user, pass);

            if (ok) {
                JOptionPane.showMessageDialog(frame, "Đăng ký thành công!");
            } else {
                JOptionPane.showMessageDialog(frame, "Tài khoản đã tồn tại!");
            }
        });
    }
}