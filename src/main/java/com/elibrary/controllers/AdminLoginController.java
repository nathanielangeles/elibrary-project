package com.elibrary.controllers;

import com.elibrary.MainApp;
import com.elibrary.database.AdminDAO;
import com.elibrary.models.Admin;
import com.elibrary.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller for Admin Login screen
 */
public class AdminLoginController {
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    private AdminDAO adminDAO;
    
    public AdminLoginController() {
        this.adminDAO = new AdminDAO();
    }
    
    /**
     * Handle admin login
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        
        // Validation
        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "Please enter both username and password");
            return;
        }
        
        // Authenticate admin
        Admin admin = adminDAO.authenticateAdmin(username, password);
        
        if (admin != null) {
            // Login successful
            SessionManager.getInstance().loginAdmin(admin);
            
            // Navigate to admin dashboard
            MainApp.changeScene("/fxml/AdminDashboard.fxml", "E-Library - Admin Dashboard");
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password. Please try again.");
            passwordField.clear();
        }
    }
    
    /**
     * Handle back button
     */
    @FXML
    private void handleBack() {
        MainApp.changeScene("/fxml/LoginSelection.fxml", "E-Library - Login");
    }
    
    /**
     * Show alert dialog
     */
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
