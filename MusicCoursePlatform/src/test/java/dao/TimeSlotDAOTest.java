package dao;

import model.TeacherProfile;
import model.TimeSlot;
import model.User;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TimeSlotDAOTest {

    private static TimeSlotDAO timeSlotDAO;
    private static TeacherProfileDAO teacherProfileDAO;
    private static UserDAO userDAO;
    private static User testUser;
    private static TeacherProfile testTeacherProfile;
    private static TimeSlot testSlot;

    @BeforeAll
    static void setUp() {
        timeSlotDAO = new TimeSlotDAO();
        teacherProfileDAO = new TeacherProfileDAO();
        userDAO = new UserDAO();

        testUser = new User(
                "test_slot_teacher_" + System.currentTimeMillis(),
                "hashedpassword",
                "test_slot_teacher_" + System.currentTimeMillis() + "@test.com",
                "TEACHER"
        );
        userDAO.create(testUser);

        // Create FULL valid TeacherProfile
        testTeacherProfile = new TeacherProfile(testUser.getUserId(), "Piano");
        testTeacherProfile.setBiography("TimeSlot test biography");
        testTeacherProfile.setYearsExperience(5);
        testTeacherProfile.setHourlyRate(50);
        testTeacherProfile.setLocation("Test City");

        boolean created = teacherProfileDAO.create(testTeacherProfile);
        assertTrue(created);
        assertTrue(testTeacherProfile.getTeacherProfileId() > 0);
    }

    @AfterAll
    static void tearDown() {
        if (testSlot != null && testSlot.getSlotId() > 0) {
            timeSlotDAO.delete(testSlot.getSlotId());
        }
        if (testTeacherProfile != null && testTeacherProfile.getTeacherProfileId() > 0) {
            teacherProfileDAO.delete(testTeacherProfile.getTeacherProfileId());
        }
        if (testUser != null && testUser.getUserId() > 0) {
            userDAO.delete(testUser.getUserId());
        }
    }

    @Test
    @Order(1)
    void testCreate() {
        testSlot = new TimeSlot(testTeacherProfile.getTeacherProfileId(), 
                               LocalDate.now().plusDays(1), 
                               "10:00", 
                               "11:00");
        
        boolean result = timeSlotDAO.create(testSlot);
        
        assertTrue(result);
        assertTrue(testSlot.getSlotId() > 0);
    }

    @Test
    @Order(2)
    void testFindById() {
        TimeSlot found = timeSlotDAO.findById(testSlot.getSlotId());
        
        assertNotNull(found);
        assertEquals(testSlot.getSlotId(), found.getSlotId());
        assertEquals("10:00", found.getStartTime());
    }

    @Test
    @Order(3)
    void testFindByTeacherProfileId() {
        List<TimeSlot> slots = timeSlotDAO.findByTeacherProfileId(testTeacherProfile.getTeacherProfileId());
        
        assertNotNull(slots);
        assertTrue(slots.stream().anyMatch(s -> s.getSlotId() == testSlot.getSlotId()));
    }

    @Test
    @Order(4)
    void testFindByTeacherProfileIdAndDate() {
        List<TimeSlot> slots = timeSlotDAO.findByTeacherProfileIdAndDate(
            testTeacherProfile.getTeacherProfileId(), 
            LocalDate.now().plusDays(1)
        );
        
        assertNotNull(slots);
        assertTrue(slots.stream().anyMatch(s -> s.getSlotId() == testSlot.getSlotId()));
    }

    @Test
    @Order(5)
    void testFindAvailableByTeacherProfileId() {
        List<TimeSlot> slots = timeSlotDAO.findAvailableByTeacherProfileId(testTeacherProfile.getTeacherProfileId());
        
        assertNotNull(slots);
        assertTrue(slots.stream().anyMatch(s -> s.getSlotId() == testSlot.getSlotId()));
    }

    @Test
    @Order(6)
    void testFindAll() {
        List<TimeSlot> slots = timeSlotDAO.findAll();
        
        assertNotNull(slots);
        assertTrue(slots.size() >= 1);
    }

    @Test
    @Order(7)
    void testUpdate() {
        testSlot.setStartTime("11:00");
        testSlot.setEndTime("12:00");
        
        boolean result = timeSlotDAO.update(testSlot);
        
        assertTrue(result);
        
        TimeSlot updated = timeSlotDAO.findById(testSlot.getSlotId());
        assertEquals("11:00", updated.getStartTime());
        assertEquals("12:00", updated.getEndTime());
    }

    @Test
    @Order(8)
    void testUpdateStatus() {
        boolean result = timeSlotDAO.updateStatus(testSlot.getSlotId(), TimeSlot.STATUS_BOOKED);
        
        assertTrue(result);
        
        TimeSlot updated = timeSlotDAO.findById(testSlot.getSlotId());
        assertEquals(TimeSlot.STATUS_BOOKED, updated.getSlotStatus());
    }

    @Test
    @Order(9)
    void testDelete() {
        TimeSlot toDelete = new TimeSlot(testTeacherProfile.getTeacherProfileId(), 
                                        LocalDate.now().plusDays(2), 
                                        "14:00", 
                                        "15:00");
        timeSlotDAO.create(toDelete);
        
        boolean result = timeSlotDAO.delete(toDelete.getSlotId());
        
        assertTrue(result);
        assertNull(timeSlotDAO.findById(toDelete.getSlotId()));
    }
}
