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

public class LoginController {

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
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields!");
            return;
        }

        try {
            User user = userService.authenticateByEmail(email, password);
            if (user != null) {
                SessionManager.getInstance().setCurrentUser(user);
                navigateToDashboard(event, user);
            } else {
                showError("Invalid email or password!");
            }
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Login failed: " + e.getMessage());
        }
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
    private void handleSignup(ActionEvent event) {
        try {
            Parent signupRoot = FXMLLoader.load(getClass().getResource("/fxml/signup.fxml"));
            Scene signupScene = new Scene(signupRoot);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(signupScene);
            stage.setTitle("Music Course Platform - Sign Up");
        } catch (IOException e) {
            e.printStackTrace();
            showError("Could not load signup screen!");
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        emailField.clear();
        passwordField.clear();
        hideError();
    }

    private void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
        }
    }

    private void hideError() {
        if (errorLabel != null) {
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);
        }
    }
}
