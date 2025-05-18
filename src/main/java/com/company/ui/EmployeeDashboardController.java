package com.company.ui;

import com.company.model.Attendance;
import com.company.model.User;
import com.company.service.AttendanceService;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public class EmployeeDashboardController {
    @FXML private Label welcomeLabel;
    @FXML private TableView<Attendance> attendanceTable;
    @FXML private TableColumn<Attendance, LocalDate> dateColumn;
    @FXML private TableColumn<Attendance, String> statusColumn;
    @FXML private Label percentageLabel;

    private final AttendanceService attendanceService = new AttendanceService();
    private User currentEmployee;

    public void initializeWithUser(User employee) {
        attendanceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        this.currentEmployee = employee;
        welcomeLabel.setText("Welcome, " + employee.getName());
        setupTableColumns();
        loadAttendanceData();
    }

    private void setupTableColumns() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        statusColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().isPresent() ? "Present" : "Absent")
        );
    }

    private void loadAttendanceData() {
        attendanceTable.setItems(FXCollections.observableArrayList(
                attendanceService.getAttendanceForUser(currentEmployee.getId())
        ));
        percentageLabel.setText(String.format("Your Attendance: %.2f%%",
                attendanceService.calculateAttendancePercentage(currentEmployee.getId())));
    }

    @FXML
    private void handleMarkAttendance() {
        if (attendanceService.isAttendanceMarked(currentEmployee.getId(), LocalDate.now())) {
            showAlert("Already Marked", "Attendance already marked for today");
            return;
        }

        attendanceService.markAttendance(
                new Attendance(currentEmployee.getId(), LocalDate.now(), true)
        );
        loadAttendanceData();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
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

            Stage currentStage = (Stage) welcomeLabel.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Logout Error", "Failed to load login screen. Make sure Login.fxml is in /resources/ui/");
        }
    }

    @FXML
    private void handleMarkAbsent() {
        if (attendanceService.isAttendanceMarked(currentEmployee.getId(), LocalDate.now())) {
            showAlert("Already Marked", "Attendance already marked for today");
            return;
        }

        attendanceService.markAttendance(
                new Attendance(currentEmployee.getId(), LocalDate.now(), false)
        );
        loadAttendanceData();
    }
}