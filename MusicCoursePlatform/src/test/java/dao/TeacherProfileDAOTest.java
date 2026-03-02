package dao;

import model.TeacherProfile;
import model.User;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TeacherProfileDAOTest {

    private static TeacherProfileDAO teacherProfileDAO;
    private static UserDAO userDAO;
    private static User testUser;
    private static TeacherProfile testProfile;

    @BeforeAll
    static void setUp() {
        teacherProfileDAO = new TeacherProfileDAO();
        userDAO = new UserDAO();
        
        testUser = new User("test_teacher_" + System.currentTimeMillis(), 
                           "hashedpassword", 
                           "test_teacher_" + System.currentTimeMillis() + "@test.com", 
                           "TEACHER");
        userDAO.create(testUser);
    }

    @AfterAll
    static void tearDown() {
        if (testProfile != null && testProfile.getTeacherProfileId() > 0) {
            teacherProfileDAO.delete(testProfile.getTeacherProfileId());
        }
        if (testUser != null && testUser.getUserId() > 0) {
            userDAO.delete(testUser.getUserId());
        }
    }

    @Test
    @Order(1)
    void testCreate() {
        testProfile = new TeacherProfile(testUser.getUserId(), "Piano");
        testProfile.setBiography("Test biography");
        testProfile.setYearsExperience(5);
        testProfile.setHourlyRate(50);
        testProfile.setLocation("Test City");
        
        boolean result = teacherProfileDAO.create(testProfile);
        
        assertTrue(result);
        assertTrue(testProfile.getTeacherProfileId() > 0);
    }

    @Test
    @Order(2)
    void testFindById() {
        TeacherProfile found = teacherProfileDAO.findById(testProfile.getTeacherProfileId());
        
        assertNotNull(found);
        assertEquals(testProfile.getTeacherProfileId(), found.getTeacherProfileId());
        assertEquals("Piano", found.getInstrumentsTaught());
    }

    @Test
    @Order(3)
    void testFindByUserId() {
        TeacherProfile found = teacherProfileDAO.findByUserId(testUser.getUserId());
        
        assertNotNull(found);
        assertEquals(testUser.getUserId(), found.getUserId());
    }

    @Test
    @Order(4)
    void testFindByInstrument() {
        List<TeacherProfile> profiles = teacherProfileDAO.findByInstrument("Piano");
        
        assertNotNull(profiles);
        assertTrue(profiles.stream().anyMatch(p -> p.getTeacherProfileId() == testProfile.getTeacherProfileId()));
    }

    @Test
    @Order(5)
    void testFindByLocation() {
        List<TeacherProfile> profiles = teacherProfileDAO.findByLocation("Test City");
        
        assertNotNull(profiles);
        assertTrue(profiles.stream().anyMatch(p -> p.getTeacherProfileId() == testProfile.getTeacherProfileId()));
    }

    @Test
    @Order(6)
    void testFindAll() {
        List<TeacherProfile> profiles = teacherProfileDAO.findAll();
        
        assertNotNull(profiles);
        assertTrue(profiles.size() >= 1);
    }

    @Test
    @Order(7)
    void testUpdate() {
        testProfile.setInstrumentsTaught("Piano,Guitar");
        testProfile.setHourlyRate(60);
        
        boolean result = teacherProfileDAO.update(testProfile);
        
        assertTrue(result);
        
        TeacherProfile updated = teacherProfileDAO.findById(testProfile.getTeacherProfileId());
        assertEquals("Piano,Guitar", updated.getInstrumentsTaught());
        assertEquals(60, updated.getHourlyRate());
    }

    @Test
    @Order(8)
    void testDelete() {
        TeacherProfile toDelete = new TeacherProfile(testUser.getUserId(), "Violin");
        teacherProfileDAO.create(toDelete);
        
        boolean result = teacherProfileDAO.delete(toDelete.getTeacherProfileId());
        
        assertTrue(result);
        assertNull(teacherProfileDAO.findById(toDelete.getTeacherProfileId()));
    }
}
