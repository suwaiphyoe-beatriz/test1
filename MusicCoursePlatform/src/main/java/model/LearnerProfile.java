package model;

import java.time.LocalDate;

public class LearnerProfile {
    
    private int learnerProfileId;
    private String instrument;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private int userId;
    
    public LearnerProfile() {
    }
    
    public LearnerProfile(int userId, String instrument) {
        this.userId = userId;
        this.instrument = instrument;
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }
    
    public int getLearnerProfileId() {
        return learnerProfileId;
    }
    
    public void setLearnerProfileId(int learnerProfileId) {
        this.learnerProfileId = learnerProfileId;
    }
    
    public String getInstrument() {
        return instrument;
    }
    
    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }
    
    public LocalDate getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDate getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
}
