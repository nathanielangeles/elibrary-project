package com.elibrary.models;

import java.sql.Timestamp;

/**
 * Student model class representing a student in the e-library system
 */
public class Student {
    private int studentId;
    private String lrn;
    private String firstName;
    private String lastName;
    private int gradeLevel;
    private String section;
    private Timestamp createdAt;
    
    // Constructors
    public Student() {}
    
    public Student(int studentId, String lrn, String firstName, String lastName, 
                   int gradeLevel, String section) {
        this.studentId = studentId;
        this.lrn = lrn;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gradeLevel = gradeLevel;
        this.section = section;
    }
    
    // Getters and Setters
    public int getStudentId() {
        return studentId;
    }
    
    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }
    
    public String getLrn() {
        return lrn;
    }
    
    public void setLrn(String lrn) {
        this.lrn = lrn;
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
    
    public int getGradeLevel() {
        return gradeLevel;
    }
    
    public void setGradeLevel(int gradeLevel) {
        this.gradeLevel = gradeLevel;
    }
    
    public String getSection() {
        return section;
    }
    
    public void setSection(String section) {
        this.section = section;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Get full name of student
     * @return Full name as "FirstName LastName"
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    @Override
    public String toString() {
        return "Student{" +
                "studentId=" + studentId +
                ", lrn='" + lrn + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gradeLevel=" + gradeLevel +
                ", section='" + section + '\'' +
                '}';
    }
}
