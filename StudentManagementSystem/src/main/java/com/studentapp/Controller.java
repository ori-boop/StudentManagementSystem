package com.studentapp;

import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.*;

public class Controller {

    @FXML private TextField          txtName;
    @FXML private TextField          txtCourse;
    @FXML private ChoiceBox<YearLevel> cbYear;

    @FXML private TableView<Student>           table;
    @FXML private TableColumn<Student, Integer> colId;
    @FXML private TableColumn<Student, String>  colName;
    @FXML private TableColumn<Student, String>  colCourse;
    @FXML private TableColumn<Student, String>  colYear;

    @FXML private Label lblStatus;

    private final ObservableList<Student> list = FXCollections.observableArrayList();
    private Connection conn;
    private int selectedId = -1;

    @FXML
    public void initialize() {
        conn = DBConnection.connect();

        if (conn == null) {
            setStatus("⚠  Database connection failed. Check DBConnection.java.", true);
        }

        cbYear.getItems().setAll(YearLevel.values());

        colId.setCellValueFactory(data -> data.getValue().idProperty().asObject());
        colName.setCellValueFactory(data -> data.getValue().nameProperty());
        colCourse.setCellValueFactory(data -> data.getValue().courseProperty());
        colYear.setCellValueFactory(data -> data.getValue().yearLevelProperty());

        loadData();

        table.setOnMouseClicked(e -> {
            Student s = table.getSelectionModel().getSelectedItem();
            if (s != null) {
                selectedId = s.getId();
                txtName.setText(s.getName());
                txtCourse.setText(s.getCourse());

                for (YearLevel y : YearLevel.values()) {
                    if (y.toString().equals(s.getYearLevel())) {
                        cbYear.setValue(y);
                        break;
                    }
                }
            }
        });
    }

    private void loadData() {
        list.clear();
        if (conn == null) return;
        try {
            String    query = "SELECT * FROM students ORDER BY id";
            ResultSet rs    = conn.createStatement().executeQuery(query);
            while (rs.next()) {
                list.add(new Student(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("course"),
                        YearLevel.values()[rs.getInt("year_level") - 1].toString()
                ));
            }
            table.setItems(list);
        } catch (Exception e) {
            e.printStackTrace();
            setStatus("Error loading data: " + e.getMessage(), true);
        }
    }

    private boolean validate() {
        if (txtName.getText().trim().isEmpty()) {
            setStatus("Name cannot be empty.", true);
            txtName.requestFocus();
            return false;
        }
        if (txtCourse.getText().trim().isEmpty()) {
            setStatus("Course cannot be empty.", true);
            txtCourse.requestFocus();
            return false;
        }
        if (cbYear.getValue() == null) {
            setStatus("Please select a year level.", true);
            return false;
        }
        return true;
    }

    @FXML
    private void addStudent() {
        if (!validate()) return;
        if (conn == null) { setStatus("No database connection.", true); return; }

        try {
            String           query = "INSERT INTO students(name, course, year_level) VALUES (?, ?, ?)";
            PreparedStatement pst  = conn.prepareStatement(query);
            pst.setString(1, txtName.getText().trim());
            pst.setString(2, txtCourse.getText().trim());
            pst.setInt(3, cbYear.getValue().ordinal() + 1);
            pst.executeUpdate();

            loadData();
            clearFields();
            setStatus("✔  Student added successfully.", false);
        } catch (Exception e) {
            e.printStackTrace();
            setStatus("Error adding student: " + e.getMessage(), true);
        }
    }

    @FXML
    private void updateStudent() {
        if (selectedId == -1) {
            setStatus("Please select a student to update.", true);
            return;
        }
        if (!validate()) return;
        if (conn == null) { setStatus("No database connection.", true); return; }

        try {
            String            query = "UPDATE students SET name=?, course=?, year_level=? WHERE id=?";
            PreparedStatement pst   = conn.prepareStatement(query);
            pst.setString(1, txtName.getText().trim());
            pst.setString(2, txtCourse.getText().trim());
            pst.setInt(3, cbYear.getValue().ordinal() + 1);
            pst.setInt(4, selectedId);
            pst.executeUpdate();

            loadData();
            clearFields();
            setStatus("✔  Student updated successfully.", false);
        } catch (Exception e) {
            e.printStackTrace();
            setStatus("Error updating student: " + e.getMessage(), true);
        }
    }

    @FXML
    private void deleteStudent() {
        if (selectedId == -1) {
            setStatus("Please select a student to delete.", true);
            return;
        }
        if (conn == null) { setStatus("No database connection.", true); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete this student?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try {
                    String            query = "DELETE FROM students WHERE id=?";
                    PreparedStatement pst   = conn.prepareStatement(query);
                    pst.setInt(1, selectedId);
                    pst.executeUpdate();

                    loadData();
                    clearFields();
                    setStatus("✔  Student deleted successfully.", false);
                } catch (Exception e) {
                    e.printStackTrace();
                    setStatus("Error deleting student: " + e.getMessage(), true);
                }
            }
        });
    }

    @FXML
    private void clearFields() {
        txtName.clear();
        txtCourse.clear();
        cbYear.setValue(null);
        selectedId = -1;
        table.getSelectionModel().clearSelection();
        setStatus("", false);
    }

    @FXML
    private void handleLogout() {
        try {
            if (conn != null) conn.close();

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/studentapp/login.fxml"));
            Scene scene = new Scene(loader.load(), 480, 380);

            Stage stage = (Stage) table.getScene().getWindow();
            stage.setTitle("Student Management System – Login");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setStatus(String msg, boolean isError) {
        if (lblStatus == null) return;
        lblStatus.setText(msg);
        lblStatus.setStyle(isError
                ? "-fx-text-fill: #e74c3c; -fx-font-weight: bold;"
                : "-fx-text-fill: #27ae60; -fx-font-weight: bold;");
    }
}
