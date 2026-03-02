package controller;

import dao.TeacherProfileDAO;
import dao.TimeSlotDAO;
import dao.BookingDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.TeacherProfile;
import model.TimeSlot;
import model.User;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TeacherDashboardController {

    @FXML private Label nameLabel;
    @FXML private ComboBox<String> instrumentsCombo;
    @FXML private TextField pricingField;
    @FXML private TextArea bioField;
    @FXML private Label monthLabel;
    @FXML private FlowPane calendarGrid;
    @FXML private Label selectedDateLabel;
    @FXML private ComboBox<String> startTimeCombo;
    @FXML private ComboBox<String> endTimeCombo;
    @FXML private VBox timeSlotsContainer;
    @FXML private ComboBox<String> languageCombo;

    private TeacherProfileDAO teacherProfileDAO;
    private TimeSlotDAO timeSlotDAO;
    private BookingDAO bookingDAO;
    
    private YearMonth currentMonth;
    private LocalDate selectedDate;
    private TeacherProfile teacherProfile;
    private User currentUser;

    @FXML
    public void initialize() {
        teacherProfileDAO = new TeacherProfileDAO();
        timeSlotDAO = new TimeSlotDAO();
        bookingDAO = new BookingDAO();
        
        currentMonth = YearMonth.now();
        currentUser = SessionManager.getInstance().getCurrentUser();
        
        setupInstrumentsCombo();
        setupTimeComboBoxes();
        setupLanguageCombo();
        loadTeacherProfile();
        updateCalendar();
    }

    private void setupInstrumentsCombo() {
        instrumentsCombo.getItems().addAll(
            "Piano", "Guitar", "Violin", "Drums", "Flute", "Saxophone", "Cello", "Voice"
        );
    }

    private void setupTimeComboBoxes() {
        for (int hour = 7; hour <= 21; hour++) {
            for (int min = 0; min < 60; min += 30) {
                String time = String.format("%d:%02d", hour, min);
                startTimeCombo.getItems().add(time);
                endTimeCombo.getItems().add(time);
            }
        }
    }

    private void setupLanguageCombo() {
        if (languageCombo != null) {
            languageCombo.getItems().addAll("EN", "DE", "ZH");
            languageCombo.setValue("EN");
        }
    }

    private void loadTeacherProfile() {
        if (currentUser == null) return;
        
        teacherProfile = teacherProfileDAO.findByUserId(currentUser.getUserId());
        
        if (teacherProfile != null) {
            nameLabel.setText(currentUser.getUsername());
            if (teacherProfile.getInstrumentsTaught() != null) {
                instrumentsCombo.setValue(teacherProfile.getInstrumentsTaught().split(",")[0].trim());
            }
            pricingField.setText(String.valueOf(teacherProfile.getHourlyRate()));
            if (teacherProfile.getBiography() != null) {
                bioField.setText(teacherProfile.getBiography());
            }
        } else {
            nameLabel.setText(currentUser != null ? currentUser.getUsername() : "Name");
            teacherProfile = new TeacherProfile(currentUser.getUserId(), "Piano");
            teacherProfileDAO.create(teacherProfile);
        }
    }

    @FXML
    private void handleSaveProfile(ActionEvent event) {
        if (currentUser == null || teacherProfile == null) return;
        
        String instrument = instrumentsCombo.getValue();
        String pricing = pricingField.getText();
        String bio = bioField.getText();
        
        teacherProfile.setInstrumentsTaught(instrument);
        if (pricing != null && !pricing.isEmpty()) {
            try {
                teacherProfile.setHourlyRate(Integer.parseInt(pricing));
            } catch (NumberFormatException e) {
                showError("Invalid pricing format");
                return;
            }
        }
        teacherProfile.setBiography(bio);
        
        boolean updated = teacherProfileDAO.update(teacherProfile);
        if (updated) {
            showSuccess("Profile saved successfully!");
        } else {
            showError("Failed to save profile");
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
            
            if (hasTimeSlots(date)) {
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

    private boolean hasTimeSlots(LocalDate date) {
        if (teacherProfile == null) return false;
        List<TimeSlot> slots = timeSlotDAO.findByTeacherProfileIdAndDate(teacherProfile.getTeacherProfileId(), date);
        return !slots.isEmpty();
    }

    private void handleDateClick(LocalDate date) {
        selectedDate = date;
        selectedDateLabel.setText(date.format(DateTimeFormatter.ofPattern("EEEE, d. MMMM")));
        updateCalendar();
        updateTimeSlots();
    }

    private void updateTimeSlots() {
        timeSlotsContainer.getChildren().clear();
        
        if (selectedDate == null || teacherProfile == null) return;
        
        List<TimeSlot> slots = timeSlotDAO.findByTeacherProfileIdAndDate(teacherProfile.getTeacherProfileId(), selectedDate);
        
        for (TimeSlot slot : slots) {
            HBox slotBox = createTimeSlotBox(slot);
            timeSlotsContainer.getChildren().add(slotBox);
        }
        
        if (timeSlotsContainer.getChildren().isEmpty()) {
            Label noSlotsLabel = new Label("No time slots set");
            noSlotsLabel.setStyle("-fx-text-fill: #718096;");
            timeSlotsContainer.getChildren().add(noSlotsLabel);
        }
    }

    private HBox createTimeSlotBox(TimeSlot slot) {
        String timeText = slot.getStartTime() + " - " + slot.getEndTime();
        
        Label timeLabel = new Label(timeText);
        timeLabel.getStyleClass().add("time-slot");
        timeLabel.setPrefWidth(120);
        
        Button deleteBtn = new Button("🗑");
        deleteBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        deleteBtn.setOnAction(e -> handleDeleteSlot(slot));
        
        HBox box = new HBox(10, timeLabel, deleteBtn);
        box.setStyle("-fx-alignment: CENTER_LEFT;");
        
        if (slot.isBooked()) {
            timeLabel.getStyleClass().add("time-slot-booked");
        }
        
        return box;
    }

    @FXML
    private void handleAddTimeSlot(ActionEvent event) {
        if (selectedDate == null) {
            showError("Please select a date first!");
            return;
        }
        
        if (teacherProfile == null) {
            showError("Teacher profile not found!");
            return;
        }
        
        String startStr = startTimeCombo.getValue();
        String endStr = endTimeCombo.getValue();
        
        if (startStr == null || endStr == null) {
            showError("Please select start and end time!");
            return;
        }
        
        TimeSlot slot = new TimeSlot(teacherProfile.getTeacherProfileId(), selectedDate, startStr, endStr);
        
        boolean created = timeSlotDAO.create(slot);
        if (created) {
            updateTimeSlots();
            updateCalendar();
            showSuccess("Time slot added!");
        } else {
            showError("Failed to add time slot");
        }
    }

    private void handleDeleteSlot(TimeSlot slot) {
        if (slot.isBooked()) {
            showError("Cannot delete a booked slot!");
            return;
        }
        
        boolean deleted = timeSlotDAO.delete(slot.getSlotId());
        if (deleted) {
            updateTimeSlots();
            updateCalendar();
        }
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
    private void handlePrevPage(ActionEvent event) {
        // Handle pagination
    }

    @FXML
    private void handleNextPage(ActionEvent event) {
        // Handle pagination
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

    private void showSuccess(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.INFORMATION
        );
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
