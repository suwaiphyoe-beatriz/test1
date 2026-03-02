package model;

import java.time.LocalDate;

public class TeacherProfile {
    
    private int teacherProfileId;
    private String biography;
    private String instrumentsTaught;
    private int yearsExperience;
    private int hourlyRate;
    private String location;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private int userId;
    
    public TeacherProfile() {
    }
    
    public TeacherProfile(int userId, String instrumentsTaught) {
        this.userId = userId;
        this.instrumentsTaught = instrumentsTaught;
        this.yearsExperience = 0;
        this.hourlyRate = 0;
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }
    
    public int getTeacherProfileId() {
        return teacherProfileId;
    }
    
    public void setTeacherProfileId(int teacherProfileId) {
        this.teacherProfileId = teacherProfileId;
    }
    
    public int getProfileId() {
        return teacherProfileId;
    }
    
    public void setProfileId(int profileId) {
        this.teacherProfileId = profileId;
    }
    
    public String getBiography() {
        return biography;
    }
    
    public void setBiography(String biography) {
        this.biography = biography;
    }
    
    public String getInstrumentsTaught() {
        return instrumentsTaught;
    }
    
    public void setInstrumentsTaught(String instrumentsTaught) {
        this.instrumentsTaught = instrumentsTaught;
    }
    
    public int getYearsExperience() {
        return yearsExperience;
    }
    
    public void setYearsExperience(int yearsExperience) {
        this.yearsExperience = yearsExperience;
    }
    
    public int getHourlyRate() {
        return hourlyRate;
    }
    
    public void setHourlyRate(int hourlyRate) {
        this.hourlyRate = hourlyRate;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
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
    
    public String[] getInstrumentsList() {
        if (instrumentsTaught == null || instrumentsTaught.isEmpty()) {
            return new String[0];
        }
        return instrumentsTaught.split(",");
    }
}
