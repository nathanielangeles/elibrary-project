package com.elibrary.models;

import java.sql.Timestamp;

/**
 * Admin model class representing an administrator in the system
 */
public class Admin {
    private int adminId;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private Timestamp createdAt;
    
    // Constructors
    public Admin() {}
    
    public Admin(int adminId, String firstName, String lastName, String username) {
        this.adminId = adminId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
    }
    
    // Getters and Setters
    public int getAdminId() {
        return adminId;
    }
    
    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Get full name of admin
     * @return Full name as "FirstName LastName"
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    @Override
    public String toString() {
        return "Admin{" +
                "adminId=" + adminId +
                ", username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
