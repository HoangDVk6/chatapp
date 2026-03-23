package database;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() throws Exception {

        // 🔥 BẮT BUỘC: load driver
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

        String url = "jdbc:sqlserver://localhost\\SQLEXPRESS;"
                   + "databaseName=ChatOnlineDB;"
                   + "encrypt=true;"
                   + "trustServerCertificate=true";

        String user = "sa";
        String password = "123456789"; // sửa đúng pass của bạn

        Connection conn = DriverManager.getConnection(url, user, password);

        System.out.println("✅ Đã kết nối SQL Server");

        return conn;
    }
}