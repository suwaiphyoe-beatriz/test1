package model;

import java.time.LocalDate;

public class TimeSlot {
    
    public static final String STATUS_AVAILABLE = "AVAILABLE";
    public static final String STATUS_BOOKED = "BOOKED";
    
    private int slotId;
    private LocalDate lessonDate;
    private String startTime;
    private String endTime;
    private String slotStatus;
    private LocalDate createdAt;
    private int teacherProfileId;
    
    public TimeSlot() {
        this.slotStatus = STATUS_AVAILABLE;
    }
    
    public TimeSlot(int teacherProfileId, LocalDate lessonDate, String startTime, String endTime) {
        this.teacherProfileId = teacherProfileId;
        this.lessonDate = lessonDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.slotStatus = STATUS_AVAILABLE;
        this.createdAt = LocalDate.now();
    }
    
    public int getSlotId() {
        return slotId;
    }
    
    public void setSlotId(int slotId) {
        this.slotId = slotId;
    }
    
    public LocalDate getLessonDate() {
        return lessonDate;
    }
    
    public void setLessonDate(LocalDate lessonDate) {
        this.lessonDate = lessonDate;
    }
    
    public String getStartTime() {
        return startTime;
    }
    
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    
    public String getEndTime() {
        return endTime;
    }
    
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    
    public String getSlotStatus() {
        return slotStatus;
    }
    
    public void setSlotStatus(String slotStatus) {
        this.slotStatus = slotStatus;
    }
    
    public String getStatus() {
        return slotStatus;
    }
    
    public void setStatus(String status) {
        this.slotStatus = status;
    }
    
    public LocalDate getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
    
    public int getTeacherProfileId() {
        return teacherProfileId;
    }
    
    public void setTeacherProfileId(int teacherProfileId) {
        this.teacherProfileId = teacherProfileId;
    }
    
    public boolean isAvailable() {
        return STATUS_AVAILABLE.equals(slotStatus);
    }
    
    public boolean isBooked() {
        return STATUS_BOOKED.equals(slotStatus);
    }
    
    public void markAsBooked() {
        this.slotStatus = STATUS_BOOKED;
    }
    
    public void markAsAvailable() {
        this.slotStatus = STATUS_AVAILABLE;
    }
}
