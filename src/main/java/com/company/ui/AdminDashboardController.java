package com.company.ui;

import com.company.model.User;
import com.company.service.UserService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminDashboardController {
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TableView<User> managersTable;
    @FXML private TableColumn<User, String> nameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, Void> actionColumn;

    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        managersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        setupTableColumns();
        loadManagers();
    }

    private void setupTableColumns() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    userService.deleteUser(user.getId());
                    loadManagers();
                });

                deleteButton.getStyleClass().add("delete-button");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });
    }

    private void loadManagers() {
        managersTable.setItems(FXCollections.observableArrayList(
                userService.getAllManagers()
        ));
    }

    @FXML
    private void handleAddManager() {
        if (nameField.getText().isEmpty() ||
                emailField.getText().isEmpty() ||
                passwordField.getText().isEmpty()) {
            showAlert("Input Error", "Please fill all fields");
            return;
        }

        User manager = new User();
        manager.setName(nameField.getText());
        manager.setEmail(emailField.getText());
        manager.setPassword(passwordField.getText());
        manager.setRole("MANAGER");

        userService.createUser(manager);
        loadManagers();
        nameField.clear();
        emailField.clear();
        passwordField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/LoginView.fxml"));
            Parent loginRoot = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(loginRoot));
            stage.setTitle("Employee Attendance Tracker");
            stage.show();

            Stage currentStage = (Stage) nameField.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Logout Error", "Failed to load login screen. Make sure Login.fxml is in /resources/ui/");
        }
    }
}