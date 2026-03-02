package dao;

import model.User;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserDAOTest {

    private static UserDAO userDAO;
    private User testUser;
    private String testPrefix;

    @BeforeAll
    static void setUpClass() {
        userDAO = new UserDAO();
    }

    @BeforeEach
    void setUp() {
        // Generate UNIQUE prefix for EACH test
        testPrefix = "test_" + System.currentTimeMillis() + "_";

        testUser = new User(
                testPrefix + "user",
                "hashed_password_123",
                testPrefix + "user@test.com",
                "LEARNER"
        );
    }

    @AfterEach
    void tearDown() {
        // Clean up safely
        if (testUser != null && testUser.getUserId() > 0) {
            userDAO.delete(testUser.getUserId());
        }
    }

    // ==================== CREATE Tests ====================

    @Test
    @Order(1)
    void testCreateUser_Success() {
        boolean result = userDAO.create(testUser);

        assertTrue(result);
        assertTrue(testUser.getUserId() > 0);
    }

    @Test
    @Order(2)
    void testCreateUser_DuplicateUsername() {
        userDAO.create(testUser);

        User duplicateUser = new User(
                testUser.getUsername(),
                "different_password",
                testPrefix + "different@test.com",
                "TEACHER"
        );

        boolean result = userDAO.create(duplicateUser);
        assertFalse(result);
    }

    @Test
    @Order(3)
    void testCreateUser_DuplicateEmail() {
        userDAO.create(testUser);

        User duplicateUser = new User(
                testPrefix + "different_user",
                "different_password",
                testUser.getEmail(),
                "TEACHER"
        );

        boolean result = userDAO.create(duplicateUser);
        assertFalse(result);
    }

    // ==================== READ Tests ====================

    @Test
    @Order(4)
    void testFindById_Success() {
        userDAO.create(testUser);

        User foundUser = userDAO.findById(testUser.getUserId());

        assertNotNull(foundUser);
        assertEquals(testUser.getUsername(), foundUser.getUsername());
        assertEquals(testUser.getEmail(), foundUser.getEmail());
        assertEquals(testUser.getUserType(), foundUser.getUserType());
    }

    @Test
    @Order(5)
    void testFindById_NotFound() {
        User foundUser = userDAO.findById(999999);
        assertNull(foundUser);
    }

    @Test
    @Order(6)
    void testFindByUsername_Success() {
        userDAO.create(testUser);

        User foundUser = userDAO.findByUsername(testUser.getUsername());

        assertNotNull(foundUser);
        assertEquals(testUser.getUserId(), foundUser.getUserId());
    }

    @Test
    @Order(7)
    void testFindByEmail_Success() {
        userDAO.create(testUser);

        User foundUser = userDAO.findByEmail(testUser.getEmail());

        assertNotNull(foundUser);
        assertEquals(testUser.getUserId(), foundUser.getUserId());
    }

    @Test
    @Order(8)
    void testFindAll() {
        userDAO.create(testUser);

        List<User> users = userDAO.findAll();

        assertNotNull(users);
        assertFalse(users.isEmpty());
    }

    @Test
    @Order(9)
    void testFindByUserType() {
        testUser.setUserType("TEACHER");
        userDAO.create(testUser);

        List<User> teachers = userDAO.findByUserType("TEACHER");

        assertNotNull(teachers);
        assertTrue(
                teachers.stream()
                        .anyMatch(u -> u.getUserId() == testUser.getUserId())
        );
    }

    // ==================== UPDATE Tests ====================

    @Test
    @Order(10)
    void testUpdateUser_Success() {
        userDAO.create(testUser);

        String newEmail = testPrefix + "updated@test.com";
        testUser.setEmail(newEmail);
        testUser.setUserType("TEACHER");

        boolean result = userDAO.update(testUser);

        assertTrue(result);

        User updatedUser = userDAO.findById(testUser.getUserId());
        assertEquals(newEmail, updatedUser.getEmail());
        assertEquals("TEACHER", updatedUser.getUserType());
    }

    // ==================== DELETE Tests ====================

    @Test
    @Order(11)
    void testDeleteUser_Success() {
        userDAO.create(testUser);
        int userId = testUser.getUserId();

        boolean result = userDAO.delete(userId);

        assertTrue(result);
        assertNull(userDAO.findById(userId));

        // Prevent tearDown from trying again
        testUser.setUserId(0);
    }

    @Test
    @Order(12)
    void testDeleteUser_NotFound() {
        boolean result = userDAO.delete(999999);
        assertFalse(result);
    }

    // ==================== Utility Tests ====================

    @Test
    @Order(13)
    void testUsernameExists() {
        userDAO.create(testUser);

        assertTrue(userDAO.usernameExists(testUser.getUsername()));
        assertFalse(userDAO.usernameExists("nonexistent_user_xyz"));
    }

    @Test
    @Order(14)
    void testEmailExists() {
        userDAO.create(testUser);

        assertTrue(userDAO.emailExists(testUser.getEmail()));
        assertFalse(userDAO.emailExists("nonexistent@xyz.com"));
    }

    @Test
    @Order(15)
    void testCountAll() {
        int initialCount = userDAO.countAll();

        userDAO.create(testUser);

        int newCount = userDAO.countAll();

        assertEquals(initialCount + 1, newCount);
    }
}