package com.elibrary.controllers;

import com.elibrary.MainApp;
import javafx.fxml.FXML;

/**
 * Controller for Login Selection screen
 */
public class LoginSelectionController {
    
    /**
     * Handle student login button
     */
    @FXML
    private void handleStudentLogin() {
        MainApp.changeScene("/fxml/StudentLogin.fxml", "E-Library - Student Login");
    }
    
    /**
     * Handle admin login button
     */
    @FXML
    private void handleAdminLogin() {
        MainApp.changeScene("/fxml/AdminLogin.fxml", "E-Library - Admin Login");
    }
}
