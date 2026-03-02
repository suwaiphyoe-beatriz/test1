package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection utility class for Music Course Platform.
 * Provides methods to obtain and manage database connections.
 * 
 * @author Lu Liu
 * @version 1.0 (Sprint 2)
 */
public class DatabaseConnection {
    
    // Database configuration
    private static final String URL = "jdbc:mariadb://localhost:3306/music_lesson_db";
    private static final String USER = "root";
    private static final String PASSWORD = "swp123";
    
    // Connection instance for connection pooling (simple implementation)
    private static Connection connection = null;

    /**
     * Private constructor to prevent instantiation
     */
    private DatabaseConnection() {
    }

    /**
     * Get a database connection.
     * Creates a new connection if none exists or if the existing one is closed.
     * 
     * @return A valid database connection
     * @throws SQLException if connection cannot be established
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Load the MariaDB driver
                Class.forName("org.mariadb.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException("MariaDB JDBC Driver not found", e);
            }
        }
        return connection;
    }

    /**
     * Get a new database connection (does not reuse existing connection).
     * Useful for transactions that need independent connections.
     * 
     * @return A new database connection
     * @throws SQLException if connection cannot be established
     */
    public static Connection getNewConnection() throws SQLException {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MariaDB JDBC Driver not found", e);
        }
    }

    /**
     * Close the shared database connection.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }

    /**
     * Test database connection.
     * 
     * @return true if connection is successful, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getNewConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }
}

