package com.elibrary.controllers;

import com.elibrary.MainApp;
import com.elibrary.database.AccessLogDAO;
import com.elibrary.database.BookDAO;
import com.elibrary.database.CategoryDAO;
import com.elibrary.models.Book;
import com.elibrary.models.Category;
import com.elibrary.models.Student;
import com.elibrary.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

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
        
        // Cover - use actual image if available, otherwise show placeholder
        StackPane coverPlaceholder = new StackPane();
        coverPlaceholder.setPrefSize(170, 220);
        coverPlaceholder.setMaxSize(170, 220);
        coverPlaceholder.setMinSize(170, 220);
        
        if (book.getCoverImagePath() != null && !book.getCoverImagePath().isEmpty()) {
            // Try to load actual cover image
            try {
                File coverFile = new File(book.getCoverImagePath());
                if (coverFile.exists()) {
                    javafx.scene.image.Image coverImage = new javafx.scene.image.Image(
                        coverFile.toURI().toString(), 170, 220, false, true
                    );
                    ImageView imageView = new ImageView(coverImage);
                    imageView.setFitWidth(170);
                    imageView.setFitHeight(220);
                    imageView.setPreserveRatio(false);
                    imageView.setStyle("-fx-background-radius: 5px;");
                    coverPlaceholder.getChildren().add(imageView);
                    coverPlaceholder.setStyle("-fx-background-radius: 5px;");
                } else {
                    // Cover file doesn't exist, use placeholder
                    addPlaceholderCover(coverPlaceholder);
                }
            } catch (Exception e) {
                // Error loading image, use placeholder
                addPlaceholderCover(coverPlaceholder);
            }
        } else {
            // No cover image path, use placeholder
            addPlaceholderCover(coverPlaceholder);
        }
        
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
        
        // Single View/Open button - centered and wider
        Button openButton = new Button("Open PDF");
        openButton.setPrefWidth(170);
        openButton.setStyle(
            "-fx-background-color: #3498db; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 12px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 8px 16px; " +
            "-fx-background-radius: 4px; " +
            "-fx-cursor: hand;"
        );
        openButton.setOnAction(e -> handleOpenBook(book));
        
        // Hover effect for button
        openButton.setOnMouseEntered(e -> openButton.setStyle(
            "-fx-background-color: #2980b9; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 12px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 8px 16px; " +
            "-fx-background-radius: 4px; " +
            "-fx-cursor: hand;"
        ));
        openButton.setOnMouseExited(e -> openButton.setStyle(
            "-fx-background-color: #3498db; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 12px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 8px 16px; " +
            "-fx-background-radius: 4px; " +
            "-fx-cursor: hand;"
        ));
        
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
            openButton
        );
        
        // Make entire card clickable for opening
        card.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) { // Double-click to open
                handleOpenBook(book);
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
     * Add placeholder cover to StackPane
     */
    private void addPlaceholderCover(StackPane coverPlaceholder) {
        coverPlaceholder.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #667eea 0%, #764ba2 100%); " +
            "-fx-background-radius: 5px;"
        );
        Label coverLabel = new Label("📚");
        coverLabel.setStyle("-fx-font-size: 48px;");
        coverPlaceholder.getChildren().add(coverLabel);
    }
    
    /**
     * Handle opening book in embedded PDF viewer
     */
    private void handleOpenBook(Book book) {
        try {
            // Log the view
            accessLogDAO.logAccess(currentStudent.getStudentId(), book.getBookId(), "VIEW");
            
            // Check if book file exists
            File bookFile = new File(book.getFilePath());
            if (!bookFile.exists()) {
                showAlert(Alert.AlertType.ERROR, "File Not Found", 
                         "The book file does not exist. Please contact the administrator.");
                return;
            }
            
            statusLabel.setText("Opening book...");
            
            // Load PDF viewer FXML
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/fxml/PDFViewer.fxml")
            );
            javafx.scene.Parent root = loader.load();
            
            // Get controller and load PDF
            PDFViewerController viewerController = loader.getController();
            viewerController.loadPDF(bookFile, book.getTitle(), book.getAuthor());
            
            // Create and show stage
            javafx.stage.Stage viewerStage = new javafx.stage.Stage();
            viewerStage.setTitle("PDF Viewer - " + book.getTitle());
            viewerStage.setScene(new javafx.scene.Scene(root));
            viewerStage.setMaximized(true);
            
            // Cleanup on close
            viewerStage.setOnCloseRequest(e -> viewerController.cleanup());
            
            viewerStage.show();
            
            statusLabel.setText("Book opened successfully");
            
            // Refresh book statistics
            loadAllBooks();
            
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Failed to open book");
            showAlert(Alert.AlertType.ERROR, "Error Opening Book", 
                     "Failed to open the book: " + e.getMessage());
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
            content.getChildren().add(new Label("No history yet. Start reading books!"));
        } else {
            TableView<com.elibrary.models.AccessLog> table = new TableView<>();
            table.setPrefHeight(400);
            
            TableColumn<com.elibrary.models.AccessLog, String> bookCol = new TableColumn<>("Book Title");
            bookCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getBookTitle()));
            bookCol.setPrefWidth(350);
            
            TableColumn<com.elibrary.models.AccessLog, String> dateCol = new TableColumn<>("Date Viewed");
            dateCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getAccessDate().toString()));
            dateCol.setPrefWidth(200);
            
            table.getColumns().addAll(bookCol, dateCol);
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
