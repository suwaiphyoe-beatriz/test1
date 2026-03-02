package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;
import service.UserService;

import java.io.IOException;

public class SignupController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private UserService userService;

    @FXML
    public void initialize() {
        userService = new UserService();
    }

    @FXML
    private void handleStudentSignup(ActionEvent event) {
        registerUser("LEARNER", event);
    }

    @FXML
    private void handleTeacherSignup(ActionEvent event) {
        registerUser("TEACHER", event);
    }

    private void registerUser(String userType, ActionEvent event) {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields!");
            return;
        }

        if (!isValidEmail(email)) {
            showError("Please enter a valid email address!");
            return;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters!");
            return;
        }

        try {
            User user = userService.registerUser(username, password, email, userType);
            if (user != null) {
                SessionManager.getInstance().setCurrentUser(user);
                navigateToDashboard(event, user);
            }
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Registration failed: " + e.getMessage());
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private void navigateToDashboard(ActionEvent event, User user) {
        try {
            String fxmlPath;
            String title;
            
            if (user.isTeacher()) {
                fxmlPath = "/fxml/teacher_dashboard.fxml";
                title = "Music Course Platform - Teacher Dashboard";
            } else {
                fxmlPath = "/fxml/student_dashboard.fxml";
                title = "Music Course Platform - Student Dashboard";
            }
            
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(title);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Could not load dashboard!");
        }
    }

    @FXML
    private void handleBackToLogin(ActionEvent event) {
        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Scene loginScene = new Scene(loginRoot);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("Music Course Platform - Login");
        } catch (IOException e) {
            e.printStackTrace();
            showError("Could not load login screen!");
        }
    }

    private void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
        }
    }
}
