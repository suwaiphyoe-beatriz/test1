package model;

import java.time.LocalDate;

public class Booking {
    
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_CONFIRMED = "CONFIRMED";
    public static final String STATUS_CANCELLED = "CANCELLED";
    
    private int bookingId;
    private LocalDate bookingDate;
    private String bookingStatus;
    private String notes;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private int learnerProfileId;
    private int slotId;
    
    public Booking() {
        this.bookingStatus = STATUS_PENDING;
        this.bookingDate = LocalDate.now();
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }
    
    public Booking(int learnerProfileId, int slotId) {
        this();
        this.learnerProfileId = learnerProfileId;
        this.slotId = slotId;
    }
    
    public int getBookingId() {
        return bookingId;
    }
    
    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }
    
    public LocalDate getBookingDate() {
        return bookingDate;
    }
    
    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }
    
    public String getBookingStatus() {
        return bookingStatus;
    }
    
    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }
    
    public String getStatus() {
        return bookingStatus;
    }
    
    public void setStatus(String status) {
        this.bookingStatus = status;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
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
    
    public int getLearnerProfileId() {
        return learnerProfileId;
    }
    
    public void setLearnerProfileId(int learnerProfileId) {
        this.learnerProfileId = learnerProfileId;
    }
    
    public int getSlotId() {
        return slotId;
    }
    
    public void setSlotId(int slotId) {
        this.slotId = slotId;
    }
    
    public boolean isPending() {
        return STATUS_PENDING.equals(bookingStatus);
    }
    
    public boolean isConfirmed() {
        return STATUS_CONFIRMED.equals(bookingStatus);
    }
    
    public boolean isCancelled() {
        return STATUS_CANCELLED.equals(bookingStatus);
    }
    
    public void confirm() {
        this.bookingStatus = STATUS_CONFIRMED;
        this.updatedAt = LocalDate.now();
    }
    
    public void cancel() {
        this.bookingStatus = STATUS_CANCELLED;
        this.updatedAt = LocalDate.now();
    }
}
