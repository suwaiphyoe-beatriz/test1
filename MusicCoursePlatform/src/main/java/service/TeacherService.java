package service;

import dao.TeacherProfileDAO;
import dao.UserDAO;
import model.TeacherProfile;
import model.User;

import java.util.List;

public class TeacherService {
    
    private final TeacherProfileDAO teacherProfileDAO;
    private final UserDAO userDAO;

    public TeacherService() {
        this.teacherProfileDAO = new TeacherProfileDAO();
        this.userDAO = new UserDAO();
    }

    public TeacherService(TeacherProfileDAO teacherProfileDAO, UserDAO userDAO) {
        this.teacherProfileDAO = teacherProfileDAO;
        this.userDAO = userDAO;
    }

    public TeacherProfile createProfile(int userId, String biography, String instruments,
                                         int yearsExperience, int hourlyRate, String location) {
        User user = userDAO.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        if (!user.isTeacher()) {
            throw new IllegalArgumentException("User is not a teacher");
        }

        TeacherProfile existing = teacherProfileDAO.findByUserId(userId);
        if (existing != null) {
            throw new IllegalArgumentException("Teacher profile already exists");
        }

        validateProfile(instruments, yearsExperience, hourlyRate);

        TeacherProfile profile = new TeacherProfile(userId, instruments);
        profile.setBiography(biography);
        profile.setYearsExperience(yearsExperience);
        profile.setHourlyRate(hourlyRate);
        profile.setLocation(location);
        
        boolean success = teacherProfileDAO.create(profile);
        if (!success) {
            throw new RuntimeException("Failed to create teacher profile");
        }
        
        return profile;
    }

    public TeacherProfile updateProfile(int profileId, String biography, String instruments,
                                         int yearsExperience, int hourlyRate, String location) {
        TeacherProfile profile = teacherProfileDAO.findById(profileId);
        if (profile == null) {
            throw new IllegalArgumentException("Teacher profile not found");
        }

        validateProfile(instruments, yearsExperience, hourlyRate);

        profile.setBiography(biography);
        profile.setInstrumentsTaught(instruments);
        profile.setYearsExperience(yearsExperience);
        profile.setHourlyRate(hourlyRate);
        profile.setLocation(location);

        boolean success = teacherProfileDAO.update(profile);
        if (!success) {
            throw new RuntimeException("Failed to update teacher profile");
        }

        return profile;
    }

    public TeacherProfile getProfileById(int profileId) {
        return teacherProfileDAO.findById(profileId);
    }

    public TeacherProfile getProfileByUserId(int userId) {
        return teacherProfileDAO.findByUserId(userId);
    }

    public List<TeacherProfile> getAllProfiles() {
        return teacherProfileDAO.findAll();
    }

    public List<TeacherProfile> searchByInstrument(String instrument) {
        if (instrument == null || instrument.trim().isEmpty()) {
            throw new IllegalArgumentException("Instrument cannot be empty");
        }
        return teacherProfileDAO.findByInstrument(instrument.trim());
    }

    public List<TeacherProfile> searchByLocation(String location) {
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("Location cannot be empty");
        }
        return teacherProfileDAO.findByLocation(location.trim());
    }

    public boolean deleteProfile(int profileId) {
        TeacherProfile profile = teacherProfileDAO.findById(profileId);
        if (profile == null) {
            throw new IllegalArgumentException("Teacher profile not found");
        }
        return teacherProfileDAO.delete(profileId);
    }

    private void validateProfile(String instruments, int yearsExperience, int hourlyRate) {
        if (instruments == null || instruments.trim().isEmpty()) {
            throw new IllegalArgumentException("Instruments cannot be empty");
        }
        if (yearsExperience < 0) {
            throw new IllegalArgumentException("Years of experience cannot be negative");
        }
        if (hourlyRate < 0) {
            throw new IllegalArgumentException("Hourly rate cannot be negative");
        }
    }
}
