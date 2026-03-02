package service;

import dao.BookingDAO;
import dao.TimeSlotDAO;
import dao.LearnerProfileDAO;
import model.Booking;
import model.TimeSlot;
import model.LearnerProfile;

import java.util.List;

public class BookingService {

    private final BookingDAO bookingDAO;
    private final TimeSlotDAO timeSlotDAO;
    private final LearnerProfileDAO learnerProfileDAO;

    public BookingService() {
        this.bookingDAO = new BookingDAO();
        this.timeSlotDAO = new TimeSlotDAO();
        this.learnerProfileDAO = new LearnerProfileDAO();
    }

    public BookingService(BookingDAO bookingDAO, TimeSlotDAO timeSlotDAO, LearnerProfileDAO learnerProfileDAO) {
        this.bookingDAO = bookingDAO;
        this.timeSlotDAO = timeSlotDAO;
        this.learnerProfileDAO = learnerProfileDAO;
    }

    public Booking createBooking(int slotId, int learnerProfileId, String notes) {
        LearnerProfile learnerProfile = learnerProfileDAO.findById(learnerProfileId);
        if (learnerProfile == null) {
            throw new IllegalArgumentException("Learner profile not found");
        }

        TimeSlot slot = timeSlotDAO.findById(slotId);
        if (slot == null) {
            throw new IllegalArgumentException("Time slot not found");
        }
        if (!slot.isAvailable()) {
            throw new IllegalArgumentException("Time slot is not available");
        }

        Booking existingBooking = bookingDAO.findBySlotId(slotId);
        if (existingBooking != null) {
            throw new IllegalArgumentException("Time slot is already booked");
        }

        Booking booking = new Booking(learnerProfileId, slotId);
        booking.setNotes(notes);
        
        boolean success = bookingDAO.create(booking);
        if (!success) {
            throw new RuntimeException("Failed to create booking");
        }

        timeSlotDAO.updateStatus(slotId, TimeSlot.STATUS_BOOKED);

        return booking;
    }

    public Booking confirmBooking(int bookingId) {
        Booking booking = bookingDAO.findById(bookingId);
        if (booking == null) {
            throw new IllegalArgumentException("Booking not found");
        }
        if (!booking.isPending()) {
            throw new IllegalArgumentException("Booking is not in pending status");
        }

        booking.confirm();
        boolean success = bookingDAO.updateStatus(bookingId, Booking.STATUS_CONFIRMED);
        if (!success) {
            throw new RuntimeException("Failed to confirm booking");
        }

        return booking;
    }

    public Booking cancelBooking(int bookingId) {
        Booking booking = bookingDAO.findById(bookingId);
        if (booking == null) {
            throw new IllegalArgumentException("Booking not found");
        }
        if (booking.isCancelled()) {
            throw new IllegalArgumentException("Booking is already cancelled");
        }

        booking.cancel();
        boolean success = bookingDAO.updateStatus(bookingId, Booking.STATUS_CANCELLED);
        if (!success) {
            throw new RuntimeException("Failed to cancel booking");
        }

        timeSlotDAO.updateStatus(booking.getSlotId(), TimeSlot.STATUS_AVAILABLE);

        return booking;
    }

    public Booking getBookingById(int bookingId) {
        return bookingDAO.findById(bookingId);
    }

    public Booking getBookingBySlotId(int slotId) {
        return bookingDAO.findBySlotId(slotId);
    }

    public List<Booking> getBookingsByLearnerProfile(int learnerProfileId) {
        return bookingDAO.findByLearnerProfileId(learnerProfileId);
    }

    public List<Booking> getBookingsByStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be empty");
        }
        return bookingDAO.findByStatus(status);
    }

    public List<Booking> getPendingBookings() {
        return bookingDAO.findByStatus(Booking.STATUS_PENDING);
    }

    public List<Booking> getAllBookings() {
        return bookingDAO.findAll();
    }

    public boolean deleteBooking(int bookingId) {
        Booking booking = bookingDAO.findById(bookingId);
        if (booking == null) {
            throw new IllegalArgumentException("Booking not found");
        }

        if (!booking.isCancelled()) {
            timeSlotDAO.updateStatus(booking.getSlotId(), TimeSlot.STATUS_AVAILABLE);
        }

        return bookingDAO.delete(bookingId);
    }
}
