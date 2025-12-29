package com.elibrary.controllers;

import com.elibrary.MainApp;
import com.elibrary.database.AccessLogDAO;
import com.elibrary.database.BookDAO;
import com.elibrary.database.CategoryDAO;
import com.elibrary.models.Book;
import com.elibrary.models.Category;
import com.elibrary.models.Student;
import com.elibrary.utils.PDFWatermarkUtil;
import com.elibrary.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.util.List;

/**
 * Controller for Student Dashboard
 */
public class StudentDashboardController {
    
    @FXML private Label welcomeLabel;
    @FXML private TextField searchField;
    @FXML private ComboBox<Category> categoryComboBox;
    @FXML private GridPane booksGrid;
    @FXML private Label resultsLabel;
    @FXML private VBox noResultsPane;
    @FXML private Label statusLabel;
    @FXML private Label bookCountLabel;
    
    private BookDAO bookDAO;
    private CategoryDAO categoryDAO;
    private AccessLogDAO accessLogDAO;
    private Student currentStudent;
    private List<Book> currentBooks;
    
    @FXML
    public void initialize() {
        bookDAO = new BookDAO();
        categoryDAO = new CategoryDAO();
        accessLogDAO = new AccessLogDAO();
        currentStudent = SessionManager.getInstance().getCurrentStudent();
        
	// test fix
	System.out.println("=== DASHBOARD LOADING ===");
    	System.out.println("Welcome Label: " + (welcomeLabel != null ? "OK" : "NULL"));
    	System.out.println("Search Field: " + (searchField != null ? "OK" : "NULL"));
    	System.out.println("Books Grid: " + (booksGrid != null ? "OK" : "NULL"));

        // Set welcome message
        if (currentStudent != null) {
            welcomeLabel.setText("Welcome, " + currentStudent.getFirstName() + "!");
        }
        
        // Load categories
        loadCategories();
        
        // Load all books
        loadAllBooks();
        
        // Setup search on Enter key
        searchField.setOnAction(e -> handleSearch());
    }
    
    /**
     * Load categories into ComboBox
     */
    private void loadCategories() {
        List<Category> categories = categoryDAO.getAllCategories();
        categoryComboBox.getItems().clear();
        
        // Add "All Categories" option
        Category allCategories = new Category();
        allCategories.setCategoryId(0);
        allCategories.setCategoryName("All Categories");
        categoryComboBox.getItems().add(allCategories);
        
        // Add actual categories
        categoryComboBox.getItems().addAll(categories);
        categoryComboBox.getSelectionModel().selectFirst();
    }
    
    /**
     * Load all books
     */
    private void loadAllBooks() {
        currentBooks = bookDAO.getAllBooks();
        displayBooks(currentBooks);
        updateStatusBar();
    }
    
    /**
     * Display books in grid
     */
    private void displayBooks(List<Book> books) {
        booksGrid.getChildren().clear();
        
        if (books.isEmpty()) {
            noResultsPane.setVisible(true);
            resultsLabel.setText("No books found");
        } else {
            noResultsPane.setVisible(false);
            resultsLabel.setText("Showing " + books.size() + " book(s)");
            
            int column = 0;
            int row = 0;
            int booksPerRow = 4;
            
            for (Book book : books) {
                VBox bookCard = createBookCard(book);
                booksGrid.add(bookCard, column, row);
                
                column++;
                if (column == booksPerRow) {
                    column = 0;
                    row++;
                }
            }
        }
    }
    
