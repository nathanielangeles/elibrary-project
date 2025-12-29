package com.elibrary.database;

import com.elibrary.models.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Book operations
 */
public class BookDAO {
    private Connection connection;
    
    public BookDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Get all books with category names and statistics
     * @return List of all books
     */
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String query = "SELECT b.*, c.category_name, " +
                      "(SELECT COUNT(*) FROM ACCESS_LOG WHERE book_id = b.book_id AND access_type = 'VIEW') as view_count, " +
                      "(SELECT COUNT(*) FROM ACCESS_LOG WHERE book_id = b.book_id AND access_type = 'DOWNLOAD') as download_count " +
                      "FROM BOOK b " +
                      "JOIN CATEGORY c ON b.category_id = c.category_id " +
                      "ORDER BY b.title";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                books.add(extractBookFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all books: " + e.getMessage());
        }
        
        return books;
    }
    
    /**
     * Get book by ID
     * @param bookId Book ID
     * @return Book object if found, null otherwise
     */
    public Book getBookById(int bookId) {
        String query = "SELECT b.*, c.category_name, " +
                      "(SELECT COUNT(*) FROM ACCESS_LOG WHERE book_id = b.book_id AND access_type = 'VIEW') as view_count, " +
                      "(SELECT COUNT(*) FROM ACCESS_LOG WHERE book_id = b.book_id AND access_type = 'DOWNLOAD') as download_count " +
                      "FROM BOOK b " +
                      "JOIN CATEGORY c ON b.category_id = c.category_id " +
                      "WHERE b.book_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractBookFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting book: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Search books by title, author, or keyword
     * @param searchTerm Search term
     * @return List of matching books
     */
    public List<Book> searchBooks(String searchTerm) {
        List<Book> books = new ArrayList<>();
        String query = "SELECT b.*, c.category_name, " +
                      "(SELECT COUNT(*) FROM ACCESS_LOG WHERE book_id = b.book_id AND access_type = 'VIEW') as view_count, " +
                      "(SELECT COUNT(*) FROM ACCESS_LOG WHERE book_id = b.book_id AND access_type = 'DOWNLOAD') as download_count " +
                      "FROM BOOK b " +
                      "JOIN CATEGORY c ON b.category_id = c.category_id " +
                      "WHERE b.title LIKE ? OR b.author LIKE ? OR b.description LIKE ? " +
                      "ORDER BY b.title";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                books.add(extractBookFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching books: " + e.getMessage());
        }
        
        return books;
    }
    
    /**
     * Get books by category
     * @param categoryId Category ID
     * @return List of books in the category
     */
    public List<Book> getBooksByCategory(int categoryId) {
        List<Book> books = new ArrayList<>();
        String query = "SELECT b.*, c.category_name, " +
                      "(SELECT COUNT(*) FROM ACCESS_LOG WHERE book_id = b.book_id AND access_type = 'VIEW') as view_count, " +
                      "(SELECT COUNT(*) FROM ACCESS_LOG WHERE book_id = b.book_id AND access_type = 'DOWNLOAD') as download_count " +
                      "FROM BOOK b " +
                      "JOIN CATEGORY c ON b.category_id = c.category_id " +
                      "WHERE b.category_id = ? " +
                      "ORDER BY b.title";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, categoryId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                books.add(extractBookFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting books by category: " + e.getMessage());
        }
        
        return books;
    }
    
    /**
     * Add new book
     * @param book Book object to add
     * @return true if successful, false otherwise
     */
    public boolean addBook(Book book) {
        String query = "INSERT INTO BOOK (category_id, title, author, year_published, description, file_path, cover_image_path) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, book.getCategoryId());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthor());
            stmt.setInt(4, book.getYearPublished());
            stmt.setString(5, book.getDescription());
            stmt.setString(6, book.getFilePath());
            stmt.setString(7, book.getCoverImagePath());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding book: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update book information
     * @param book Book object with updated information
     * @return true if successful, false otherwise
     */
    public boolean updateBook(Book book) {
        String query = "UPDATE BOOK SET category_id = ?, title = ?, author = ?, year_published = ?, " +
                      "description = ?, file_path = ?, cover_image_path = ? WHERE book_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, book.getCategoryId());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthor());
            stmt.setInt(4, book.getYearPublished());
            stmt.setString(5, book.getDescription());
            stmt.setString(6, book.getFilePath());
            stmt.setString(7, book.getCoverImagePath());
            stmt.setInt(8, book.getBookId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating book: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete book
     * @param bookId Book ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteBook(int bookId) {
        String query = "DELETE FROM BOOK WHERE book_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, bookId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting book: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Extract Book object from ResultSet
     * @param rs ResultSet containing book data
     * @return Book object
     * @throws SQLException if error occurs
     */
    private Book extractBookFromResultSet(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setBookId(rs.getInt("book_id"));
        book.setCategoryId(rs.getInt("category_id"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setYearPublished(rs.getInt("year_published"));
        book.setDescription(rs.getString("description"));
        book.setFilePath(rs.getString("file_path"));
        book.setCoverImagePath(rs.getString("cover_image_path"));
        book.setCreatedAt(rs.getTimestamp("created_at"));
        book.setUpdatedAt(rs.getTimestamp("updated_at"));
        book.setCategoryName(rs.getString("category_name"));
        book.setViewCount(rs.getInt("view_count"));
        book.setDownloadCount(rs.getInt("download_count"));
        return book;
    }
}
