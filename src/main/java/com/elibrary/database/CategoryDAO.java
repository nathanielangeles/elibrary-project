package com.elibrary.database;

import com.elibrary.models.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Category operations
 */
public class CategoryDAO {
    private Connection connection;
    
    public CategoryDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Get all categories
     * @return List of all categories
     */
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String query = "SELECT * FROM CATEGORY ORDER BY category_name";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                categories.add(extractCategoryFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all categories: " + e.getMessage());
        }
        
        return categories;
    }
    
    /**
     * Get category by ID
     * @param categoryId Category ID
     * @return Category object if found, null otherwise
     */
    public Category getCategoryById(int categoryId) {
        String query = "SELECT * FROM CATEGORY WHERE category_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, categoryId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractCategoryFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting category: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Add new category
     * @param category Category object to add
     * @return true if successful, false otherwise
     */
    public boolean addCategory(Category category) {
        String query = "INSERT INTO CATEGORY (category_name) VALUES (?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, category.getCategoryName());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding category: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update category
     * @param category Category object with updated information
     * @return true if successful, false otherwise
     */
    public boolean updateCategory(Category category) {
        String query = "UPDATE CATEGORY SET category_name = ? WHERE category_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, category.getCategoryName());
            stmt.setInt(2, category.getCategoryId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating category: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete category
     * @param categoryId Category ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteCategory(int categoryId) {
        String query = "DELETE FROM CATEGORY WHERE category_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, categoryId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting category: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Extract Category object from ResultSet
     * @param rs ResultSet containing category data
     * @return Category object
     * @throws SQLException if error occurs
     */
    private Category extractCategoryFromResultSet(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setCategoryId(rs.getInt("category_id"));
        category.setCategoryName(rs.getString("category_name"));
        category.setCreatedAt(rs.getTimestamp("created_at"));
        return category;
    }
}
