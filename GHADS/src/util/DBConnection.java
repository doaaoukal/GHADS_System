/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static DBConnection instance;
    private Connection connection;

    private static final String URL      = "jdbc:mysql://localhost:3306/ghads_db 1";
    private static final String USER     = "root";
    private static final String PASSWORD = "";

    private DBConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connected to database!");
        } catch (Exception e) {
            System.err.println("❌ Connection failed: " + e.getMessage());
        }
    }

    public static DBConnection getInstance() {
        try {
            if (instance == null || instance.connection.isClosed()) {
                instance = new DBConnection();
            }
        } catch (SQLException e) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}