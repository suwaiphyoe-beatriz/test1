package dao;

import model.User;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UserDAO class.
 * Tests CRUD operations for user management.
 * 
 * Note: These tests require a running MariaDB database with the schema loaded.
 * 
 * @author Lu Liu
 * @version 1.0 (Sprint 2)
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserDAOTest {

    private static UserDAO userDAO;
    private static User testUser;
    private static final String TEST_PREFIX = "test_" + System.currentTimeMillis() + "_";

    @BeforeAll
    static void setUpClass() {
        userDAO = new UserDAO();
    }

    @BeforeEach
    void setUp() {
        // Create a fresh test user for each test
        testUser = new User(
            TEST_PREFIX + "user",
            "hashed_password_123",
            TEST_PREFIX + "user@test.com",
            "LEARNER"
        );
    }

    @AfterEach
    void tearDown() {
        // Clean up: delete test user if it exists
        if (testUser != null && testUser.getUserId() > 0) {
            userDAO.delete(testUser.getUserId());
        }
    }

    // ==================== CREATE Tests ====================

    @Test
    @Order(1)
    @DisplayName("Test: Create user successfully")
    void testCreateUser_Success() {
        boolean result = userDAO.create(testUser);
        
        assertTrue(result, "User creation should succeed");
        assertTrue(testUser.getUserId() > 0, "User ID should be set after creation");
    }

    @Test
    @Order(2)
    @DisplayName("Test: Create user with duplicate username fails")
    void testCreateUser_DuplicateUsername() {
        // Create first user
        userDAO.create(testUser);
        
        // Try to create second user with same username
        User duplicateUser = new User(
            testUser.getUsername(),  // Same username
            "different_password",
            "different@test.com",
            "TEACHER"
        );
        
        boolean result = userDAO.create(duplicateUser);
        assertFalse(result, "Creating user with duplicate username should fail");
    }

    @Test
    @Order(3)
    @DisplayName("Test: Create user with duplicate email fails")
    void testCreateUser_DuplicateEmail() {
        // Create first user
        userDAO.create(testUser);
        
        // Try to create second user with same email
        User duplicateUser = new User(
            "different_username_" + System.currentTimeMillis(),
            "different_password",
            testUser.getEmail(),  // Same email
            "TEACHER"
        );
        
        boolean result = userDAO.create(duplicateUser);
        assertFalse(result, "Creating user with duplicate email should fail");
    }

    // ==================== READ Tests ====================

    @Test
    @Order(4)
    @DisplayName("Test: Find user by ID")
    void testFindById_Success() {
        userDAO.create(testUser);
        
        User foundUser = userDAO.findById(testUser.getUserId());
        
        assertNotNull(foundUser, "Should find user by ID");
        assertEquals(testUser.getUsername(), foundUser.getUsername());
        assertEquals(testUser.getEmail(), foundUser.getEmail());
        assertEquals(testUser.getUserType(), foundUser.getUserType());
    }

    @Test
    @Order(5)
    @DisplayName("Test: Find user by ID - not found")
    void testFindById_NotFound() {
        User foundUser = userDAO.findById(999999);
        assertNull(foundUser, "Should return null for non-existent ID");
    }

    @Test
    @Order(6)
    @DisplayName("Test: Find user by username")
    void testFindByUsername_Success() {
        userDAO.create(testUser);
        
        User foundUser = userDAO.findByUsername(testUser.getUsername());
        
        assertNotNull(foundUser, "Should find user by username");
        assertEquals(testUser.getUserId(), foundUser.getUserId());
        assertEquals(testUser.getEmail(), foundUser.getEmail());
    }

    @Test
    @Order(7)
    @DisplayName("Test: Find user by username - not found")
    void testFindByUsername_NotFound() {
        User foundUser = userDAO.findByUsername("nonexistent_user_xyz_123");
        assertNull(foundUser, "Should return null for non-existent username");
    }

    @Test
    @Order(8)
    @DisplayName("Test: Find user by email")
    void testFindByEmail_Success() {
        userDAO.create(testUser);
        
        User foundUser = userDAO.findByEmail(testUser.getEmail());
        
        assertNotNull(foundUser, "Should find user by email");
        assertEquals(testUser.getUserId(), foundUser.getUserId());
        assertEquals(testUser.getUsername(), foundUser.getUsername());
    }

    @Test
    @Order(9)
    @DisplayName("Test: Find all users")
    void testFindAll() {
        userDAO.create(testUser);
        
        List<User> users = userDAO.findAll();
        
        assertNotNull(users, "User list should not be null");
        assertFalse(users.isEmpty(), "User list should not be empty");
    }

    @Test
    @Order(10)
    @DisplayName("Test: Find users by type")
    void testFindByUserType() {
        testUser.setUserType("TEACHER");
        userDAO.create(testUser);
        
        List<User> teachers = userDAO.findByUserType("TEACHER");
        
        assertNotNull(teachers, "Teacher list should not be null");
        assertTrue(teachers.stream().anyMatch(u -> u.getUserId() == testUser.getUserId()),
            "Should find the created teacher in the list");
    }

    // ==================== UPDATE Tests ====================

    @Test
    @Order(11)
    @DisplayName("Test: Update user successfully")
    void testUpdateUser_Success() {
        userDAO.create(testUser);
        
        // Update user information
        String newEmail = "updated_" + System.currentTimeMillis() + "@test.com";
        testUser.setEmail(newEmail);
        testUser.setUserType("TEACHER");
        
        boolean result = userDAO.update(testUser);
        
        assertTrue(result, "Update should succeed");
        
        // Verify update
        User updatedUser = userDAO.findById(testUser.getUserId());
        assertEquals(newEmail, updatedUser.getEmail(), "Email should be updated");
        assertEquals("TEACHER", updatedUser.getUserType(), "User type should be updated");
    }

    // ==================== DELETE Tests ====================

    @Test
    @Order(12)
    @DisplayName("Test: Delete user successfully")
    void testDeleteUser_Success() {
        userDAO.create(testUser);
        int userId = testUser.getUserId();
        
        boolean result = userDAO.delete(userId);
        
        assertTrue(result, "Delete should succeed");
        assertNull(userDAO.findById(userId), "User should not exist after deletion");
        
        // Set to 0 to prevent cleanup in tearDown
        testUser.setUserId(0);
    }

    @Test
    @Order(13)
    @DisplayName("Test: Delete non-existent user")
    void testDeleteUser_NotFound() {
        boolean result = userDAO.delete(999999);
        assertFalse(result, "Deleting non-existent user should return false");
    }

    // ==================== UTILITY Tests ====================

    @Test
    @Order(14)
    @DisplayName("Test: Username exists check")
    void testUsernameExists() {
        userDAO.create(testUser);
        
        assertTrue(userDAO.usernameExists(testUser.getUsername()), 
            "Should return true for existing username");
        assertFalse(userDAO.usernameExists("nonexistent_username_xyz"), 
            "Should return false for non-existing username");
    }

    @Test
    @Order(15)
    @DisplayName("Test: Email exists check")
    void testEmailExists() {
        userDAO.create(testUser);
        
        assertTrue(userDAO.emailExists(testUser.getEmail()), 
            "Should return true for existing email");
        assertFalse(userDAO.emailExists("nonexistent@xyz.com"), 
            "Should return false for non-existing email");
    }

    @Test
    @Order(16)
    @DisplayName("Test: Count all users")
    void testCountAll() {
        int initialCount = userDAO.countAll();
        
        userDAO.create(testUser);
        
        int newCount = userDAO.countAll();
        assertEquals(initialCount + 1, newCount, "Count should increase by 1 after adding user");
    }
}

