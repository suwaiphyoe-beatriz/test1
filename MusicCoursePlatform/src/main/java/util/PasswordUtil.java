package util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class for secure password hashing and verification using BCrypt.
 * BCrypt is a strong, adaptive hash function designed specifically for password hashing.
 *
 * @author CHEN Yicheng
 * @version 1.0 (Sprint 2)
 */
public class PasswordUtil {

    // BCrypt work factor (log2 of number of rounds)
    // Higher values = more secure but slower
    // 12 is a good balance between security and performance
    private static final int WORK_FACTOR = 12;

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static methods.
     */
    private PasswordUtil() {
    }

    /**
     * Hash a plain text password using BCrypt with a generated salt.
     *
     * @param plainPassword The plain text password to hash
     * @return The hashed password (includes salt)
     * @throws IllegalArgumentException if password is null or empty
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        // BCrypt.hashpw generates a salt and hashes the password
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(WORK_FACTOR));
    }

    /**
     * Verify a plain text password against a hashed password.
     *
     * @param plainPassword The plain text password to verify
     * @param hashedPassword The hashed password to compare against
     * @return true if the passwords match, false otherwise
     * @throws IllegalArgumentException if either parameter is null or empty
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Plain password cannot be null or empty");
        }
        if (hashedPassword == null || hashedPassword.isEmpty()) {
            throw new IllegalArgumentException("Hashed password cannot be null or empty");
        }

        try {
            // BCrypt.checkpw compares the plain password with the hashed one
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // If the hashed password is invalid format, return false
            return false;
        }
    }

    /**
     * Check if a password meets minimum strength requirements.
     *
     * @param password The password to check
     * @return true if password meets requirements, false otherwise
     */
    public static boolean isPasswordStrong(String password) {
        if (password == null) {
            return false;
        }

        // Minimum length of 8 characters
        return password.length() >= 8;
    }
}
