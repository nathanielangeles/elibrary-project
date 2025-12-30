package com.elibrary.controllers;

import com.elibrary.MainApp;
import com.elibrary.database.AccessLogDAO;
import com.elibrary.database.BookDAO;
import com.elibrary.database.CategoryDAO;
import com.elibrary.database.StudentDAO;
import com.elibrary.models.Admin;
import com.elibrary.models.Book;
import com.elibrary.models.Category;
import com.elibrary.utils.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

/**
 * Controller for Admin Dashboard
 */
public class AdminDashboardController {
    
    @FXML private Label welcomeLabel;
    @FXML private Label totalBooksLabel;
    @FXML private Label totalStudentsLabel;
    @FXML private Label totalViewsLabel;
    @FXML private Label totalDownloadsLabel;
    @FXML private TextField searchField;
    @FXML private TableView<Book> booksTable;
    @FXML private TableColumn<Book, Integer> idColumn;
    @FXML private TableColumn<Book, String> titleColumn;
    @FXML private TableColumn<Book, String> authorColumn;
    @FXML private TableColumn<Book, String> categoryColumn;
    @FXML private TableColumn<Book, Integer> yearColumn;
    @FXML private TableColumn<Book, Integer> viewsColumn;
    @FXML private TableColumn<Book, Integer> downloadsColumn;
    @FXML private TableColumn<Book, Void> actionsColumn;
    @FXML private Label selectionLabel;
    @FXML private Label statusLabel;
    
    private BookDAO bookDAO;
    private CategoryDAO categoryDAO;
    private StudentDAO studentDAO;
    private AccessLogDAO accessLogDAO;
    private Admin currentAdmin;
    
