package controller;

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
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.TeacherProfile;
import model.TimeSlot;
import model.User;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TeacherProfileViewController {

    @FXML private Label teacherNameLabel;
    @FXML private Label titleLabel;
    @FXML private Label instrumentsLabel;
    @FXML private Label experienceLabel;
    @FXML private Label locationLabel;
    @FXML private Label rateLabel;
    @FXML private Label biographyLabel;
    @FXML private Label weekLabel;
    
    @FXML private VBox day1Box;
    @FXML private VBox day2Box;
    @FXML private VBox day3Box;
    @FXML private VBox day4Box;
    @FXML private VBox day5Box;
    @FXML private VBox day6Box;
    @FXML private VBox day7Box;
    
    private TeacherProfileDAO teacherProfileDAO;
    private TimeSlotDAO timeSlotDAO;
    private UserDAO userDAO;
    
    private TeacherProfile teacherProfile;
    private LocalDate weekStart;
    
    private DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEE");
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d");

    @FXML
    public void initialize() {
        teacherProfileDAO = new TeacherProfileDAO();
        timeSlotDAO = new TimeSlotDAO();
        userDAO = new UserDAO();
        
        weekStart = LocalDate.now().with(DayOfWeek.MONDAY);
    }

    public void setTeacherProfile(TeacherProfile profile) {
        this.teacherProfile = profile;
        loadTeacherDetails();
        updateWeekView();
    }

    public void setTeacherProfileId(int profileId) {
        this.teacherProfile = teacherProfileDAO.findById(profileId);
        if (teacherProfile != null) {
            loadTeacherDetails();
            updateWeekView();
        }
    }

    private void loadTeacherDetails() {
        if (teacherProfile == null) return;
        
        User user = userDAO.findById(teacherProfile.getUserId());
        if (user != null) {
            teacherNameLabel.setText(user.getUsername());
        } else {
            teacherNameLabel.setText("Teacher " + teacherProfile.getTeacherProfileId());
        }
        
        titleLabel.setText("Music Instructor");
        instrumentsLabel.setText(teacherProfile.getInstrumentsTaught());
        experienceLabel.setText(teacherProfile.getYearsExperience() + " years");
        locationLabel.setText(teacherProfile.getLocation() != null ? 
            teacherProfile.getLocation() : "Not specified");
        rateLabel.setText("$" + teacherProfile.getHourlyRate() + "/hr");
        biographyLabel.setText(teacherProfile.getBiography() != null ? 
            teacherProfile.getBiography() : "No biography available.");
    }

    private void updateWeekView() {
        weekLabel.setText(weekStart.format(DateTimeFormatter.ofPattern("MMM d")) + 
            " - " + weekStart.plusDays(6).format(DateTimeFormatter.ofPattern("MMM d, yyyy")));
        
        VBox[] dayBoxes = {day1Box, day2Box, day3Box, day4Box, day5Box, day6Box, day7Box};
        
        for (int i = 0; i < 7; i++) {
            LocalDate date = weekStart.plusDays(i);
            VBox dayBox = dayBoxes[i];
            dayBox.getChildren().clear();
            dayBox.setPrefWidth(120);
            
            Label dayLabel = new Label(date.format(dayFormatter));
            dayLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2D4A47;");
            
            Label dateLabel = new Label(date.format(dateFormatter));
            dateLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
            
            dayBox.getChildren().addAll(dayLabel, dateLabel);
            
            if (teacherProfile != null) {
                List<TimeSlot> slots = timeSlotDAO.findByTeacherProfileIdAndDate(
                    teacherProfile.getTeacherProfileId(), date);
                
                for (TimeSlot slot : slots) {
                    if (slot.isAvailable()) {
                        Button slotBtn = new Button(slot.getStartTime());
                        slotBtn.getStyleClass().add("time-slot-mini");
                        slotBtn.setPrefWidth(80);
                        slotBtn.setOnAction(e -> handleSlotClick(slot));
                        dayBox.getChildren().add(slotBtn);
                    }
                }
                
                if (slots.stream().noneMatch(TimeSlot::isAvailable)) {
                    Label noSlots = new Label("-");
                    noSlots.setStyle("-fx-text-fill: #CBD5E0;");
                    dayBox.getChildren().add(noSlots);
                }
            }
        }
    }

    private void handleSlotClick(TimeSlot slot) {
        navigateToBooking(slot);
    }

    @FXML
    private void handleBookNow(ActionEvent event) {
        navigateToBookingView(event);
    }

    private void navigateToBooking(TimeSlot slot) {
        // Navigate to booking view with pre-selected slot
    }

    private void navigateToBookingView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/booking_view.fxml"));
            Parent root = loader.load();
            
            BookingViewController controller = loader.getController();
            controller.setTeacher(teacherProfile);
            
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePrevWeek(ActionEvent event) {
        weekStart = weekStart.minusWeeks(1);
        updateWeekView();
    }

    @FXML
    private void handleNextWeek(ActionEvent event) {
        weekStart = weekStart.plusWeeks(1);
        updateWeekView();
    }

    @FXML
    private void handleBack(ActionEvent event) {
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
}
