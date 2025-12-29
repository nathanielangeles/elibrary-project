package com.elibrary.utils;

import com.elibrary.models.Student;
import com.elibrary.models.Admin;

/**
 * Session manager to keep track of logged-in user
 * Uses singleton pattern
 */
public class SessionManager {
    private static SessionManager instance;
    
    private Student currentStudent;
    private Admin currentAdmin;
    private boolean isStudentLoggedIn;
    private boolean isAdminLoggedIn;
    
    private SessionManager() {
        this.isStudentLoggedIn = false;
        this.isAdminLoggedIn = false;
    }
    
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    /**
     * Login a student
     * @param student Student object
     */
    public void loginStudent(Student student) {
        this.currentStudent = student;
        this.isStudentLoggedIn = true;
        this.isAdminLoggedIn = false;
        this.currentAdmin = null;
    }
    
    /**
     * Login an admin
     * @param admin Admin object
     */
    public void loginAdmin(Admin admin) {
        this.currentAdmin = admin;
        this.isAdminLoggedIn = true;
        this.isStudentLoggedIn = false;
        this.currentStudent = null;
    }
    
    /**
     * Logout current user
     */
    public void logout() {
        this.currentStudent = null;
        this.currentAdmin = null;
        this.isStudentLoggedIn = false;
        this.isAdminLoggedIn = false;
    }
    
    /**
     * Get current logged-in student
     * @return Student object or null
     */
    public Student getCurrentStudent() {
        return currentStudent;
    }
    
    /**
     * Get current logged-in admin
     * @return Admin object or null
     */
    public Admin getCurrentAdmin() {
        return currentAdmin;
    }
    
    /**
     * Check if a student is logged in
     * @return true if student is logged in
     */
    public boolean isStudentLoggedIn() {
        return isStudentLoggedIn;
    }
    
    /**
     * Check if an admin is logged in
     * @return true if admin is logged in
     */
    public boolean isAdminLoggedIn() {
        return isAdminLoggedIn;
    }
}