    /**
     * Create a book card UI component
     */
    private VBox createBookCard(Book book) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.TOP_CENTER);
        card.getStyleClass().add("book-card");
        card.setPrefWidth(220);
        card.setMaxWidth(220);
        card.setPrefHeight(320);
        
        // Cover placeholder (you can add actual image loading here)
        StackPane coverPlaceholder = new StackPane();
        coverPlaceholder.setPrefSize(180, 240);
        coverPlaceholder.setStyle("-fx-background-color: linear-gradient(to bottom, #3498db, #2980b9); -fx-background-radius: 5;");
        
        Label coverLabel = new Label("📚");
        coverLabel.setStyle("-fx-font-size: 48px;");
        coverPlaceholder.getChildren().add(coverLabel);
        
        // Book info
        VBox infoBox = new VBox(5);
        infoBox.setAlignment(Pos.CENTER);
        infoBox.setPrefWidth(200);
        
        Label titleLabel = new Label(book.getTitle());
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(200);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.getStyleClass().add("book-title");
        titleLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        
        Label authorLabel = new Label("by " + book.getAuthor());
        authorLabel.getStyleClass().add("book-author");
        authorLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d;");
        
        Label categoryLabel = new Label(book.getCategoryName());
        categoryLabel.getStyleClass().add("book-category");
        categoryLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #3498db;");
        
        infoBox.getChildren().addAll(titleLabel, authorLabel, categoryLabel);
        
        // Action buttons
        HBox buttonBox = new HBox(5);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button viewButton = new Button("View");
        viewButton.getStyleClass().addAll("button", "button-primary");
        viewButton.setStyle("-fx-font-size: 11px; -fx-padding: 5 15;");
        viewButton.setOnAction(e -> handleViewBook(book));
        
        Button downloadButton = new Button("Download");
        downloadButton.getStyleClass().addAll("button", "button-success");
        downloadButton.setStyle("-fx-font-size: 11px; -fx-padding: 5 15;");
        downloadButton.setOnAction(e -> handleDownloadBook(book));
        
        buttonBox.getChildren().addAll(viewButton, downloadButton);
        
        card.getChildren().addAll(coverPlaceholder, infoBox, buttonBox);
        
        // Make card clickable
        card.setOnMouseClicked(e -> handleViewBook(book));
        
        return card;
    }
    
    /**
     * Handle search button
     */
    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().trim();
        
        if (searchTerm.isEmpty()) {
            loadAllBooks();
        } else {
            currentBooks = bookDAO.searchBooks(searchTerm);
            displayBooks(currentBooks);
            statusLabel.setText("Search results for: " + searchTerm);
        }
    }
    
    /**
     * Handle clear search
     */
    @FXML
    private void handleClearSearch() {
        searchField.clear();
        categoryComboBox.getSelectionModel().selectFirst();
        loadAllBooks();
        statusLabel.setText("Ready");
    }
    
    /**
     * Handle category filter
     */
    @FXML
    private void handleCategoryFilter() {
        Category selectedCategory = categoryComboBox.getValue();
        
        if (selectedCategory != null) {
            if (selectedCategory.getCategoryId() == 0) {
                // All categories
                loadAllBooks();
            } else {
                currentBooks = bookDAO.getBooksByCategory(selectedCategory.getCategoryId());
                displayBooks(currentBooks);
                statusLabel.setText("Filtered by: " + selectedCategory.getCategoryName());
            }
        }
    }
    
    /**
     * Handle view book details
     */
    private void handleViewBook(Book book) {
        // Log the view
        accessLogDAO.logAccess(currentStudent.getStudentId(), book.getBookId(), "VIEW");
        
        // Create and show book details dialog
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Book Details");
        alert.setHeaderText(book.getTitle());
        
        VBox content = new VBox(10);
        content.setPrefWidth(500);
        
        content.getChildren().addAll(
            new Label("Author: " + book.getAuthor()),
            new Label("Category: " + book.getCategoryName()),
            new Label("Year Published: " + book.getYearPublished()),
            new Label(""),
            new Label("Description:"),
            new Text(book.getDescription() != null ? book.getDescription() : "No description available"),
            new Label(""),
            new Label("Statistics:"),
            new Label("Views: " + book.getViewCount()),
            new Label("Downloads: " + book.getDownloadCount())
        );
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);
        
        alert.getDialogPane().setContent(scrollPane);
        
        // Add download button to dialog
        ButtonType downloadButtonType = new ButtonType("Download", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().add(downloadButtonType);
        
        alert.showAndWait().ifPresent(response -> {
            if (response == downloadButtonType) {
                handleDownloadBook(book);
            }
        });
        
        // Refresh book to update view count
        loadAllBooks();
    }
    
    /**
     * Handle download book
     */
    private void handleDownloadBook(Book book) {
        try {
            // Check if book file exists
            File bookFile = new File(book.getFilePath());
            if (!bookFile.exists()) {
                showAlert(Alert.AlertType.ERROR, "File Not Found", 
                         "The book file does not exist. Please contact the administrator.");
                return;
            }
            
            // Choose download location
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Choose Download Location");
            directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            
            File selectedDirectory = directoryChooser.showDialog(MainApp.getPrimaryStage());
            
            if (selectedDirectory != null) {
                statusLabel.setText("Downloading book...");
                
                // Create watermarked PDF
                File watermarkedFile = PDFWatermarkUtil.createWatermarkedCopy(
                    book.getFilePath(),
                    currentStudent.getLrn(),
                    selectedDirectory.getAbsolutePath()
                );
                
                // Log the download
                accessLogDAO.logAccess(currentStudent.getStudentId(), book.getBookId(), "DOWNLOAD");
                
                statusLabel.setText("Download complete!");
                
                showAlert(Alert.AlertType.INFORMATION, "Download Successful", 
                         "Book downloaded successfully to:\n" + watermarkedFile.getAbsolutePath() + 
                         "\n\nYour LRN has been added as a watermark on the first page.");
                
                // Refresh book to update download count
                loadAllBooks();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Download failed");
            showAlert(Alert.AlertType.ERROR, "Download Failed", 
                     "Failed to download book: " + e.getMessage());
        }
    }
    
    /**
     * Handle view history
     */
    @FXML
    private void handleViewHistory() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("My History");
        alert.setHeaderText("Your Reading History");
        
        VBox content = new VBox(10);
        content.setPrefWidth(600);
        
        var logs = accessLogDAO.getAccessLogsByStudent(currentStudent.getStudentId());
        
        if (logs.isEmpty()) {
            content.getChildren().add(new Label("No history yet. Start exploring books!"));
        } else {
            TableView<com.elibrary.models.AccessLog> table = new TableView<>();
            table.setPrefHeight(400);
            
            TableColumn<com.elibrary.models.AccessLog, String> bookCol = new TableColumn<>("Book Title");
            bookCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getBookTitle()));
            bookCol.setPrefWidth(250);
            
            TableColumn<com.elibrary.models.AccessLog, String> typeCol = new TableColumn<>("Action");
            typeCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getAccessType()));
            typeCol.setPrefWidth(100);
            
            TableColumn<com.elibrary.models.AccessLog, String> dateCol = new TableColumn<>("Date");
            dateCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getAccessDate().toString()));
            dateCol.setPrefWidth(200);
            
            table.getColumns().addAll(bookCol, typeCol, dateCol);
            table.getItems().addAll(logs);
            
            content.getChildren().add(table);
        }
        
        alert.getDialogPane().setContent(content);
        alert.showAndWait();
    }
    
    /**
     * Handle logout
     */
    @FXML
    private void handleLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Logout");
        confirm.setHeaderText("Confirm Logout");
        confirm.setContentText("Are you sure you want to logout?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                SessionManager.getInstance().logout();
                MainApp.changeScene("/fxml/LoginSelection.fxml", "E-Library - Login");
            }
        });
    }
    
    /**
     * Update status bar with book count
     */
    private void updateStatusBar() {
        int totalBooks = bookDAO.getAllBooks().size();
        bookCountLabel.setText(totalBooks + " book(s) available");
    }
    
    /**
     * Show alert dialog
     */
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
