package com.elibrary.database;

import com.elibrary.models.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Student operations
 */
public class StudentDAO {
    private Connection connection;
    
    public StudentDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Authenticate student by LRN
     * @param lrn Student LRN
     * @return Student object if found, null otherwise
     */
    public Student authenticateStudent(String lrn) {
        String query = "SELECT * FROM STUDENT WHERE lrn = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, lrn);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractStudentFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating student: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get student by ID
     * @param studentId Student ID
     * @return Student object if found, null otherwise
     */
    public Student getStudentById(int studentId) {
        String query = "SELECT * FROM STUDENT WHERE student_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractStudentFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting student: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Get all students
     * @return List of all students
     */
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String query = "SELECT * FROM STUDENT ORDER BY last_name, first_name";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                students.add(extractStudentFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all students: " + e.getMessage());
        }
        
        return students;
    }
    
    /**
     * Add new student
     * @param student Student object to add
     * @return true if successful, false otherwise
     */
    public boolean addStudent(Student student) {
        String query = "INSERT INTO STUDENT (lrn, first_name, last_name, grade_level, section) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, student.getLrn());
            stmt.setString(2, student.getFirstName());
            stmt.setString(3, student.getLastName());
            stmt.setInt(4, student.getGradeLevel());
            stmt.setString(5, student.getSection());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding student: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update student information
     * @param student Student object with updated information
     * @return true if successful, false otherwise
     */
    public boolean updateStudent(Student student) {
        String query = "UPDATE STUDENT SET first_name = ?, last_name = ?, grade_level = ?, section = ? WHERE student_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, student.getFirstName());
            stmt.setString(2, student.getLastName());
            stmt.setInt(3, student.getGradeLevel());
            stmt.setString(4, student.getSection());
            stmt.setInt(5, student.getStudentId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating student: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete student
     * @param studentId Student ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteStudent(int studentId) {
        String query = "DELETE FROM STUDENT WHERE student_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting student: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Extract Student object from ResultSet
     * @param rs ResultSet containing student data
     * @return Student object
     * @throws SQLException if error occurs
     */
    private Student extractStudentFromResultSet(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setStudentId(rs.getInt("student_id"));
        student.setLrn(rs.getString("lrn"));
        student.setFirstName(rs.getString("first_name"));
        student.setLastName(rs.getString("last_name"));
        student.setGradeLevel(rs.getInt("grade_level"));
        student.setSection(rs.getString("section"));
        student.setCreatedAt(rs.getTimestamp("created_at"));
        return student;
    }
}
