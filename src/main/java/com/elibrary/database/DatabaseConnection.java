package com.elibrary.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection manager for the E-Library application
 * Handles MySQL database connections using singleton pattern
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/elibrary_db?useSSL=false&serverTimezone=UTC";
    private static final String USERNAME = "root"; // Change as needed
    private static final String PASSWORD = ""; // Change as needed
    
    private static DatabaseConnection instance;
    private Connection connection;
    
    /**
     * Private constructor to prevent instantiation
     */
    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Database connection established successfully!");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Database Connection Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Get singleton instance of DatabaseConnection
     * @return DatabaseConnection instance
     */
    public static DatabaseConnection getInstance() {
        if (instance == null || instance.getConnection() == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null || instance.getConnection() == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }
    
    /**
     * Get the active database connection
     * @return Connection object
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("Error getting connection: " + e.getMessage());
        }
        return connection;
    }
    
    /**
     * Close the database connection
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * Test database connection
     * @return true if connection is active, false otherwise
     */
    public boolean testConnection() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
