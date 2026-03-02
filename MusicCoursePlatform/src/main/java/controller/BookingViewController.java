package controller;

import dao.BookingDAO;
import dao.LearnerProfileDAO;
import dao.TeacherProfileDAO;
import dao.TimeSlotDAO;
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
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
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
import java.util.Locale;

public class BookingViewController {

    @FXML private Label teacherNameLabel;
    @FXML private Label teacherTitleLabel;
    @FXML private Label monthLabel;
    @FXML private FlowPane calendarGrid;
    @FXML private Label selectedDateLabel;
    @FXML private Label selectedTimeLabel;
    @FXML private Label timeSlotsDateLabel;
    @FXML private VBox timeSlotsContainer;
    @FXML private Button continueBtn;
    @FXML private ComboBox<String> languageCombo;
    
    @FXML private VBox calendarPanel;
    @FXML private VBox confirmPanel;
    @FXML private Label confirmDateLabel;
    @FXML private Label confirmTimeLabel;
    @FXML private TextArea notesField;
    
    @FXML private Circle step1Circle;
    @FXML private Circle step2Circle;
    @FXML private Circle step3Circle;
    @FXML private Circle step4Circle;

    private TeacherProfileDAO teacherProfileDAO;
    private TimeSlotDAO timeSlotDAO;
    private BookingDAO bookingDAO;
    private LearnerProfileDAO learnerProfileDAO;
    private UserDAO userDAO;
    
    private YearMonth currentMonth;
    private LocalDate selectedDate;
    private TimeSlot selectedSlot;
    private TeacherProfile selectedTeacher;
    private LearnerProfile learnerProfile;
    
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.ENGLISH);
    private DateTimeFormatter shortDateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);

    @FXML
    public void initialize() {
        teacherProfileDAO = new TeacherProfileDAO();
        timeSlotDAO = new TimeSlotDAO();
        bookingDAO = new BookingDAO();
        learnerProfileDAO = new LearnerProfileDAO();
        userDAO = new UserDAO();
        
        currentMonth = YearMonth.now();
        
        loadLearnerProfile();
        loadTeacher();
        setupLanguageCombo();
        updateCalendar();
        updateStepIndicators(1);
    }

    private void loadLearnerProfile() {
        var currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            learnerProfile = learnerProfileDAO.findByUserId(currentUser.getUserId());
        }
    }

    private void loadTeacher() {
        List<TeacherProfile> teachers = teacherProfileDAO.findAll();
        if (!teachers.isEmpty()) {
            selectedTeacher = teachers.get(0);
            updateTeacherDisplay();
        }
    }

    public void setTeacher(TeacherProfile teacher) {
        this.selectedTeacher = teacher;
        if (teacher != null) {
            updateTeacherDisplay();
            updateCalendar();
        }
    }

    private void updateTeacherDisplay() {
        if (selectedTeacher != null) {
            User user = userDAO.findById(selectedTeacher.getUserId());
            String name = (user != null) ? user.getUsername() : "Teacher " + selectedTeacher.getTeacherProfileId();
            teacherNameLabel.setText(name);
            if (teacherTitleLabel != null) {
                teacherTitleLabel.setText(selectedTeacher.getInstrumentsTaught() + " Instructor");
            }
        }
    }

    private void setupLanguageCombo() {
        if (languageCombo != null) {
            languageCombo.getItems().addAll("EN", "DE", "ZH");
            languageCombo.setValue("EN");
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
        List<TimeSlot> slots = timeSlotDAO.findByTeacherProfileIdAndDate(
            selectedTeacher.getTeacherProfileId(), date);
        return slots.stream().anyMatch(TimeSlot::isAvailable);
    }

    private void handleDateClick(LocalDate date) {
        selectedDate = date;
        selectedSlot = null;
        continueBtn.setDisable(true);
        
        selectedDateLabel.setText(date.format(shortDateFormatter));
        timeSlotsDateLabel.setText(date.format(dateFormatter));
        
        updateCalendar();
        updateTimeSlots();
        updateStepIndicators(2);
    }

    private void updateTimeSlots() {
        timeSlotsContainer.getChildren().clear();
        
        if (selectedDate == null || selectedTeacher == null) {
            return;
        }
        
        List<TimeSlot> slots = timeSlotDAO.findByTeacherProfileIdAndDate(
            selectedTeacher.getTeacherProfileId(), selectedDate);
        
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
        slotBtn.setPrefWidth(140);
        
        if (selectedSlot != null && selectedSlot.getSlotId() == slot.getSlotId()) {
            slotBtn.getStyleClass().add("time-slot-selected");
        }
        
        slotBtn.setOnAction(e -> handleSlotSelect(slot, slotBtn));
        
        HBox box = new HBox(slotBtn);
        box.setStyle("-fx-alignment: CENTER;");
        return box;
    }

    private void handleSlotSelect(TimeSlot slot, Button btn) {
        selectedSlot = slot;
        selectedTimeLabel.setText(slot.getStartTime() + " - " + slot.getEndTime());
        continueBtn.setDisable(false);
        
        updateTimeSlots();
        updateStepIndicators(3);
    }

    @FXML
    private void handleContinue(ActionEvent event) {
        if (selectedSlot == null || selectedDate == null) {
            return;
        }
        
        calendarPanel.setVisible(false);
        calendarPanel.setManaged(false);
        confirmPanel.setVisible(true);
        confirmPanel.setManaged(true);
        
        confirmDateLabel.setText(selectedDate.format(dateFormatter));
        confirmTimeLabel.setText(selectedSlot.getStartTime() + " - " + selectedSlot.getEndTime());
        
        updateStepIndicators(4);
    }

    @FXML
    private void handleBackToCalendar(ActionEvent event) {
        confirmPanel.setVisible(false);
        confirmPanel.setManaged(false);
        calendarPanel.setVisible(true);
        calendarPanel.setManaged(true);
        
        updateStepIndicators(3);
    }

    @FXML
    private void handleConfirmBooking(ActionEvent event) {
        if (selectedSlot == null || learnerProfile == null) {
            showError("Cannot create booking");
            return;
        }
        
        Booking booking = new Booking(learnerProfile.getLearnerProfileId(), selectedSlot.getSlotId());
        booking.setNotes(notesField.getText());
        
        boolean created = bookingDAO.create(booking);
        if (created) {
            timeSlotDAO.updateStatus(selectedSlot.getSlotId(), TimeSlot.STATUS_BOOKED);
            showSuccess("Booking confirmed successfully!");
            navigateBack(event);
        } else {
            showError("Failed to create booking");
        }
    }

    private void updateStepIndicators(int currentStep) {
        String activeColor = "-fx-fill: #2D4A47;";
        String inactiveColor = "-fx-fill: #CBD5E0;";
        
        step1Circle.setStyle(currentStep >= 1 ? activeColor : inactiveColor);
        step2Circle.setStyle(currentStep >= 2 ? activeColor : inactiveColor);
        step3Circle.setStyle(currentStep >= 3 ? activeColor : inactiveColor);
        step4Circle.setStyle(currentStep >= 4 ? activeColor : inactiveColor);
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

    private void navigateBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/student_dashboard.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackToDashboard(ActionEvent event) {
        navigateBack(event);
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        SessionManager.getInstance().logout();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
