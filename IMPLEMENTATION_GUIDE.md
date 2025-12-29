# E-Library System - Technical Implementation Guide

## Overview
This document provides detailed technical information for developers working with the E-Library Management System.

## Architecture

### System Design Pattern
The application follows the Model-View-Controller (MVC) architectural pattern:

- **Model**: Located in `com.elibrary.models` package
- **View**: FXML files in `src/main/resources/fxml`
- **Controller**: Located in `com.elibrary.controllers` package

### Database Layer
The Data Access Object (DAO) pattern is used for database operations:
- Each model has a corresponding DAO class
- Database connection is managed via Singleton pattern
- All SQL operations are parameterized to prevent injection attacks

## Key Components

### 1. Database Connection (DatabaseConnection.java)
```java
// Singleton pattern ensures single database connection
DatabaseConnection.getInstance().getConnection()
```

**Configuration:**
- Update URL, USERNAME, PASSWORD in DatabaseConnection.java
- Default: localhost:3306/elibrary_db
- Uses connection pooling for efficiency

### 2. Session Management (SessionManager.java)
```java
// Singleton session manager
SessionManager session = SessionManager.getInstance();

// Student login
session.loginStudent(student);

// Admin login
session.loginAdmin(admin);

// Check authentication
if (session.isStudentLoggedIn()) {
    Student currentStudent = session.getCurrentStudent();
}
```

### 3. Password Security (PasswordUtil.java)
```java
// Hash password using SHA-256
String hashedPassword = PasswordUtil.hashPassword(plainPassword);

// Verify password
boolean isValid = PasswordUtil.verifyPassword(plainPassword, hashedPassword);
```

**Security Notes:**
- Passwords are hashed using SHA-256 before storage
- Plain text passwords are never stored in database
- Consider implementing salt for additional security

### 4. PDF Watermarking (PDFWatermarkUtil.java)
```java
// Add watermark to PDF
File watermarkedFile = PDFWatermarkUtil.createWatermarkedCopy(
    originalFilePath,
    studentLRN,
    downloadDirectory
);
```

**How it works:**
1. Original PDF is loaded using Apache PDFBox
2. LRN watermark is added to first page bottom-left
3. New PDF is created in download directory
4. Original file remains unchanged

## Database Operations

### Student Operations (StudentDAO.java)

**Authentication:**
```java
StudentDAO studentDAO = new StudentDAO();
Student student = studentDAO.authenticateStudent(lrn);
```

**CRUD Operations:**
```java
// Create
boolean success = studentDAO.addStudent(student);

// Read
Student student = studentDAO.getStudentById(id);
List<Student> students = studentDAO.getAllStudents();

// Update
boolean success = studentDAO.updateStudent(student);

// Delete
boolean success = studentDAO.deleteStudent(id);
```

### Book Operations (BookDAO.java)

**Get all books with statistics:**
```java
BookDAO bookDAO = new BookDAO();
List<Book> books = bookDAO.getAllBooks();
// Includes view count and download count
```

**Search functionality:**
```java
List<Book> results = bookDAO.searchBooks(searchTerm);
// Searches in title, author, and description
```

**Filter by category:**
```java
List<Book> books = bookDAO.getBooksByCategory(categoryId);
```

### Access Logging (AccessLogDAO.java)

**Log book access:**
```java
AccessLogDAO accessLogDAO = new AccessLogDAO();

// Log a view
accessLogDAO.logAccess(studentId, bookId, "VIEW");

// Log a download
accessLogDAO.logAccess(studentId, bookId, "DOWNLOAD");
```

**Get statistics:**
```java
int views = accessLogDAO.getViewCount(bookId);
int downloads = accessLogDAO.getDownloadCount(bookId);
List<AccessLog> logs = accessLogDAO.getAccessLogsByBook(bookId);
```

## JavaFX Controllers

### Controller Lifecycle

1. **Initialization**
```java
@FXML
public void initialize() {
    // Called after FXML loading
    // Initialize components here
    loadData();
    setupEventHandlers();
}
```

2. **Event Handling**
```java
@FXML
private void handleButtonClick(ActionEvent event) {
    // Handle button click
}
```

3. **Scene Navigation**
```java
MainApp.changeScene("/fxml/NextScene.fxml", "Window Title");
```

### Student Dashboard Controller Example

