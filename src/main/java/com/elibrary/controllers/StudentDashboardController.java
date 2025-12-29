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
    @FXML private FlowPane booksGrid;
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
        try {
            System.out.println("StudentDashboardController initializing...");
            
            bookDAO = new BookDAO();
            categoryDAO = new CategoryDAO();
            accessLogDAO = new AccessLogDAO();
            currentStudent = SessionManager.getInstance().getCurrentStudent();
            
            System.out.println("DAOs initialized successfully");
            
            // Set welcome message
            if (currentStudent != null) {
                welcomeLabel.setText("Welcome, " + currentStudent.getFirstName() + "!");
                System.out.println("Welcome label set for: " + currentStudent.getFirstName());
            } else {
                System.err.println("WARNING: Current student is null!");
            }
            
            // Load categories
            loadCategories();
            System.out.println("Categories loaded");
            
            // Load all books
            loadAllBooks();
            System.out.println("Books loaded");
            
            // Setup search on Enter key
            searchField.setOnAction(e -> handleSearch());
            
            System.out.println("StudentDashboardController initialization complete");
        } catch (Exception e) {
            System.err.println("ERROR in StudentDashboardController.initialize(): " + e.getMessage());
            e.printStackTrace();
        }
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
     * Display books in responsive grid
     */
    private void displayBooks(List<Book> books) {
        try {
            booksGrid.getChildren().clear();
            
            System.out.println("Displaying " + books.size() + " books");
            
            if (books.isEmpty()) {
                noResultsPane.setVisible(true);
                booksGrid.setVisible(false);
                resultsLabel.setText("No books found");
                System.out.println("No books to display");
            } else {
                noResultsPane.setVisible(false);
                booksGrid.setVisible(true);
                resultsLabel.setText("Showing " + books.size() + " book(s)");
                
                for (Book book : books) {
                    try {
                        VBox bookCard = createBookCard(book);
                        booksGrid.getChildren().add(bookCard);
                    } catch (Exception e) {
                        System.err.println("Error creating card for book: " + book.getTitle());
                        e.printStackTrace();
                    }
                }
                System.out.println("Books displayed successfully in responsive grid");
            }
        } catch (Exception e) {
            System.err.println("ERROR in displayBooks: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Create a responsive book card UI component
     */
    private VBox createBookCard(Book book) {
        VBox card = new VBox(12);
        card.setAlignment(Pos.TOP_CENTER);
        card.getStyleClass().add("book-card");
        card.setPrefWidth(200);
        card.setMinWidth(200);
        card.setMaxWidth(200);
        card.setPrefHeight(340);
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-padding: 15px; " +
            "-fx-border-color: #e0e0e0; " +
            "-fx-border-width: 1px; " +
            "-fx-border-radius: 8px; " +
            "-fx-background-radius: 8px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2); " +
            "-fx-cursor: hand;"
        );
        
        // Hover effect
        card.setOnMouseEntered(e -> {
            card.setStyle(
                "-fx-background-color: white; " +
                "-fx-padding: 15px; " +
                "-fx-border-color: #3498db; " +
                "-fx-border-width: 2px; " +
                "-fx-border-radius: 8px; " +
                "-fx-background-radius: 8px; " +
                "-fx-effect: dropshadow(gaussian, rgba(52,152,219,0.3), 12, 0, 0, 4); " +
                "-fx-cursor: hand;"
            );
        });
        
        card.setOnMouseExited(e -> {
            card.setStyle(
                "-fx-background-color: white; " +
                "-fx-padding: 15px; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-width: 1px; " +
                "-fx-border-radius: 8px; " +
                "-fx-background-radius: 8px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2); " +
                "-fx-cursor: hand;"
            );
        });
        
        // Cover placeholder
        StackPane coverPlaceholder = new StackPane();
        coverPlaceholder.setPrefSize(170, 220);
        coverPlaceholder.setMaxSize(170, 220);
        coverPlaceholder.setMinSize(170, 220);
        coverPlaceholder.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #667eea 0%, #764ba2 100%); " +
            "-fx-background-radius: 5px;"
        );
        
        Label coverLabel = new Label("📚");
        coverLabel.setStyle("-fx-font-size: 48px;");
        coverPlaceholder.getChildren().add(coverLabel);
        
        // Book info container with fixed height
        VBox infoBox = new VBox(8);
        infoBox.setAlignment(Pos.CENTER);
        infoBox.setPrefWidth(190);
        infoBox.setMaxWidth(190);
        infoBox.setMinHeight(60);
        infoBox.setMaxHeight(60);
        
        // Title with text wrapping and ellipsis
        Label titleLabel = new Label(book.getTitle());
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(180);
        titleLabel.setMaxHeight(36); // 2 lines max
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        titleLabel.setStyle(
            "-fx-font-size: 13px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #2c3e50;"
        );
        
        // Author
        Label authorLabel = new Label("by " + book.getAuthor());
        authorLabel.setStyle(
            "-fx-font-size: 11px; " +
            "-fx-text-fill: #7f8c8d; " +
            "-fx-text-alignment: center;"
        );
        authorLabel.setMaxWidth(180);
        
        infoBox.getChildren().addAll(titleLabel, authorLabel);
        
        // Category badge
        Label categoryBadge = new Label(book.getCategoryName());
        categoryBadge.setStyle(
            "-fx-background-color: #3498db; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 3px 8px; " +
            "-fx-background-radius: 10px; " +
            "-fx-font-size: 10px; " +
            "-fx-font-weight: bold;"
        );
        
        // Action buttons with consistent width
        HBox buttonBox = new HBox(8);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPrefWidth(190);
        
        Button viewButton = new Button("View");
        viewButton.setPrefWidth(85);
        viewButton.setStyle(
            "-fx-background-color: #3498db; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 11px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 6px 12px; " +
            "-fx-background-radius: 4px; " +
            "-fx-cursor: hand;"
        );
        viewButton.setOnAction(e -> handleViewBook(book));
        
        Button downloadButton = new Button("Download");
        downloadButton.setPrefWidth(85);
        downloadButton.setStyle(
            "-fx-background-color: #27ae60; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 11px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 6px 12px; " +
            "-fx-background-radius: 4px; " +
            "-fx-cursor: hand;"
        );
        downloadButton.setOnAction(e -> handleDownloadBook(book));
        
        // Hover effects for buttons
        viewButton.setOnMouseEntered(e -> viewButton.setStyle(
            "-fx-background-color: #2980b9; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 11px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 6px 12px; " +
            "-fx-background-radius: 4px; " +
            "-fx-cursor: hand;"
        ));
        viewButton.setOnMouseExited(e -> viewButton.setStyle(
            "-fx-background-color: #3498db; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 11px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 6px 12px; " +
            "-fx-background-radius: 4px; " +
            "-fx-cursor: hand;"
        ));
        
        downloadButton.setOnMouseEntered(e -> downloadButton.setStyle(
            "-fx-background-color: #229954; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 11px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 6px 12px; " +
            "-fx-background-radius: 4px; " +
            "-fx-cursor: hand;"
        ));
        downloadButton.setOnMouseExited(e -> downloadButton.setStyle(
            "-fx-background-color: #27ae60; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 11px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 6px 12px; " +
            "-fx-background-radius: 4px; " +
            "-fx-cursor: hand;"
        ));
        
        buttonBox.getChildren().addAll(viewButton, downloadButton);
        
        // Add all components to card with proper spacing
        Region spacer1 = new Region();
        spacer1.setPrefHeight(5);
        
        Region spacer2 = new Region();
        spacer2.setPrefHeight(5);
        
        card.getChildren().addAll(
            coverPlaceholder,
            spacer1,
            infoBox,
            categoryBadge,
            spacer2,
            buttonBox
        );
        
        // Make entire card clickable for viewing
        card.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) { // Double-click to view
                handleViewBook(book);
            }
        });
        
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
