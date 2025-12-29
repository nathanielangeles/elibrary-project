package com.elibrary.models;

import java.sql.Timestamp;

/**
 * AccessLog model class representing student book access tracking
 */
public class AccessLog {
    private int accessId;
    private int studentId;
    private int bookId;
    private String accessType; // VIEW or DOWNLOAD
    private Timestamp accessDate;
    
    // Additional fields for display
    private String studentName;
    private String bookTitle;
    
    // Constructors
    public AccessLog() {}
    
    public AccessLog(int studentId, int bookId, String accessType) {
        this.studentId = studentId;
        this.bookId = bookId;
        this.accessType = accessType;
    }
    
    // Getters and Setters
    public int getAccessId() {
        return accessId;
    }
    
    public void setAccessId(int accessId) {
        this.accessId = accessId;
    }
    
    public int getStudentId() {
        return studentId;
    }
    
    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }
    
    public int getBookId() {
        return bookId;
    }
    
    public void setBookId(int bookId) {
        this.bookId = bookId;
    }
    
    public String getAccessType() {
        return accessType;
    }
    
    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }
    
    public Timestamp getAccessDate() {
        return accessDate;
    }
    
    public void setAccessDate(Timestamp accessDate) {
        this.accessDate = accessDate;
    }
    
    public String getStudentName() {
        return studentName;
    }
    
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
    
    public String getBookTitle() {
        return bookTitle;
    }
    
    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }
    
    @Override
    public String toString() {
        return "AccessLog{" +
                "accessId=" + accessId +
                ", studentId=" + studentId +
                ", bookId=" + bookId +
                ", accessType='" + accessType + '\'' +
                ", accessDate=" + accessDate +
                '}';
    }
}
