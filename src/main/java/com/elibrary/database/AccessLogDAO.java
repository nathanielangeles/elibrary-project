package com.elibrary.database;

import com.elibrary.models.AccessLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for AccessLog operations
 */
public class AccessLogDAO {
    private Connection connection;
    
    public AccessLogDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Log a book access (view or download)
     * @param studentId Student ID
     * @param bookId Book ID
     * @param accessType "VIEW" or "DOWNLOAD"
     * @return true if successful, false otherwise
     */
    public boolean logAccess(int studentId, int bookId, String accessType) {
        String query = "INSERT INTO ACCESS_LOG (student_id, book_id, access_type) VALUES (?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, bookId);
            stmt.setString(3, accessType);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error logging access: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get all access logs for a specific book
     * @param bookId Book ID
     * @return List of access logs
     */
    public List<AccessLog> getAccessLogsByBook(int bookId) {
        List<AccessLog> logs = new ArrayList<>();
        String query = "SELECT al.*, CONCAT(s.first_name, ' ', s.last_name) as student_name, b.title as book_title " +
                      "FROM ACCESS_LOG al " +
                      "JOIN STUDENT s ON al.student_id = s.student_id " +
                      "JOIN BOOK b ON al.book_id = b.book_id " +
                      "WHERE al.book_id = ? " +
                      "ORDER BY al.access_date DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                logs.add(extractAccessLogFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting access logs: " + e.getMessage());
        }
        
        return logs;
    }
    
    /**
     * Get all access logs for a specific student
     * @param studentId Student ID
     * @return List of access logs
     */
    public List<AccessLog> getAccessLogsByStudent(int studentId) {
        List<AccessLog> logs = new ArrayList<>();
        String query = "SELECT al.*, CONCAT(s.first_name, ' ', s.last_name) as student_name, b.title as book_title " +
                      "FROM ACCESS_LOG al " +
                      "JOIN STUDENT s ON al.student_id = s.student_id " +
                      "JOIN BOOK b ON al.book_id = b.book_id " +
                      "WHERE al.student_id = ? " +
                      "ORDER BY al.access_date DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                logs.add(extractAccessLogFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting access logs: " + e.getMessage());
        }
        
        return logs;
    }
    
    /**
     * Get view count for a specific book
     * @param bookId Book ID
     * @return Number of views
     */
    public int getViewCount(int bookId) {
        String query = "SELECT COUNT(*) as count FROM ACCESS_LOG WHERE book_id = ? AND access_type = 'VIEW'";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Error getting view count: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Get download count for a specific book
     * @param bookId Book ID
     * @return Number of downloads
     */
    public int getDownloadCount(int bookId) {
        String query = "SELECT COUNT(*) as count FROM ACCESS_LOG WHERE book_id = ? AND access_type = 'DOWNLOAD'";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Error getting download count: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Get all access logs (for admin statistics)
     * @return List of all access logs
     */
    public List<AccessLog> getAllAccessLogs() {
        List<AccessLog> logs = new ArrayList<>();
        String query = "SELECT al.*, CONCAT(s.first_name, ' ', s.last_name) as student_name, b.title as book_title " +
                      "FROM ACCESS_LOG al " +
                      "JOIN STUDENT s ON al.student_id = s.student_id " +
                      "JOIN BOOK b ON al.book_id = b.book_id " +
                      "ORDER BY al.access_date DESC " +
                      "LIMIT 100";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                logs.add(extractAccessLogFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all access logs: " + e.getMessage());
        }
        
        return logs;
    }
    
    /**
     * Extract AccessLog object from ResultSet
     * @param rs ResultSet containing access log data
     * @return AccessLog object
     * @throws SQLException if error occurs
     */
    private AccessLog extractAccessLogFromResultSet(ResultSet rs) throws SQLException {
        AccessLog log = new AccessLog();
        log.setAccessId(rs.getInt("access_id"));
        log.setStudentId(rs.getInt("student_id"));
        log.setBookId(rs.getInt("book_id"));
        log.setAccessType(rs.getString("access_type"));
        log.setAccessDate(rs.getTimestamp("access_date"));
        log.setStudentName(rs.getString("student_name"));
        log.setBookTitle(rs.getString("book_title"));
        return log;
    }
}
