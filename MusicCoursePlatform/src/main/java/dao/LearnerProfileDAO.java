package dao;

import model.LearnerProfile;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LearnerProfileDAO {

    public boolean create(LearnerProfile profile) {
        String sql = "INSERT INTO LEARNERPROFILE (instrument, user_id) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, profile.getInstrument());
            stmt.setInt(2, profile.getUserId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        profile.setLearnerProfileId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error creating learner profile: " + e.getMessage());
            return false;
        }
    }

    public LearnerProfile findById(int learnerProfileId) {
        String sql = "SELECT * FROM LEARNERPROFILE WHERE learner_profile_id = ?";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, learnerProfileId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLearnerProfile(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding learner profile by ID: " + e.getMessage());
        }
        return null;
    }

    public LearnerProfile findByUserId(int userId) {
        String sql = "SELECT * FROM LEARNERPROFILE WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLearnerProfile(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding learner profile by user ID: " + e.getMessage());
        }
        return null;
    }

    public List<LearnerProfile> findAll() {
        List<LearnerProfile> profiles = new ArrayList<>();
        String sql = "SELECT * FROM LEARNERPROFILE ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                profiles.add(mapResultSetToLearnerProfile(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all learner profiles: " + e.getMessage());
        }
        return profiles;
    }

    public boolean update(LearnerProfile profile) {
        String sql = "UPDATE LEARNERPROFILE SET instrument = ?, updated_at = CURRENT_DATE WHERE learner_profile_id = ?";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, profile.getInstrument());
            stmt.setInt(2, profile.getLearnerProfileId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating learner profile: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int learnerProfileId) {
        String sql = "DELETE FROM LEARNERPROFILE WHERE learner_profile_id = ?";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, learnerProfileId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting learner profile: " + e.getMessage());
            return false;
        }
    }

    private LearnerProfile mapResultSetToLearnerProfile(ResultSet rs) throws SQLException {
        LearnerProfile profile = new LearnerProfile();
        profile.setLearnerProfileId(rs.getInt("learner_profile_id"));
        profile.setInstrument(rs.getString("instrument"));
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
