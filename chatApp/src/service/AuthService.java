package service;

import database.DBConnection;
import java.sql.*;
import java.util.*;

public class AuthService {

    public static int login(String username, String password) {
        try {
            Connection conn = DBConnection.getConnection();

            PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM Users WHERE Username=? AND Password=?"
            );

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getInt("UserID");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static boolean register(String username, String password) {
        try {
            Connection conn = DBConnection.getConnection();

            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO Users(Username, Password) VALUES (?, ?)"
            );

            ps.setString(1, username);
            ps.setString(2, password);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            return false;
        }
    }

    public static List<String> getAllUsers() {
        List<String> list = new ArrayList<>();

        try {
            Connection conn = DBConnection.getConnection();

            ResultSet rs = conn.createStatement()
                .executeQuery("SELECT Username FROM Users");

            while (rs.next()) {
                list.add(rs.getString("Username"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static int getUserID(String username) {
        try {
            Connection conn = DBConnection.getConnection();

            PreparedStatement ps = conn.prepareStatement(
                "SELECT UserID FROM Users WHERE Username=?"
            );

            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {}

        return -1;
    }

    // ===== BLOCK =====
    public static void blockUser(int u1, int u2) throws Exception {
        Connection conn = DBConnection.getConnection();

        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO Blocks(UserID, BlockedUserID) VALUES (?, ?)"
        );

        ps.setInt(1, u1);
        ps.setInt(2, u2);

        ps.executeUpdate();
    }

    // ===== UNBLOCK =====
    public static void unblockUser(int u1, int u2) throws Exception {
        Connection conn = DBConnection.getConnection();

        PreparedStatement ps = conn.prepareStatement(
            "DELETE FROM Blocks WHERE UserID=? AND BlockedUserID=?"
        );

        ps.setInt(1, u1);
        ps.setInt(2, u2);

        ps.executeUpdate();
    }

    public static boolean isBlocked(int u1, int u2) {
        try {
            Connection conn = DBConnection.getConnection();

            PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM Blocks WHERE UserID=? AND BlockedUserID=?"
            );

            ps.setInt(1, u1);
            ps.setInt(2, u2);

            return ps.executeQuery().next();

        } catch (Exception e) {}

        return false;
    }

    // ===== AVATAR =====
    public static String getAvatar(String username) {
        try {
            Connection conn = DBConnection.getConnection();

            PreparedStatement ps = conn.prepareStatement(
                "SELECT Avatar FROM Users WHERE Username=?"
            );

            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getString("Avatar");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}