```java
public class StudentDashboardController {
    
    @FXML private GridPane bookGrid;
    @FXML private TextField searchField;
    @FXML private ComboBox<Category> categoryComboBox;
    
    private BookDAO bookDAO;
    private Student currentStudent;
    
    @FXML
    public void initialize() {
        bookDAO = new BookDAO();
        currentStudent = SessionManager.getInstance().getCurrentStudent();
        
        loadBooks();
        loadCategories();
    }
    
    private void loadBooks() {
        List<Book> books = bookDAO.getAllBooks();
        displayBooks(books);
    }
    
    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText();
        List<Book> results = bookDAO.searchBooks(searchTerm);
        displayBooks(results);
    }
    
    @FXML
    private void handleDownload(Book book) {
        try {
            // Log access
            AccessLogDAO accessLogDAO = new AccessLogDAO();
            accessLogDAO.logAccess(
                currentStudent.getStudentId(),
                book.getBookId(),
                "DOWNLOAD"
            );
            
            // Create watermarked PDF
            File watermarkedFile = PDFWatermarkUtil.createWatermarkedCopy(
                book.getFilePath(),
                currentStudent.getLrn(),
                System.getProperty("user.home") + "/Downloads"
            );
            
            showAlert("Success", "Book downloaded successfully!");
            
        } catch (IOException e) {
            showAlert("Error", "Failed to download book: " + e.getMessage());
        }
    }
}
```

### Admin Dashboard Controller Example

```java
public class AdminDashboardController {
    
    @FXML private TableView<Book> bookTable;
    @FXML private Label totalBooksLabel;
    @FXML private Label totalStudentsLabel;
    
    private BookDAO bookDAO;
    private StudentDAO studentDAO;
    
    @FXML
    public void initialize() {
        bookDAO = new BookDAO();
        studentDAO = new StudentDAO();
        
        loadStatistics();
        loadBooks();
    }
    
    private void loadStatistics() {
        int totalBooks = bookDAO.getAllBooks().size();
        int totalStudents = studentDAO.getAllStudents().size();
        
        totalBooksLabel.setText(String.valueOf(totalBooks));
        totalStudentsLabel.setText(String.valueOf(totalStudents));
    }
    
    @FXML
    private void handleAddBook() {
        // Open add book dialog
        // Implementation depends on UI design
    }
    
    @FXML
    private void handleDeleteBook() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        
        if (selectedBook != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirm Delete");
            confirmation.setContentText("Delete this book?");
            
            confirmation.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    boolean success = bookDAO.deleteBook(selectedBook.getBookId());
                    if (success) {
                        loadBooks(); // Refresh table
                        showAlert("Success", "Book deleted successfully!");
                    }
                }
            });
        }
    }
}
```

## FXML Files

### Basic Structure

```xml
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>
<?import javafx.geometry.Insets?>

<VBox xmlns="http://javafx.com/javafx"
      xmlns:fx="http://javafx.com/fxml"
      fx:controller="com.elibrary.controllers.ControllerName"
      styleClass="main-container"
      spacing="20">
    
    <!-- Your UI components here -->
    
</VBox>
```

### Connecting to Controller

```xml
<!-- TextField -->
<TextField fx:id="searchField" promptText="Search books..."/>

<!-- Button with action -->
<Button text="Search" onAction="#handleSearch"/>

<!-- TableView -->
<TableView fx:id="bookTable">
    <columns>
        <TableColumn text="Title" prefWidth="200"/>
        <TableColumn text="Author" prefWidth="150"/>
    </columns>
</TableView>
```

## File Storage

### Directory Structure
```
project-root/
├── library/
│   ├── books/           # PDF files
│   │   └── book_1.pdf
│   ├── covers/          # Cover images
│   │   └── book_1.jpg
│   └── downloads/       # Temporary watermarked files
```

### File Upload Implementation

```java
// Admin adds new book
FileChooser fileChooser = new FileChooser();
fileChooser.setTitle("Select PDF Book");
fileChooser.getExtensionFilters().add(
    new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
);

File selectedFile = fileChooser.showOpenDialog(stage);

if (selectedFile != null) {
    // Copy to library directory
    String destinationPath = "library/books/" + selectedFile.getName();
    Files.copy(selectedFile.toPath(), Paths.get(destinationPath));
    
    // Save path to database
    book.setFilePath(destinationPath);
    bookDAO.addBook(book);
}
```

## Error Handling

### Best Practices

1. **Try-Catch Blocks:**
```java
try {
    // Database operation
    bookDAO.addBook(book);
} catch (SQLException e) {
    showAlert("Database Error", "Failed to add book: " + e.getMessage());
    e.printStackTrace();
}
```

2. **Validation:**
```java
private boolean validateInput() {
    if (titleField.getText().trim().isEmpty()) {
        showAlert("Validation Error", "Title is required");
        return false;
    }
    
    if (yearField.getText().matches("\\d{4}")) {
        showAlert("Validation Error", "Invalid year format");
        return false;
    }
    
    return true;
}
```

3. **User Feedback:**
```java
private void showAlert(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
}
```

## Testing

### Unit Testing Example

```java
import org.junit.Test;
import static org.junit.Assert.*;

public class PasswordUtilTest {
    
    @Test
    public void testPasswordHashing() {
        String password = "test123";
        String hashed = PasswordUtil.hashPassword(password);
        
        assertNotNull(hashed);
        assertNotEquals(password, hashed);
    }
    
    @Test
    public void testPasswordVerification() {
        String password = "test123";
        String hashed = PasswordUtil.hashPassword(password);
        
        assertTrue(PasswordUtil.verifyPassword(password, hashed));
        assertFalse(PasswordUtil.verifyPassword("wrong", hashed));
    }
}
```

