package dao;

import model.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookingDAOTest {

    private static BookingDAO bookingDAO;
    private static TimeSlotDAO timeSlotDAO;
    private static TeacherProfileDAO teacherProfileDAO;
    private static LearnerProfileDAO learnerProfileDAO;
    private static UserDAO userDAO;

    private static User testTeacher;
    private static User testLearner;
    private static TeacherProfile testTeacherProfile;
    private static LearnerProfile testLearnerProfile;
    private static TimeSlot testSlot;
    private static Booking testBooking;

    @BeforeAll
    static void setUp() {
        bookingDAO = new BookingDAO();
        timeSlotDAO = new TimeSlotDAO();
        teacherProfileDAO = new TeacherProfileDAO();
        learnerProfileDAO = new LearnerProfileDAO();
        userDAO = new UserDAO();

        long timestamp = System.currentTimeMillis();

        // Create Teacher
        testTeacher = new User(
                "test_booking_teacher_" + timestamp,
                "hashedpassword",
                "test_booking_teacher_" + timestamp + "@test.com",
                "TEACHER"
        );
        userDAO.create(testTeacher);

        // Create Learner
        testLearner = new User(
                "test_booking_learner_" + timestamp,
                "hashedpassword",
                "test_booking_learner_" + timestamp + "@test.com",
                "LEARNER"
        );
        userDAO.create(testLearner);

        // Create Teacher Profile (ALL required fields!)
        testTeacherProfile = new TeacherProfile(testTeacher.getUserId(), "Piano");
        testTeacherProfile.setBiography("Booking test biography");
        testTeacherProfile.setYearsExperience(5);
        testTeacherProfile.setHourlyRate(50);
        testTeacherProfile.setLocation("Test City");

        assertTrue(teacherProfileDAO.create(testTeacherProfile));
        assertTrue(testTeacherProfile.getTeacherProfileId() > 0);

        // Create Learner Profile
        testLearnerProfile = new LearnerProfile(testLearner.getUserId(), "Piano");
        assertTrue(learnerProfileDAO.create(testLearnerProfile));
        assertTrue(testLearnerProfile.getLearnerProfileId() > 0);

        // Create TimeSlot
        testSlot = new TimeSlot(
                testTeacherProfile.getTeacherProfileId(),
                LocalDate.now().plusDays(1),
                "10:00",
                "11:00"
        );
        assertTrue(timeSlotDAO.create(testSlot));
        assertTrue(testSlot.getSlotId() > 0);
    }

    @AfterAll
    static void tearDown() {

        if (testBooking != null && testBooking.getBookingId() > 0) {
            bookingDAO.delete(testBooking.getBookingId());
        }

        if (testSlot != null && testSlot.getSlotId() > 0) {
            timeSlotDAO.delete(testSlot.getSlotId());
        }

        if (testTeacherProfile != null && testTeacherProfile.getTeacherProfileId() > 0) {
            teacherProfileDAO.delete(testTeacherProfile.getTeacherProfileId());
        }

        if (testLearnerProfile != null && testLearnerProfile.getLearnerProfileId() > 0) {
            learnerProfileDAO.delete(testLearnerProfile.getLearnerProfileId());
        }

        if (testTeacher != null && testTeacher.getUserId() > 0) {
            userDAO.delete(testTeacher.getUserId());
        }

        if (testLearner != null && testLearner.getUserId() > 0) {
            userDAO.delete(testLearner.getUserId());
        }
    }

    @Test
    @Order(1)
    void testCreate() {

        testBooking = new Booking(
                testLearnerProfile.getLearnerProfileId(),
                testSlot.getSlotId()
        );

        testBooking.setNotes("Test booking");
        testBooking.setBookingStatus(Booking.STATUS_PENDING);

        boolean result = bookingDAO.create(testBooking);

        assertTrue(result);
        assertTrue(testBooking.getBookingId() > 0);
    }

    @Test
    @Order(2)
    void testFindById() {

        Booking found = bookingDAO.findById(testBooking.getBookingId());

        assertNotNull(found);
        assertEquals(testBooking.getBookingId(), found.getBookingId());
        assertEquals("Test booking", found.getNotes());
    }

    @Test
    @Order(3)
    void testFindByLearnerProfileId() {

        List<Booking> bookings =
                bookingDAO.findByLearnerProfileId(testLearnerProfile.getLearnerProfileId());

        assertNotNull(bookings);
        assertTrue(
                bookings.stream()
                        .anyMatch(b -> b.getBookingId() == testBooking.getBookingId())
        );
    }

    @Test
    @Order(4)
    void testFindBySlotId() {

        Booking found = bookingDAO.findBySlotId(testSlot.getSlotId());

        assertNotNull(found);
        assertEquals(testBooking.getBookingId(), found.getBookingId());
    }

    @Test
    @Order(5)
    void testFindByStatus() {

        List<Booking> bookings =
                bookingDAO.findByStatus(Booking.STATUS_PENDING);

        assertNotNull(bookings);
        assertTrue(
                bookings.stream()
                        .anyMatch(b -> b.getBookingId() == testBooking.getBookingId())
        );
    }

    @Test
    @Order(6)
    void testFindAll() {

        List<Booking> bookings = bookingDAO.findAll();

        assertNotNull(bookings);
        assertTrue(bookings.size() >= 1);
    }

    @Test
    @Order(7)
    void testUpdate() {

        testBooking.setNotes("Updated notes");

        boolean result = bookingDAO.update(testBooking);

        assertTrue(result);

        Booking updated = bookingDAO.findById(testBooking.getBookingId());
        assertEquals("Updated notes", updated.getNotes());
    }

    @Test
    @Order(8)
    void testUpdateStatus() {

        boolean result =
                bookingDAO.updateStatus(
                        testBooking.getBookingId(),
                        Booking.STATUS_CONFIRMED
                );

        assertTrue(result);

        Booking updated = bookingDAO.findById(testBooking.getBookingId());
        assertEquals(Booking.STATUS_CONFIRMED, updated.getBookingStatus());
    }

    @Test
    @Order(9)
    void testDelete() {

        TimeSlot tempSlot = new TimeSlot(
                testTeacherProfile.getTeacherProfileId(),
                LocalDate.now().plusDays(3),
                "14:00",
                "15:00"
        );

        assertTrue(timeSlotDAO.create(tempSlot));

        Booking toDelete = new Booking(
                testLearnerProfile.getLearnerProfileId(),
                tempSlot.getSlotId()
        );

        toDelete.setNotes("Temp booking");
        toDelete.setBookingStatus(Booking.STATUS_PENDING);

        assertTrue(bookingDAO.create(toDelete));

        boolean result = bookingDAO.delete(toDelete.getBookingId());

        assertTrue(result);
        assertNull(bookingDAO.findById(toDelete.getBookingId()));

        timeSlotDAO.delete(tempSlot.getSlotId());
    }
}