    @FXML
    public void initialize() {
        try {
            System.out.println("AdminDashboardController initializing...");
            
            bookDAO = new BookDAO();
            categoryDAO = new CategoryDAO();
            studentDAO = new StudentDAO();
            accessLogDAO = new AccessLogDAO();
            currentAdmin = SessionManager.getInstance().getCurrentAdmin();
            
            System.out.println("DAOs initialized successfully");
            
            // Set welcome message
            if (currentAdmin != null) {
                welcomeLabel.setText("Welcome, " + currentAdmin.getFirstName() + "!");
                System.out.println("Welcome label set for: " + currentAdmin.getFirstName());
            } else {
                System.err.println("WARNING: Current admin is null!");
            }
            
            // Setup table columns
            setupTableColumns();
            System.out.println("Table columns set up");
            
            // Load data
            loadStatistics();
            System.out.println("Statistics loaded");
            
            loadBooks();
            System.out.println("Books loaded");
            
            // Setup selection listener
            booksTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    selectionLabel.setText("Selected: " + newSelection.getTitle());
                } else {
                    selectionLabel.setText("No book selected");
                }
            });
            
            System.out.println("AdminDashboardController initialization complete");
        } catch (Exception e) {
            System.err.println("ERROR in AdminDashboardController.initialize(): " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Setup table columns
     */
    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("yearPublished"));
        viewsColumn.setCellValueFactory(new PropertyValueFactory<>("viewCount"));
        downloadsColumn.setCellValueFactory(new PropertyValueFactory<>("downloadCount"));
        
        // Setup actions column with buttons
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox pane = new HBox(5, editBtn, deleteBtn);
            
            {
                editBtn.getStyleClass().addAll("button", "button-primary");
                editBtn.setStyle("-fx-font-size: 10px; -fx-padding: 3 10;");
                deleteBtn.getStyleClass().addAll("button", "button-danger");
                deleteBtn.setStyle("-fx-font-size: 10px; -fx-padding: 3 10;");
                pane.setAlignment(Pos.CENTER);
                
                editBtn.setOnAction(event -> {
                    Book book = getTableView().getItems().get(getIndex());
                    editBook(book);
                });
                
                deleteBtn.setOnAction(event -> {
                    Book book = getTableView().getItems().get(getIndex());
                    deleteBook(book);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }
    
    /**
     * Load statistics
     */
    private void loadStatistics() {
        int totalBooks = bookDAO.getAllBooks().size();
        int totalStudents = studentDAO.getAllStudents().size();
        
        // Calculate total views and downloads
        int totalViews = 0;
        int totalDownloads = 0;
        for (Book book : bookDAO.getAllBooks()) {
            totalViews += book.getViewCount();
            totalDownloads += book.getDownloadCount();
        }
        
        totalBooksLabel.setText(String.valueOf(totalBooks));
        totalStudentsLabel.setText(String.valueOf(totalStudents));
        totalViewsLabel.setText(String.valueOf(totalViews));
        totalDownloadsLabel.setText(String.valueOf(totalDownloads));
    }
    
    /**
     * Load books into table
     */
    private void loadBooks() {
        List<Book> books = bookDAO.getAllBooks();
        booksTable.getItems().clear();
        booksTable.getItems().addAll(books);
        statusLabel.setText("Loaded " + books.size() + " books");
    }
    
    /**
     * Handle search
     */
    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().trim();
        
        if (searchTerm.isEmpty()) {
            loadBooks();
        } else {
            List<Book> results = bookDAO.searchBooks(searchTerm);
            booksTable.getItems().clear();
            booksTable.getItems().addAll(results);
            statusLabel.setText("Found " + results.size() + " book(s)");
        }
    }
    
    /**
     * Handle add book
     */
    @FXML
    private void handleAddBook() {
        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle("Add New Book");
        dialog.setHeaderText("Enter book details");
        
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
        
        // Create form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField titleField = new TextField();
        titleField.setPromptText("Book Title");
        TextField authorField = new TextField();
        authorField.setPromptText("Author Name");
        TextField yearField = new TextField();
        yearField.setPromptText("Year (e.g., 2024)");
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Book Description");
        descriptionArea.setPrefRowCount(3);
        
        ComboBox<Category> categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll(categoryDAO.getAllCategories());
        categoryCombo.setPromptText("Select Category");
        
        // PDF File Selection
        Label pdfFileLabel = new Label("No file selected");
        Button pdfFileButton = new Button("Choose PDF File");
        final File[] selectedPdfFile = {null};
        
        pdfFileButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select PDF Book");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
            File file = fileChooser.showOpenDialog(dialog.getOwner());
            if (file != null) {
                selectedPdfFile[0] = file;
                pdfFileLabel.setText(file.getName());
            }
        });
        
        // Cover Image Selection
        Label coverImageLabel = new Label("No cover image selected");
        Button coverImageButton = new Button("Choose Cover Image");
        ImageView coverPreview = new ImageView();
        coverPreview.setFitWidth(100);
        coverPreview.setFitHeight(140);
        coverPreview.setPreserveRatio(true);
        coverPreview.setStyle("-fx-border-color: #ccc; -fx-border-width: 1;");
        final File[] selectedCoverImage = {null};
        
        coverImageButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Cover Image");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            File file = fileChooser.showOpenDialog(dialog.getOwner());
            if (file != null) {
                selectedCoverImage[0] = file;
                coverImageLabel.setText(file.getName());
                // Show preview
                try {
                    javafx.scene.image.Image image = new javafx.scene.image.Image(file.toURI().toString());
                    coverPreview.setImage(image);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        
        VBox coverBox = new VBox(10);
        coverBox.getChildren().addAll(coverImageButton, coverImageLabel, coverPreview);
        
        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Author:"), 0, 1);
        grid.add(authorField, 1, 1);
        grid.add(new Label("Year:"), 0, 2);
        grid.add(yearField, 1, 2);
        grid.add(new Label("Category:"), 0, 3);
        grid.add(categoryCombo, 1, 3);
        grid.add(new Label("Description:"), 0, 4);
        grid.add(descriptionArea, 1, 4);
        grid.add(new Label("PDF File:"), 0, 5);
        grid.add(pdfFileButton, 1, 5);
        grid.add(pdfFileLabel, 1, 6);
        grid.add(new Label("Cover Image:"), 0, 7);
        grid.add(coverBox, 1, 7);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                if (titleField.getText().isEmpty() || authorField.getText().isEmpty() || 
                    yearField.getText().isEmpty() || categoryCombo.getValue() == null || selectedPdfFile[0] == null) {
                    showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill all required fields and select a PDF file.");
                    return null;
                }
                
                try {
                    Book book = new Book();
                    book.setTitle(titleField.getText());
                    book.setAuthor(authorField.getText());
                    book.setYearPublished(Integer.parseInt(yearField.getText()));
                    book.setCategoryId(categoryCombo.getValue().getCategoryId());
                    book.setDescription(descriptionArea.getText());
                    
                    // Copy PDF file to library directory
                    File libraryDir = new File("library/books");
                    if (!libraryDir.exists()) {
                        libraryDir.mkdirs();
                    }
                    
                    String pdfFileName = System.currentTimeMillis() + "_" + selectedPdfFile[0].getName();
                    File destPdfFile = new File(libraryDir, pdfFileName);
                    Files.copy(selectedPdfFile[0].toPath(), destPdfFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    book.setFilePath(destPdfFile.getPath());
                    
                    // Copy cover image if selected
                    if (selectedCoverImage[0] != null) {
                        File coversDir = new File("library/covers");
                        if (!coversDir.exists()) {
                            coversDir.mkdirs();
                        }
                        
                        String coverFileName = System.currentTimeMillis() + "_cover_" + selectedCoverImage[0].getName();
                        File destCoverFile = new File(coversDir, coverFileName);
                        Files.copy(selectedCoverImage[0].toPath(), destCoverFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        book.setCoverImagePath(destCoverFile.getPath());
                    } else {
                        book.setCoverImagePath(null);
                    }
                    
                    return book;
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to add book: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });
        
        Optional<Book> result = dialog.showAndWait();
        result.ifPresent(book -> {
            if (bookDAO.addBook(book)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book added successfully!");
                handleRefresh();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add book to database.");
            }
        });
    }
    
    /**
     * Handle edit book
     */
    @FXML
    private void handleEditBook() {
        Book selectedBook = booksTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a book to edit.");
            return;
        }
        editBook(selectedBook);
    }
    
    /**
     * Edit book dialog
     */
    private void editBook(Book book) {
        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle("Edit Book");
        dialog.setHeaderText("Edit book details");
        
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField titleField = new TextField(book.getTitle());
        TextField authorField = new TextField(book.getAuthor());
        TextField yearField = new TextField(String.valueOf(book.getYearPublished()));
        TextArea descriptionArea = new TextArea(book.getDescription());
        descriptionArea.setPrefRowCount(3);
        
        ComboBox<Category> categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll(categoryDAO.getAllCategories());
        categoryCombo.getItems().stream()
            .filter(c -> c.getCategoryId() == book.getCategoryId())
            .findFirst()
            .ifPresent(categoryCombo::setValue);
        
        // Cover Image Section
        Label coverImageLabel = new Label(book.getCoverImagePath() != null ? "Current cover" : "No cover image");
        Button coverImageButton = new Button("Change Cover Image");
        Button removeCoverButton = new Button("Remove Cover");
        ImageView coverPreview = new ImageView();
        coverPreview.setFitWidth(100);
        coverPreview.setFitHeight(140);
        coverPreview.setPreserveRatio(true);
        coverPreview.setStyle("-fx-border-color: #ccc; -fx-border-width: 1;");
        
        // Load existing cover if available
        if (book.getCoverImagePath() != null) {
            try {
                File coverFile = new File(book.getCoverImagePath());
                if (coverFile.exists()) {
                    javafx.scene.image.Image image = new javafx.scene.image.Image(coverFile.toURI().toString());
                    coverPreview.setImage(image);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        final File[] selectedCoverImage = {null};
        final boolean[] removeCover = {false};
        
        coverImageButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Cover Image");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            File file = fileChooser.showOpenDialog(dialog.getOwner());
            if (file != null) {
                selectedCoverImage[0] = file;
                removeCover[0] = false;
                coverImageLabel.setText(file.getName());
                // Show preview
                try {
                    javafx.scene.image.Image image = new javafx.scene.image.Image(file.toURI().toString());
                    coverPreview.setImage(image);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        
        removeCoverButton.setOnAction(e -> {
            removeCover[0] = true;
            selectedCoverImage[0] = null;
            coverPreview.setImage(null);
            coverImageLabel.setText("Cover will be removed");
        });
        
        HBox coverButtonBox = new HBox(10);
        coverButtonBox.getChildren().addAll(coverImageButton, removeCoverButton);
        
        VBox coverBox = new VBox(10);
        coverBox.getChildren().addAll(coverButtonBox, coverImageLabel, coverPreview);
        
        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Author:"), 0, 1);
        grid.add(authorField, 1, 1);
        grid.add(new Label("Year:"), 0, 2);
        grid.add(yearField, 1, 2);
        grid.add(new Label("Category:"), 0, 3);
        grid.add(categoryCombo, 1, 3);
        grid.add(new Label("Description:"), 0, 4);
        grid.add(descriptionArea, 1, 4);
        grid.add(new Label("Cover Image:"), 0, 5);
        grid.add(coverBox, 1, 5);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                book.setTitle(titleField.getText());
                book.setAuthor(authorField.getText());
                book.setYearPublished(Integer.parseInt(yearField.getText()));
                book.setCategoryId(categoryCombo.getValue().getCategoryId());
                book.setDescription(descriptionArea.getText());
                
                // Handle cover image
                try {
                    if (removeCover[0]) {
                        // Remove existing cover
                        if (book.getCoverImagePath() != null) {
                            File oldCover = new File(book.getCoverImagePath());
                            if (oldCover.exists()) {
                                oldCover.delete();
                            }
                        }
                        book.setCoverImagePath(null);
                    } else if (selectedCoverImage[0] != null) {
                        // Delete old cover if exists
                        if (book.getCoverImagePath() != null) {
                            File oldCover = new File(book.getCoverImagePath());
                            if (oldCover.exists()) {
                                oldCover.delete();
                            }
                        }
                        
                        // Copy new cover
                        File coversDir = new File("library/covers");
                        if (!coversDir.exists()) {
                            coversDir.mkdirs();
                        }
                        
                        String coverFileName = System.currentTimeMillis() + "_cover_" + selectedCoverImage[0].getName();
                        File destCoverFile = new File(coversDir, coverFileName);
                        Files.copy(selectedCoverImage[0].toPath(), destCoverFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        book.setCoverImagePath(destCoverFile.getPath());
                    }
                    // If neither remove nor new image selected, keep existing path
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to update cover image: " + e.getMessage());
                }
                
                return book;
            }
            return null;
        });
        
        Optional<Book> result = dialog.showAndWait();
        result.ifPresent(updatedBook -> {
            if (bookDAO.updateBook(updatedBook)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book updated successfully!");
                handleRefresh();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update book.");
            }
        });
    }
    
    /**
     * Handle delete book
     */
    @FXML
    private void handleDeleteBook() {
        Book selectedBook = booksTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a book to delete.");
            return;
        }
        deleteBook(selectedBook);
    }
    
    /**
     * Delete book with confirmation
     */
    private void deleteBook(Book book) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Book");
        confirm.setContentText("Are you sure you want to delete \"" + book.getTitle() + "\"?\nThis action cannot be undone.");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (bookDAO.deleteBook(book.getBookId())) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Book deleted successfully!");
                    handleRefresh();
                    
                    // Optionally delete the file
                    File bookFile = new File(book.getFilePath());
                    if (bookFile.exists()) {
                        bookFile.delete();
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete book.");
                }
            }
        });
    }
    
    /**
     * Handle manage categories
     */
    @FXML
    private void handleManageCategories() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Manage Categories");
        dialog.setHeaderText("Book Categories");
        
        VBox content = new VBox(10);
        content.setPrefWidth(400);
        content.setPrefHeight(400);
        
        ListView<Category> categoryList = new ListView<>();
        categoryList.getItems().addAll(categoryDAO.getAllCategories());
        categoryList.setPrefHeight(300);
        
        HBox buttonBox = new HBox(10);
        Button addBtn = new Button("Add Category");
        Button deleteBtn = new Button("Delete Selected");
        
        addBtn.setOnAction(e -> {
            TextInputDialog inputDialog = new TextInputDialog();
            inputDialog.setTitle("Add Category");
            inputDialog.setHeaderText("Enter category name:");
            inputDialog.showAndWait().ifPresent(name -> {
                if (!name.trim().isEmpty()) {
                    Category category = new Category();
                    category.setCategoryName(name.trim());
                    if (categoryDAO.addCategory(category)) {
                        categoryList.getItems().clear();
                        categoryList.getItems().addAll(categoryDAO.getAllCategories());
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Category added!");
                    }
                }
            });
        });
        
        deleteBtn.setOnAction(e -> {
            Category selected = categoryList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setContentText("Delete category \"" + selected.getCategoryName() + "\"?");
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        if (categoryDAO.deleteCategory(selected.getCategoryId())) {
                            categoryList.getItems().remove(selected);
                            showAlert(Alert.AlertType.INFORMATION, "Success", "Category deleted!");
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Error", "Cannot delete category. Books may be using it.");
                        }
                    }
                });
            }
        });
        
        buttonBox.getChildren().addAll(addBtn, deleteBtn);
        content.getChildren().addAll(categoryList, buttonBox);
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }
    
    /**
     * Handle view access logs
     */
    @FXML
    private void handleViewLogs() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Access Logs");
        alert.setHeaderText("Recent Access Logs");
        
        VBox content = new VBox(10);
        content.setPrefWidth(700);
        content.setPrefHeight(500);
        
        TableView<com.elibrary.models.AccessLog> table = new TableView<>();
        
        TableColumn<com.elibrary.models.AccessLog, String> studentCol = new TableColumn<>("Student");
        studentCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStudentName()));
        studentCol.setPrefWidth(150);
        
        TableColumn<com.elibrary.models.AccessLog, String> bookCol = new TableColumn<>("Book");
        bookCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBookTitle()));
        bookCol.setPrefWidth(250);
        
        TableColumn<com.elibrary.models.AccessLog, String> actionCol = new TableColumn<>("Action");
        actionCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAccessType()));
        actionCol.setPrefWidth(100);
        
        TableColumn<com.elibrary.models.AccessLog, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAccessDate().toString()));
        dateCol.setPrefWidth(180);
        
        table.getColumns().addAll(studentCol, bookCol, actionCol, dateCol);
        table.getItems().addAll(accessLogDAO.getAllAccessLogs());
        
        content.getChildren().add(table);
        alert.getDialogPane().setContent(content);
        alert.showAndWait();
    }
    
    /**
     * Handle refresh
     */
    @FXML
    private void handleRefresh() {
        loadStatistics();
        loadBooks();
        searchField.clear();
        statusLabel.setText("Data refreshed");
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