### Database Testing

```java
@Test
public void testStudentAuthentication() {
    StudentDAO studentDAO = new StudentDAO();
    
    // Test valid LRN
    Student student = studentDAO.authenticateStudent("123456789012");
    assertNotNull(student);
    assertEquals("Juan", student.getFirstName());
    
    // Test invalid LRN
    Student invalidStudent = studentDAO.authenticateStudent("000000000000");
    assertNull(invalidStudent);
}
```

## Performance Optimization

### 1. Database Connection Pooling
Consider implementing HikariCP for production:

```xml
<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
    <version>5.0.1</version>
</dependency>
```

### 2. Lazy Loading
Load book covers only when needed:

```java
// Load book list without images
List<Book> books = bookDAO.getAllBooks();

// Load cover image on demand
Image cover = new Image(book.getCoverImagePath());
```

### 3. Caching
Cache frequently accessed data:

```java
private Map<Integer, Category> categoryCache = new HashMap<>();

public Category getCategoryById(int id) {
    if (categoryCache.containsKey(id)) {
        return categoryCache.get(id);
    }
    
    Category category = categoryDAO.getCategoryById(id);
    categoryCache.put(id, category);
    return category;
}
```

## Security Considerations

### 1. SQL Injection Prevention
Always use PreparedStatement:

```java
// GOOD
String query = "SELECT * FROM STUDENT WHERE lrn = ?";
PreparedStatement stmt = connection.prepareStatement(query);
stmt.setString(1, lrn);

// BAD - Vulnerable to SQL injection
String query = "SELECT * FROM STUDENT WHERE lrn = '" + lrn + "'";
Statement stmt = connection.createStatement();
```

### 2. Input Validation
Validate all user inputs:

```java
private boolean isValidLRN(String lrn) {
    return lrn != null && lrn.matches("\\d{12}");
}

private boolean isValidYear(String year) {
    try {
        int y = Integer.parseInt(year);
        return y >= 1900 && y <= LocalDate.now().getYear();
    } catch (NumberFormatException e) {
        return false;
    }
}
```

### 3. File Security
Validate file types and sizes:

```java
private boolean isValidPDF(File file) {
    if (!file.getName().toLowerCase().endsWith(".pdf")) {
        return false;
    }
    
    // Check file size (max 50MB)
    long maxSize = 50 * 1024 * 1024;
    if (file.length() > maxSize) {
        return false;
    }
    
    return true;
}
```

## Deployment

### 1. Create Executable JAR

```bash
mvn clean package
```

The JAR file will be in `target/elibrary-system-1.0.0.jar`

### 2. Create Installation Package

**Windows (using Launch4j):**
- Create Windows executable (.exe)
- Bundle JRE for standalone distribution

**macOS:**
- Create .app bundle
- Sign application for distribution

**Linux:**
- Create .deb or .rpm package
- Include desktop entry file

### 3. Database Migration

For production deployment:

```sql
-- Create production database
CREATE DATABASE elibrary_prod;

-- Import schema
mysql -u root -p elibrary_prod < schema.sql

-- Update connection string
jdbc:mysql://production-server:3306/elibrary_prod
```

## Troubleshooting Common Issues

### Issue: JavaFX Runtime Components Missing

**Solution:**
```bash
# Run with module path
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -jar app.jar
```

### Issue: MySQL Connection Timeout

**Solution:**
```java
// Increase connection timeout
private static final String URL = 
    "jdbc:mysql://localhost:3306/elibrary_db?connectTimeout=30000";
```

### Issue: PDF Watermark Not Showing

**Solution:**
- Verify PDFBox dependency is included
- Check file permissions
- Ensure temp directory is writable

### Issue: Memory Leak on Book Loading

**Solution:**
```java
// Dispose of images properly
imageView.setImage(null);
System.gc(); // Suggest garbage collection
```

## Future Enhancements

### Suggested Features

1. **Email Notifications**
   - Send email when books are added
   - Notify students of new releases

2. **Advanced Search**
   - Full-text search in PDF content
   - Fuzzy matching for typos

3. **User Reviews**
   - Allow students to rate books
   - Add review/comment system

4. **Reading Progress**
   - Track reading progress
   - Bookmarks and highlights

5. **Mobile App**
   - Android/iOS companion app
   - Sync across devices

## Resources

### Documentation
- JavaFX: https://openjfx.io/
- MySQL: https://dev.mysql.com/doc/
- Apache PDFBox: https://pdfbox.apache.org/

### Tools
- Scene Builder: Visual FXML editor
- MySQL Workbench: Database design tool
- IntelliJ IDEA: Recommended IDE

## Support

For technical questions:
- GitHub Issues: [repository URL]
- Email: dev@elibrary.example.com
- Wiki: [wiki URL]

---

**Document Version:** 1.0  
**Last Updated:** December 2025
