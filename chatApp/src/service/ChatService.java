package service;

import database.DBConnection;
import java.sql.*;

public class ChatService {

    public static int getOrCreateConversation(int u1, int u2) throws Exception {
        Connection conn = DBConnection.getConnection();

        String sql =
            "SELECT cm1.ConversationID FROM ConversationMembers cm1 " +
            "JOIN ConversationMembers cm2 ON cm1.ConversationID = cm2.ConversationID " +
            "WHERE cm1.UserID=? AND cm2.UserID=?";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, u1);
        ps.setInt(2, u2);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getInt(1);

        PreparedStatement ps2 = conn.prepareStatement(
            "INSERT INTO Conversations DEFAULT VALUES",
            Statement.RETURN_GENERATED_KEYS
        );
        ps2.executeUpdate();

        ResultSet rs2 = ps2.getGeneratedKeys();
        rs2.next();
        int id = rs2.getInt(1);

        PreparedStatement ps3 = conn.prepareStatement(
            "INSERT INTO ConversationMembers VALUES (?,?)"
        );

        ps3.setInt(1, id);
        ps3.setInt(2, u1);
        ps3.executeUpdate();

        ps3.setInt(2, u2);
        ps3.executeUpdate();

        return id;
    }

    public static void deleteConversation(int id) throws Exception {
        Connection conn = DBConnection.getConnection();

        conn.createStatement().executeUpdate("DELETE FROM Messages WHERE ConversationID=" + id);
        conn.createStatement().executeUpdate("DELETE FROM ConversationMembers WHERE ConversationID=" + id);
        conn.createStatement().executeUpdate("DELETE FROM Conversations WHERE ConversationID=" + id);
    }

    public static int createGroup(String name, int[] members) throws Exception {
        Connection conn = DBConnection.getConnection();

        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO Conversations(Name, IsGroup) VALUES (?,1)",
            Statement.RETURN_GENERATED_KEYS
        );

        ps.setString(1, name);
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        rs.next();
        int id = rs.getInt(1);

        PreparedStatement ps2 = conn.prepareStatement(
            "INSERT INTO ConversationMembers VALUES (?,?)"
        );

        for (int m : members) {
            ps2.setInt(1, id);
            ps2.setInt(2, m);
            ps2.executeUpdate();
        }

        return id;
    }

    public static void sendMessage(int cid, int sender, String msg) throws Exception {
        Connection conn = DBConnection.getConnection();

        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO Messages(ConversationID, SenderID, Content) VALUES (?,?,?)"
        );

        ps.setInt(1, cid);
        ps.setInt(2, sender);
        ps.setString(3, msg);
        ps.executeUpdate();
    }

    public static ResultSet getMessages(int cid) throws Exception {
        Connection conn = DBConnection.getConnection();

        PreparedStatement ps = conn.prepareStatement(
            "SELECT m.Content, u.Username FROM Messages m " +
            "JOIN Users u ON m.SenderID=u.UserID " +
            "WHERE ConversationID=? ORDER BY MessageID"
        );

        ps.setInt(1, cid);
        return ps.executeQuery();
    }
}