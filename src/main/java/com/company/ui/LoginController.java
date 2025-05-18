package com.company.ui;

import com.company.model.User;
import com.company.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    private final UserService userService = new UserService();

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Input Error", "Please fill in all fields");
            return;
        }

        try {
            User user = userService.authenticate(email, password);
            if (user != null) {
                navigateToDashboard(user);
            } else {
                showAlert("Login Failed", "Invalid credentials");
            }
        } catch (Exception e) {
            showAlert("Error", "Database connection failed");
        }
    }

    private void navigateToDashboard(User user) {
        try {
            FXMLLoader loader = new FXMLLoader();
            String fxmlPath = switch (user.getRole()) {
                case "ADMIN" -> "/fxml/AdminDashboard.fxml";
                case "MANAGER" -> "/fxml/ManagerDashboard.fxml";
                default -> "/fxml/EmployeeDashboard.fxml";
            };

            loader.setLocation(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Pass user data to dashboard
            switch (user.getRole()) {
                case "ADMIN":
                    ((AdminDashboardController) loader.getController()).initialize();
                    break;
                case "MANAGER":
                    ((ManagerDashboardController) loader.getController()).initializeWithUser(user);
                    break;
                default:
                    ((EmployeeDashboardController) loader.getController()).initializeWithUser(user);
            }

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (Exception e) {
            System.out.println(e);
            showAlert("Error", "Failed to load dashboard");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}