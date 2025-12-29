package com.elibrary.database;

import com.elibrary.models.Admin;
import com.elibrary.utils.PasswordUtil;

import java.sql.*;

/**
 * Data Access Object for Admin operations
 */
public class AdminDAO {
    private Connection connection;
    
    public AdminDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Authenticate admin by username and password
     * @param username Admin username
     * @param password Admin password (plain text)
     * @return Admin object if authenticated, null otherwise
     */
    public Admin authenticateAdmin(String username, String password) {
        String query = "SELECT * FROM ADMIN WHERE username = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                
                // Verify password
                if (PasswordUtil.verifyPassword(password, storedPassword)) {
                    return extractAdminFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating admin: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get admin by ID
     * @param adminId Admin ID
     * @return Admin object if found, null otherwise
     */
    public Admin getAdminById(int adminId) {
        String query = "SELECT * FROM ADMIN WHERE admin_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, adminId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractAdminFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting admin: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Add new admin
     * @param admin Admin object to add
     * @param plainPassword Plain text password
     * @return true if successful, false otherwise
     */
    public boolean addAdmin(Admin admin, String plainPassword) {
        String query = "INSERT INTO ADMIN (first_name, last_name, username, password) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, admin.getFirstName());
            stmt.setString(2, admin.getLastName());
            stmt.setString(3, admin.getUsername());
            stmt.setString(4, PasswordUtil.hashPassword(plainPassword));
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding admin: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update admin password
     * @param adminId Admin ID
     * @param newPassword New plain text password
     * @return true if successful, false otherwise
     */
    public boolean updateAdminPassword(int adminId, String newPassword) {
        String query = "UPDATE ADMIN SET password = ? WHERE admin_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, PasswordUtil.hashPassword(newPassword));
            stmt.setInt(2, adminId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating admin password: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Extract Admin object from ResultSet
     * @param rs ResultSet containing admin data
     * @return Admin object
     * @throws SQLException if error occurs
     */
    private Admin extractAdminFromResultSet(ResultSet rs) throws SQLException {
        Admin admin = new Admin();
        admin.setAdminId(rs.getInt("admin_id"));
        admin.setFirstName(rs.getString("first_name"));
        admin.setLastName(rs.getString("last_name"));
        admin.setUsername(rs.getString("username"));
        admin.setCreatedAt(rs.getTimestamp("created_at"));
        // Note: We don't set the password field for security
        return admin;
    }
}
