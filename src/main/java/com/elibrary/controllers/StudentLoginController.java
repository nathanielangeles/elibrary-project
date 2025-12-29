package com.elibrary.controllers;

import com.elibrary.MainApp;
import com.elibrary.database.StudentDAO;
import com.elibrary.models.Student;
import com.elibrary.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller for Student Login screen
 */
public class StudentLoginController {
    
    @FXML
    private TextField lrnField;
    
    private StudentDAO studentDAO;
    
    public StudentLoginController() {
        this.studentDAO = new StudentDAO();
    }
    
    /**
     * Handle student login
     */
    @FXML
    private void handleLogin() {
        String lrn = lrnField.getText().trim();
        
        // Validation
        if (lrn.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "Please enter your LRN");
            return;
        }
        
        // Authenticate student
        Student student = studentDAO.authenticateStudent(lrn);
        
        if (student != null) {
            // Login successful
            SessionManager.getInstance().loginStudent(student);
            
            // Navigate to student dashboard
            MainApp.changeScene("/fxml/StudentDashboard.fxml", "E-Library - Student Dashboard");
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid LRN. Please try again.");
            lrnField.clear();
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
