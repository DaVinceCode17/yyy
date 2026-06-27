package com.resources;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    
    private static Connection connection = null;
    
    public static Connection getConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            return connection;
        }
        
        try {
            String dbUrl, dbUser, dbPassword, driver;
            
            // RAILWAY: Use environment variables
            String railwayUrl = System.getenv("MYSQL_URL") != null ? System.getenv("MYSQL_URL") : System.getenv("DATABASE_URL");
            String railwayUser = System.getenv("MYSQL_USER") != null ? System.getenv("MYSQL_USER") : System.getenv("DATABASE_USERNAME");
            String railwayPass = System.getenv("MYSQL_PASSWORD") != null ? System.getenv("MYSQL_PASSWORD") : System.getenv("DATABASE_PASSWORD");
            
            if (railwayUrl != null && !railwayUrl.isEmpty()) {
                System.out.println("✅ Using Railway MySQL Database");
                dbUrl = railwayUrl;
                if (!dbUrl.startsWith("jdbc:")) {
                    dbUrl = "jdbc:mysql://" + dbUrl;
                }
                driver = "com.mysql.cj.jdbc.Driver";
                dbUser = railwayUser != null ? railwayUser : "";
                dbPassword = railwayPass != null ? railwayPass : "";
            } else {
                // LOCAL: MySQL
                System.out.println("✅ Using Local MySQL");
                driver = "com.mysql.cj.jdbc.Driver";
                dbUrl = "jdbc:mysql://localhost:3306/laundry_db?useSSL=false&serverTimezone=UTC";
                dbUser = "root";
                dbPassword = "";
            }
            
            Class.forName(driver);
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            System.out.println("✅ Database connected successfully!");
            
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver not found: " + e.getMessage());
            throw new SQLException("Driver not found");
        } catch (SQLException e) {
            System.err.println("❌ Connection failed: " + e.getMessage());
            throw e;
        }
        
        return connection;
    }
    
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
                System.out.println("✅ Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error closing: " + e.getMessage());
        }
    }
}