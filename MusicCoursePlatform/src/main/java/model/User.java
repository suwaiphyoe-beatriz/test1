package model;

import java.time.LocalDateTime;

/**
 * User entity class representing a user in the Music Course Platform.
 * Users can be either TEACHER or LEARNER type.
 * 
 * @author Lu Liu
 * @version 1.0 (Sprint 2)
 */
public class User {
    
    private int userId;
    private String username;
    private String passwordHash;
    private String email;
    private String userType; // "TEACHER" or "LEARNER"
    private LocalDateTime createdAt;

    /**
     * Default constructor
     */
    public User() {
    }

    /**
     * Constructor with essential fields (for registration)
     * 
     * @param username User's unique username
     * @param passwordHash Hashed password
     * @param email User's email address
     * @param userType Type of user: "TEACHER" or "LEARNER"
     */
    public User(String username, String passwordHash, String email, String userType) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.userType = userType;
    }

    /**
     * Full constructor with all fields
     */
    public User(int userId, String username, String passwordHash, String email, 
                String userType, LocalDateTime createdAt) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.userType = userType;
        this.createdAt = createdAt;
    }

    // ==================== Getters and Setters ====================

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // ==================== Utility Methods ====================

    /**
     * Check if user is a teacher
     * @return true if user type is TEACHER
     */
    public boolean isTeacher() {
        return "TEACHER".equals(userType);
    }

    /**
     * Check if user is a learner
     * @return true if user type is LEARNER
     */
    public boolean isLearner() {
        return "LEARNER".equals(userType);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", userType='" + userType + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}

