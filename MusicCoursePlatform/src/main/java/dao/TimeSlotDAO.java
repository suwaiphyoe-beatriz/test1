package dao;

import model.TimeSlot;
import util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TimeSlotDAO {

    public boolean create(TimeSlot slot) {
        String sql = "INSERT INTO TIMESLOT (lesson_date, start_time, end_time, slot_status, teacher_profile_id) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setDate(1, Date.valueOf(slot.getLessonDate()));
            stmt.setString(2, slot.getStartTime());
            stmt.setString(3, slot.getEndTime());
            stmt.setString(4, slot.getSlotStatus());
            stmt.setInt(5, slot.getTeacherProfileId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        slot.setSlotId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error creating time slot: " + e.getMessage());
            return false;
        }
    }

    public TimeSlot findById(int slotId) {
        String sql = "SELECT * FROM TIMESLOT WHERE slot_id = ?";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, slotId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTimeSlot(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding time slot by ID: " + e.getMessage());
        }
        return null;
    }

    public List<TimeSlot> findByTeacherProfileId(int teacherProfileId) {
        List<TimeSlot> slots = new ArrayList<>();
        String sql = "SELECT * FROM TIMESLOT WHERE teacher_profile_id = ? ORDER BY lesson_date, start_time";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, teacherProfileId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    slots.add(mapResultSetToTimeSlot(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding time slots by teacher profile ID: " + e.getMessage());
        }
        return slots;
    }

    public List<TimeSlot> findByTeacherProfileIdAndDate(int teacherProfileId, LocalDate date) {
        List<TimeSlot> slots = new ArrayList<>();
        String sql = "SELECT * FROM TIMESLOT WHERE teacher_profile_id = ? AND lesson_date = ? ORDER BY start_time";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, teacherProfileId);
            stmt.setDate(2, Date.valueOf(date));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    slots.add(mapResultSetToTimeSlot(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding time slots by teacher and date: " + e.getMessage());
        }
        return slots;
    }

    public List<TimeSlot> findAvailableByTeacherProfileId(int teacherProfileId) {
        List<TimeSlot> slots = new ArrayList<>();
        String sql = "SELECT * FROM TIMESLOT WHERE teacher_profile_id = ? AND slot_status = 'AVAILABLE' ORDER BY lesson_date, start_time";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, teacherProfileId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    slots.add(mapResultSetToTimeSlot(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding available time slots: " + e.getMessage());
        }
        return slots;
    }

    public List<TimeSlot> findAvailableByDate(LocalDate date) {
        List<TimeSlot> slots = new ArrayList<>();
        String sql = "SELECT * FROM TIMESLOT WHERE lesson_date = ? AND slot_status = 'AVAILABLE' ORDER BY start_time";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(date));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    slots.add(mapResultSetToTimeSlot(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding available time slots by date: " + e.getMessage());
        }
        return slots;
    }

    public List<TimeSlot> findAll() {
        List<TimeSlot> slots = new ArrayList<>();
        String sql = "SELECT * FROM TIMESLOT ORDER BY lesson_date, start_time";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                slots.add(mapResultSetToTimeSlot(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all time slots: " + e.getMessage());
        }
        return slots;
    }

    public boolean update(TimeSlot slot) {
        String sql = "UPDATE TIMESLOT SET lesson_date = ?, start_time = ?, end_time = ?, slot_status = ? WHERE slot_id = ?";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(slot.getLessonDate()));
            stmt.setString(2, slot.getStartTime());
            stmt.setString(3, slot.getEndTime());
            stmt.setString(4, slot.getSlotStatus());
            stmt.setInt(5, slot.getSlotId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating time slot: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStatus(int slotId, String status) {
        String sql = "UPDATE TIMESLOT SET slot_status = ? WHERE slot_id = ?";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, slotId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating time slot status: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int slotId) {
        String sql = "DELETE FROM TIMESLOT WHERE slot_id = ?";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, slotId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting time slot: " + e.getMessage());
            return false;
        }
    }

    private TimeSlot mapResultSetToTimeSlot(ResultSet rs) throws SQLException {
        TimeSlot slot = new TimeSlot();
        slot.setSlotId(rs.getInt("slot_id"));
        slot.setLessonDate(rs.getDate("lesson_date").toLocalDate());
        slot.setStartTime(rs.getString("start_time"));
        slot.setEndTime(rs.getString("end_time"));
        slot.setSlotStatus(rs.getString("slot_status"));
        slot.setTeacherProfileId(rs.getInt("teacher_profile_id"));
        
        Date createdAt = rs.getDate("created_at");
        if (createdAt != null) {
            slot.setCreatedAt(createdAt.toLocalDate());
        }
        
        return slot;
    }
}
