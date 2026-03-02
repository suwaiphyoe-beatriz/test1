package dao;

import model.TeacherProfile;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeacherProfileDAO {

    public boolean create(TeacherProfile profile) {
        String sql = "INSERT INTO TEACHERPROFILE (biography, instruments_taught, years_experience, hourly_rate, location, user_id) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, profile.getBiography());
            stmt.setString(2, profile.getInstrumentsTaught());
            stmt.setInt(3, profile.getYearsExperience());
            stmt.setInt(4, profile.getHourlyRate());
            stmt.setString(5, profile.getLocation());
            stmt.setInt(6, profile.getUserId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        profile.setTeacherProfileId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error creating teacher profile: " + e.getMessage());
            return false;
        }
    }

    public TeacherProfile findById(int teacherProfileId) {
        String sql = "SELECT * FROM TEACHERPROFILE WHERE teacher_profile_id = ?";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, teacherProfileId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTeacherProfile(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding teacher profile by ID: " + e.getMessage());
        }
        return null;
    }

    public TeacherProfile findByUserId(int userId) {
        String sql = "SELECT * FROM TEACHERPROFILE WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTeacherProfile(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding teacher profile by user ID: " + e.getMessage());
        }
        return null;
    }

    public List<TeacherProfile> findAll() {
        List<TeacherProfile> profiles = new ArrayList<>();
        String sql = "SELECT * FROM TEACHERPROFILE ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                profiles.add(mapResultSetToTeacherProfile(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all teacher profiles: " + e.getMessage());
        }
        return profiles;
    }

    public List<TeacherProfile> findByInstrument(String instrument) {
        List<TeacherProfile> profiles = new ArrayList<>();
        String sql = "SELECT * FROM TEACHERPROFILE WHERE instruments_taught LIKE ?";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + instrument + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    profiles.add(mapResultSetToTeacherProfile(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding teacher profiles by instrument: " + e.getMessage());
        }
        return profiles;
    }

    public List<TeacherProfile> findByLocation(String location) {
        List<TeacherProfile> profiles = new ArrayList<>();
        String sql = "SELECT * FROM TEACHERPROFILE WHERE location LIKE ?";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + location + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    profiles.add(mapResultSetToTeacherProfile(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding teacher profiles by location: " + e.getMessage());
        }
        return profiles;
    }

    public boolean update(TeacherProfile profile) {
        String sql = "UPDATE TEACHERPROFILE SET biography = ?, instruments_taught = ?, years_experience = ?, hourly_rate = ?, location = ?, updated_at = CURRENT_DATE WHERE teacher_profile_id = ?";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, profile.getBiography());
            stmt.setString(2, profile.getInstrumentsTaught());
            stmt.setInt(3, profile.getYearsExperience());
            stmt.setInt(4, profile.getHourlyRate());
            stmt.setString(5, profile.getLocation());
            stmt.setInt(6, profile.getTeacherProfileId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating teacher profile: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int teacherProfileId) {
        String sql = "DELETE FROM TEACHERPROFILE WHERE teacher_profile_id = ?";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, teacherProfileId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting teacher profile: " + e.getMessage());
            return false;
        }
    }

    private TeacherProfile mapResultSetToTeacherProfile(ResultSet rs) throws SQLException {
        TeacherProfile profile = new TeacherProfile();
        profile.setTeacherProfileId(rs.getInt("teacher_profile_id"));
        profile.setBiography(rs.getString("biography"));
        profile.setInstrumentsTaught(rs.getString("instruments_taught"));
        profile.setYearsExperience(rs.getInt("years_experience"));
        profile.setHourlyRate(rs.getInt("hourly_rate"));
        profile.setLocation(rs.getString("location"));
        profile.setUserId(rs.getInt("user_id"));
        
        Date createdAt = rs.getDate("created_at");
        if (createdAt != null) {
            profile.setCreatedAt(createdAt.toLocalDate());
        }
        
        Date updatedAt = rs.getDate("updated_at");
        if (updatedAt != null) {
            profile.setUpdatedAt(updatedAt.toLocalDate());
        }
        
        return profile;
    }
}
