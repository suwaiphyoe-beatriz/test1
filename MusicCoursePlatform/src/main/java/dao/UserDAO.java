package dao;

import model.User;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User entity.
 * Provides CRUD operations for the users table.
 * 
 * @author Lu Liu
 * @version 1.0 (Sprint 2)
 */
public class UserDAO {

    /**
     * Create a new user in the database.
     * 
     * @param user The user object to create
     * @return true if creation was successful, false otherwise
     */
    public boolean create(User user) {
        String sql = "INSERT INTO USERS (username, password_hash, email, user_type) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getUserType());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Get the generated user_id
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setUserId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Find a user by their unique ID.
     * 
     * @param userId The user's ID
     * @return User object if found, null otherwise
     */
    public User findById(int userId) {
        String sql = "SELECT * FROM USERS WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Find a user by their username.
     * Used for login authentication.
     * 
     * @param username The username to search for
     * @return User object if found, null otherwise
     */
    public User findByUsername(String username) {
        String sql = "SELECT * FROM USERS WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by username: " + e.getMessage());
        }
        return null;
    }

    /**
     * Find a user by their email address.
     * 
     * @param email The email to search for
     * @return User object if found, null otherwise
     */
    public User findByEmail(String email) {
        String sql = "SELECT * FROM USERS WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by email: " + e.getMessage());
        }
        return null;
    }

    /**
     * Get all users from the database.
     * 
     * @return List of all users
     */
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM USERS ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all users: " + e.getMessage());
        }
        return users;
    }

    /**
     * Get all users of a specific type (TEACHER or LEARNER).
     * 
     * @param userType The type of users to retrieve
     * @return List of users matching the type
     */
    public List<User> findByUserType(String userType) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM USERS WHERE user_type = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userType);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding users by type: " + e.getMessage());
        }
        return users;
    }

    /**
     * Update an existing user's information.
     * 
     * @param user The user object with updated information
     * @return true if update was successful, false otherwise
     */
    public boolean update(User user) {
        String sql = "UPDATE USERS SET username = ?, password_hash = ?, email = ?, user_type = ? WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getUserType());
            stmt.setInt(5, user.getUserId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete a user from the database.
     * 
     * @param userId The ID of the user to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean delete(int userId) {
        String sql = "DELETE FROM USERS WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if a username already exists in the database.
     * 
     * @param username The username to check
     * @return true if username exists, false otherwise
     */
    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM USERS WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking username existence: " + e.getMessage());
        }
        return false;
    }

    /**
     * Check if an email already exists in the database.
     * 
     * @param email The email to check
     * @return true if email exists, false otherwise
     */
    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM USERS WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking email existence: " + e.getMessage());
        }
        return false;
    }

    /**
     * Count total number of users.
     * 
     * @return Total count of users
     */
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM USERS";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting users: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Helper method to map a ResultSet row to a User object.
     * 
     * @param rs The ResultSet positioned at a valid row
     * @return User object populated with data from the ResultSet
     * @throws SQLException if database access error occurs
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setEmail(rs.getString("email"));
        user.setUserType(rs.getString("user_type"));
        
        Timestamp timestamp = rs.getTimestamp("created_at");
        if (timestamp != null) {
            user.setCreatedAt(timestamp.toLocalDateTime());
        }
        
        return user;
    }
}

