package com.company.ui;

import com.company.model.Attendance;
import com.company.model.User;

import java.io.IOException;
import java.time.LocalDate;
import com.company.service.AttendanceService;
import com.company.service.UserService;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.bson.types.ObjectId;

public class ManagerDashboardController {
    @FXML private TextField empNameField;
    @FXML private TextField empEmailField;
    @FXML private PasswordField empPasswordField;
    @FXML private TableView<User> employeesTable;
    @FXML private TableColumn<User, String> empNameColumn;
    @FXML private TableColumn<User, String> empEmailColumn;
    @FXML private TableView<Attendance> attendanceTable;
    @FXML private TableColumn<Attendance, String> employeeNameColumn;
    @FXML private TableColumn<Attendance, LocalDate> dateColumn;
    @FXML private TableColumn<Attendance, String> statusColumn;
    @FXML private Label percentageLabel;
    @FXML private TableColumn<User, String> empPasswordColumn;
    @FXML private TableColumn<User, Void> empDeleteColumn;

    private final UserService userService = new UserService();
    private final AttendanceService attendanceService = new AttendanceService();
    private User currentManager;

    public void initializeWithUser(User manager) {
        employeesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        attendanceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        this.currentManager = manager;
        setupTableColumns();
        loadEmployees();
        loadAttendanceData();
    }

    private void setupTableColumns() {
        empNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        empEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        empPasswordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));

        employeeNameColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(
                        attendanceService.getUserName(cellData.getValue().getUserId())
                )
        );
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        statusColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().isPresent() ? "Present" : "Absent")
        );
        empDeleteColumn.setCellFactory(col -> new TableCell<>() {
            private final Button deleteBtn = new Button("Delete");

            {
                deleteBtn.getStyleClass().add("delete-button");
                deleteBtn.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    userService.deleteUser(user.getId());
                    loadEmployees(); // Refresh table
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteBtn);
                }
            }
        });
        empDeleteColumn.setCellValueFactory(param -> null);
    }

    private void loadEmployees() {
        employeesTable.setItems(FXCollections.observableArrayList(
                userService.getEmployeesByManager(currentManager.getId())
        ));
    }

    private void loadAttendanceData() {
        attendanceTable.setItems(FXCollections.observableArrayList(
                attendanceService.getTeamAttendance(currentManager.getId())
        ));

    }

    @FXML
    private void handleAddEmployee() {
        if (empNameField.getText().isEmpty() || empEmailField.getText().isEmpty() || empPasswordField.getText().isEmpty()) {
            showAlert("Input Error", "Please fill all fields");
            return;
        }

        User employee = new User();
        employee.setName(empNameField.getText());
        employee.setEmail(empEmailField.getText());
        // now pull password from the PasswordField
        employee.setPassword(empPasswordField.getText());
        employee.setRole("EMPLOYEE");
        employee.setManagerId(currentManager.getId());

        userService.createUser(employee);
        loadEmployees();

        empNameField.clear();
        empEmailField.clear();
        empPasswordField.clear();  // clear it too
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

            Stage currentStage = (Stage) empNameField.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Logout Error", "Failed to load login screen. Make sure Login.fxml is in /resources/ui/");
        }
    }
}