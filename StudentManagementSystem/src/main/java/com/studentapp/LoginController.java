package com.studentapp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField     txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label         lblError;

    // Hard-coded credentials as requested
    private static final String VALID_USER = "admin";
    private static final String VALID_PASS = "admin123";

    @FXML
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Please enter username and password.");
            return;
        }

        if (VALID_USER.equals(username) && VALID_PASS.equals(password)) {
            openMainWindow();
        } else {
            lblError.setText("Invalid username or password.");
            txtPassword.clear();
        }
    }

    private void openMainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/studentapp/main.fxml"));
            Scene scene = new Scene(loader.load(), 900, 600);

            Stage stage = (Stage) txtUsername.getScene().getWindow();
            stage.setTitle("Student Management System");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            lblError.setText("Error loading main window.");
        }
    }

    @FXML
    private void handleClear() {
        txtUsername.clear();
        txtPassword.clear();
        lblError.setText("");
        txtUsername.requestFocus();
    }
}
