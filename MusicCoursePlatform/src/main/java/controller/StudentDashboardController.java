package controller;

import dao.TeacherProfileDAO;
import dao.TimeSlotDAO;
import dao.BookingDAO;
import dao.LearnerProfileDAO;
import dao.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Booking;
import model.LearnerProfile;
import model.TeacherProfile;
import model.TimeSlot;
import model.User;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StudentDashboardController {

    @FXML private Label teacherNameLabel;
    @FXML private Label teacherInstrumentLabel;
    @FXML private Label teacherExperienceLabel;
    @FXML private Label teacherRateLabel;
    @FXML private ComboBox<String> instrumentCombo;
    @FXML private ComboBox<String> teacherCombo;
    @FXML private Label monthLabel;
    @FXML private FlowPane calendarGrid;
    @FXML private Label selectedDateLabel;
    @FXML private Label selectedTimeLabel;
    @FXML private VBox timeSlotsContainer;
    @FXML private ComboBox<String> languageCombo;
    @FXML private Button bookButton;

    private TeacherProfileDAO teacherProfileDAO;
    private TimeSlotDAO timeSlotDAO;
    private BookingDAO bookingDAO;
    private LearnerProfileDAO learnerProfileDAO;
    private UserDAO userDAO;
    
    private YearMonth currentMonth;
    private LocalDate selectedDate;
    private TeacherProfile selectedTeacher;
    private LearnerProfile learnerProfile;
    private List<TeacherProfile> teacherProfiles;
    private TimeSlot selectedSlot;

    @FXML
    public void initialize() {
        teacherProfileDAO = new TeacherProfileDAO();
        timeSlotDAO = new TimeSlotDAO();
        bookingDAO = new BookingDAO();
        learnerProfileDAO = new LearnerProfileDAO();
        userDAO = new UserDAO();
        
        currentMonth = YearMonth.now();
        
        loadLearnerProfile();
        setupInstrumentCombo();
        setupLanguageCombo();
        updateCalendar();
        loadTeachers();
    }

    private void loadLearnerProfile() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            learnerProfile = learnerProfileDAO.findByUserId(currentUser.getUserId());
            if (learnerProfile == null) {
                learnerProfile = new LearnerProfile(currentUser.getUserId(), "Piano");
                learnerProfileDAO.create(learnerProfile);
            }
        }
    }

    private void setupInstrumentCombo() {
        instrumentCombo.getItems().addAll(
            "Piano", "Guitar", "Violin", "Drums", "Flute", "Saxophone", "Cello", "Voice"
        );
        instrumentCombo.setValue("Piano");
    }

    private void setupLanguageCombo() {
        if (languageCombo != null) {
            languageCombo.getItems().addAll("EN", "DE", "ZH");
            languageCombo.setValue("EN");
        }
    }

    private void loadTeachers() {
        String instrument = instrumentCombo.getValue();
        if (instrument != null) {
            teacherProfiles = teacherProfileDAO.findByInstrument(instrument);
            teacherCombo.getItems().clear();
            for (TeacherProfile profile : teacherProfiles) {
                User user = userDAO.findById(profile.getUserId());
                String name = (user != null) ? user.getUsername() : "Teacher " + profile.getTeacherProfileId();
                teacherCombo.getItems().add(name);
            }
            if (!teacherProfiles.isEmpty()) {
                teacherCombo.setValue(teacherCombo.getItems().get(0));
                selectedTeacher = teacherProfiles.get(0);
                updateTeacherDisplay();
                updateCalendar();
            } else {
                teacherNameLabel.setText("No teachers available");
                if (teacherInstrumentLabel != null) teacherInstrumentLabel.setText("");
                if (teacherExperienceLabel != null) teacherExperienceLabel.setText("");
                if (teacherRateLabel != null) teacherRateLabel.setText("");
            }
        }
    }

    @FXML
    private void handleInstrumentChange(ActionEvent event) {
        loadTeachers();
        updateTimeSlots();
    }

    @FXML
    private void handleTeacherChange(ActionEvent event) {
        int index = teacherCombo.getSelectionModel().getSelectedIndex();
        if (index >= 0 && index < teacherProfiles.size()) {
            selectedTeacher = teacherProfiles.get(index);
            updateTeacherDisplay();
            updateTimeSlots();
        }
    }

    private void updateTeacherDisplay() {
        if (selectedTeacher != null) {
            User user = userDAO.findById(selectedTeacher.getUserId());
            String name = (user != null) ? user.getUsername() : "Teacher " + selectedTeacher.getTeacherProfileId();
            teacherNameLabel.setText(name);
            
            if (teacherInstrumentLabel != null) {
                teacherInstrumentLabel.setText(selectedTeacher.getInstrumentsTaught());
            }
            if (teacherExperienceLabel != null) {
                teacherExperienceLabel.setText(selectedTeacher.getYearsExperience() + " years exp.");
            }
            if (teacherRateLabel != null) {
                teacherRateLabel.setText("$" + selectedTeacher.getHourlyRate() + "/hr");
            }
        }
    }

    private void updateCalendar() {
        monthLabel.setText(currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        calendarGrid.getChildren().clear();
        
        LocalDate firstOfMonth = currentMonth.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7;
        
        for (int i = 0; i < dayOfWeek; i++) {
            Label emptyLabel = new Label("");
            emptyLabel.setPrefWidth(40);
            emptyLabel.setPrefHeight(40);
            calendarGrid.getChildren().add(emptyLabel);
        }
        
        for (int day = 1; day <= currentMonth.lengthOfMonth(); day++) {
            LocalDate date = currentMonth.atDay(day);
            Button dayBtn = new Button(String.valueOf(day));
            dayBtn.setPrefWidth(40);
            dayBtn.setPrefHeight(40);
            dayBtn.getStyleClass().add("calendar-day");
            
            if (hasAvailableSlots(date)) {
                dayBtn.getStyleClass().add("calendar-day-available");
            }
            
            if (date.equals(selectedDate)) {
                dayBtn.getStyleClass().add("calendar-day-selected");
            }
            
            final LocalDate clickedDate = date;
            dayBtn.setOnAction(e -> handleDateClick(clickedDate));
            
            calendarGrid.getChildren().add(dayBtn);
        }
    }

    private boolean hasAvailableSlots(LocalDate date) {
        if (selectedTeacher == null) return false;
        List<TimeSlot> slots = timeSlotDAO.findByTeacherProfileIdAndDate(selectedTeacher.getTeacherProfileId(), date);
        return slots.stream().anyMatch(TimeSlot::isAvailable);
    }

    private void handleDateClick(LocalDate date) {
        selectedDate = date;
        selectedDateLabel.setText(date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d")));
        updateCalendar();
        updateTimeSlots();
    }

    private void updateTimeSlots() {
        timeSlotsContainer.getChildren().clear();
        
        if (selectedDate == null || selectedTeacher == null) {
            return;
        }
        
        List<TimeSlot> slots = timeSlotDAO.findByTeacherProfileIdAndDate(selectedTeacher.getTeacherProfileId(), selectedDate);
        
        for (TimeSlot slot : slots) {
            if (slot.isAvailable()) {
                HBox slotBox = createTimeSlotBox(slot);
                timeSlotsContainer.getChildren().add(slotBox);
            }
        }
        
        if (timeSlotsContainer.getChildren().isEmpty()) {
            Label noSlotsLabel = new Label("No available slots");
            noSlotsLabel.setStyle("-fx-text-fill: #718096;");
            timeSlotsContainer.getChildren().add(noSlotsLabel);
        }
    }

    private HBox createTimeSlotBox(TimeSlot slot) {
        String timeText = slot.getStartTime() + " - " + slot.getEndTime();
        
        Button slotBtn = new Button(timeText);
        slotBtn.getStyleClass().add("time-slot");
        slotBtn.setPrefWidth(160);
        
        if (selectedSlot != null && selectedSlot.getSlotId() == slot.getSlotId()) {
            slotBtn.getStyleClass().add("time-slot-selected");
        }
        
        slotBtn.setOnAction(e -> handleSlotSelect(slot));
        
        HBox box = new HBox(slotBtn);
        box.setStyle("-fx-alignment: CENTER;");
        return box;
    }

    private void handleSlotSelect(TimeSlot slot) {
        selectedSlot = slot;
        if (selectedTimeLabel != null) {
            selectedTimeLabel.setText(slot.getStartTime() + " - " + slot.getEndTime());
        }
        if (bookButton != null) {
            bookButton.setDisable(false);
        }
        updateTimeSlots();
    }

    private void confirmBooking() {
        if (learnerProfile == null) {
            showError("Learner profile not found!");
            return;
        }
        if (selectedSlot == null) {
            showError("Please select a time slot first!");
            return;
        }
        
        Booking booking = new Booking(learnerProfile.getLearnerProfileId(), selectedSlot.getSlotId());
        
        boolean created = bookingDAO.create(booking);
        if (created) {
            timeSlotDAO.updateStatus(selectedSlot.getSlotId(), TimeSlot.STATUS_BOOKED);
            selectedSlot = null;
            if (selectedTimeLabel != null) {
                selectedTimeLabel.setText("Select a time");
            }
            if (bookButton != null) {
                bookButton.setDisable(true);
            }
            updateTimeSlots();
            showSuccessMessage("Booking created successfully!");
        } else {
            showError("Failed to create booking");
        }
    }

    private void showSuccessMessage(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.INFORMATION
        );
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.ERROR
        );
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handlePrevMonth(ActionEvent event) {
        currentMonth = currentMonth.minusMonths(1);
        updateCalendar();
    }

    @FXML
    private void handleNextMonth(ActionEvent event) {
        currentMonth = currentMonth.plusMonths(1);
        updateCalendar();
    }

    @FXML
    private void handleViewProfile(ActionEvent event) {
        if (selectedTeacher == null) {
            showError("Please select a teacher first");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/teacher_profile_view.fxml"));
            Parent root = loader.load();
            
            TeacherProfileViewController controller = loader.getController();
            controller.setTeacherProfile(selectedTeacher);
            
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBookNow(ActionEvent event) {
        if (selectedTeacher == null) {
            showError("Please select a teacher first");
            return;
        }
        if (selectedSlot == null) {
            showError("Please select a time slot first");
            return;
        }
        confirmBooking();
    }

    @FXML
    private void handlePrevPage(ActionEvent event) {
        // Handle pagination
    }

    @FXML
    private void handleNextPage(ActionEvent event) {
        // Handle pagination
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        SessionManager.getInstance().logout();
        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Scene loginScene = new Scene(loginRoot);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("Music Course Platform - Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
