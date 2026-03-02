package dao;

import model.Booking;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    public boolean create(Booking booking) {
        String sql = "INSERT INTO BOOKING (booking_date, booking_status, notes, learner_profile_id, slot_id) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setDate(1, Date.valueOf(booking.getBookingDate()));
            stmt.setString(2, booking.getBookingStatus());
            stmt.setString(3, booking.getNotes());
            stmt.setInt(4, booking.getLearnerProfileId());
            stmt.setInt(5, booking.getSlotId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        booking.setBookingId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error creating booking: " + e.getMessage());
            return false;
        }
    }

    public Booking findById(int bookingId) {
        String sql = "SELECT * FROM BOOKING WHERE booking_id = ?";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookingId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBooking(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding booking by ID: " + e.getMessage());
        }
        return null;
    }

    public List<Booking> findByLearnerProfileId(int learnerProfileId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM BOOKING WHERE learner_profile_id = ? ORDER BY booking_date DESC";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, learnerProfileId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapResultSetToBooking(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding bookings by learner profile ID: " + e.getMessage());
        }
        return bookings;
    }

    public Booking findBySlotId(int slotId) {
        String sql = "SELECT * FROM BOOKING WHERE slot_id = ?";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, slotId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBooking(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding booking by slot ID: " + e.getMessage());
        }
        return null;
    }

    public List<Booking> findByStatus(String status) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM BOOKING WHERE booking_status = ? ORDER BY booking_date DESC";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapResultSetToBooking(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding bookings by status: " + e.getMessage());
        }
        return bookings;
    }

    public List<Booking> findAll() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM BOOKING ORDER BY booking_date DESC";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all bookings: " + e.getMessage());
        }
        return bookings;
    }

    public boolean update(Booking booking) {
        String sql = "UPDATE BOOKING SET booking_status = ?, notes = ?, updated_at = CURRENT_DATE WHERE booking_id = ?";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, booking.getBookingStatus());
            stmt.setString(2, booking.getNotes());
            stmt.setInt(3, booking.getBookingId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating booking: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStatus(int bookingId, String status) {
        String sql = "UPDATE BOOKING SET booking_status = ?, updated_at = CURRENT_DATE WHERE booking_id = ?";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, bookingId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating booking status: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int bookingId) {
        String sql = "DELETE FROM BOOKING WHERE booking_id = ?";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookingId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting booking: " + e.getMessage());
            return false;
        }
    }

    private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setBookingId(rs.getInt("booking_id"));
        booking.setBookingDate(rs.getDate("booking_date").toLocalDate());
        booking.setBookingStatus(rs.getString("booking_status"));
        booking.setNotes(rs.getString("notes"));
        booking.setLearnerProfileId(rs.getInt("learner_profile_id"));
        booking.setSlotId(rs.getInt("slot_id"));
        
        Date createdAt = rs.getDate("created_at");
        if (createdAt != null) {
            booking.setCreatedAt(createdAt.toLocalDate());
        }
        
        Date updatedAt = rs.getDate("updated_at");
        if (updatedAt != null) {
            booking.setUpdatedAt(updatedAt.toLocalDate());
        }
        
        return booking;
    }
}
