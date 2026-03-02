package service;

import dao.TimeSlotDAO;
import dao.TeacherProfileDAO;
import model.TimeSlot;
import model.TeacherProfile;

import java.time.LocalDate;
import java.util.List;

public class TimeSlotService {

    private final TimeSlotDAO timeSlotDAO;
    private final TeacherProfileDAO teacherProfileDAO;

    public TimeSlotService() {
        this.timeSlotDAO = new TimeSlotDAO();
        this.teacherProfileDAO = new TeacherProfileDAO();
    }

    public TimeSlotService(TimeSlotDAO timeSlotDAO, TeacherProfileDAO teacherProfileDAO) {
        this.timeSlotDAO = timeSlotDAO;
        this.teacherProfileDAO = teacherProfileDAO;
    }

    public TimeSlot createTimeSlot(int teacherProfileId, LocalDate lessonDate, 
                                    String startTime, String endTime) {
        TeacherProfile profile = teacherProfileDAO.findById(teacherProfileId);
        if (profile == null) {
            throw new IllegalArgumentException("Teacher profile not found");
        }

        validateTimeSlot(lessonDate, startTime, endTime);
        checkForOverlap(teacherProfileId, lessonDate, startTime, endTime, -1);

        TimeSlot slot = new TimeSlot(teacherProfileId, lessonDate, startTime, endTime);
        
        boolean success = timeSlotDAO.create(slot);
        if (!success) {
            throw new RuntimeException("Failed to create time slot");
        }

        return slot;
    }

    public TimeSlot updateTimeSlot(int slotId, LocalDate lessonDate,
                                    String startTime, String endTime) {
        TimeSlot slot = timeSlotDAO.findById(slotId);
        if (slot == null) {
            throw new IllegalArgumentException("Time slot not found");
        }
        if (slot.isBooked()) {
            throw new IllegalArgumentException("Cannot modify a booked time slot");
        }

        validateTimeSlot(lessonDate, startTime, endTime);
        checkForOverlap(slot.getTeacherProfileId(), lessonDate, startTime, endTime, slotId);

        slot.setLessonDate(lessonDate);
        slot.setStartTime(startTime);
        slot.setEndTime(endTime);

        boolean success = timeSlotDAO.update(slot);
        if (!success) {
            throw new RuntimeException("Failed to update time slot");
        }

        return slot;
    }

    public TimeSlot getTimeSlotById(int slotId) {
        return timeSlotDAO.findById(slotId);
    }

    public List<TimeSlot> getTimeSlotsByTeacherProfile(int teacherProfileId) {
        return timeSlotDAO.findByTeacherProfileId(teacherProfileId);
    }

    public List<TimeSlot> getTimeSlotsByTeacherProfileAndDate(int teacherProfileId, LocalDate date) {
        return timeSlotDAO.findByTeacherProfileIdAndDate(teacherProfileId, date);
    }

    public List<TimeSlot> getAvailableSlotsByTeacherProfile(int teacherProfileId) {
        return timeSlotDAO.findAvailableByTeacherProfileId(teacherProfileId);
    }

    public List<TimeSlot> getAvailableSlotsByDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        return timeSlotDAO.findAvailableByDate(date);
    }

    public List<TimeSlot> getAllTimeSlots() {
        return timeSlotDAO.findAll();
    }

    public boolean markAsBooked(int slotId) {
        TimeSlot slot = timeSlotDAO.findById(slotId);
        if (slot == null) {
            throw new IllegalArgumentException("Time slot not found");
        }
        if (slot.isBooked()) {
            throw new IllegalArgumentException("Time slot is already booked");
        }
        return timeSlotDAO.updateStatus(slotId, TimeSlot.STATUS_BOOKED);
    }

    public boolean markAsAvailable(int slotId) {
        TimeSlot slot = timeSlotDAO.findById(slotId);
        if (slot == null) {
            throw new IllegalArgumentException("Time slot not found");
        }
        return timeSlotDAO.updateStatus(slotId, TimeSlot.STATUS_AVAILABLE);
    }

    public boolean deleteTimeSlot(int slotId) {
        TimeSlot slot = timeSlotDAO.findById(slotId);
        if (slot == null) {
            throw new IllegalArgumentException("Time slot not found");
        }
        if (slot.isBooked()) {
            throw new IllegalArgumentException("Cannot delete a booked time slot");
        }
        return timeSlotDAO.delete(slotId);
    }

    private void validateTimeSlot(LocalDate lessonDate, String startTime, String endTime) {
        if (lessonDate == null) {
            throw new IllegalArgumentException("Lesson date cannot be null");
        }
        if (startTime == null || endTime == null || startTime.isEmpty() || endTime.isEmpty()) {
            throw new IllegalArgumentException("Start time and end time cannot be empty");
        }
        if (lessonDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Lesson date cannot be in the past");
        }
    }

    private void checkForOverlap(int teacherProfileId, LocalDate date, String startTime, 
                                  String endTime, int excludeSlotId) {
        List<TimeSlot> existingSlots = timeSlotDAO.findByTeacherProfileIdAndDate(teacherProfileId, date);
        
        for (TimeSlot existing : existingSlots) {
            if (existing.getSlotId() == excludeSlotId) {
                continue;
            }
            if (startTime.compareTo(existing.getEndTime()) < 0 && endTime.compareTo(existing.getStartTime()) > 0) {
                throw new IllegalArgumentException("Time slot overlaps with existing slot");
            }
        }
    }
}
