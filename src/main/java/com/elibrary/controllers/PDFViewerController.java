package com.elibrary.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Controller for embedded PDF viewer
 */
public class PDFViewerController {
    
    @FXML private Label titleLabel;
    @FXML private Label authorLabel;
    @FXML private Label pageLabel;
    @FXML private ImageView pdfImageView;
    @FXML private Button prevButton;
    @FXML private Button nextButton;
    @FXML private Button closeButton;
    @FXML private Slider zoomSlider;
    @FXML private ScrollPane scrollPane;
    
    private PDDocument document;
    private PDFRenderer pdfRenderer;
    private int currentPage = 0;
    private int totalPages = 0;
    private float currentZoom = 1.5f;
    private String bookTitle;
    private String bookAuthor;
    
    /**
     * Load PDF file
     */
    public void loadPDF(File pdfFile, String title, String author) {
        try {
            this.bookTitle = title;
            this.bookAuthor = author;
            
            // Load PDF document
            document = PDDocument.load(pdfFile);
            pdfRenderer = new PDFRenderer(document);
            totalPages = document.getNumberOfPages();
            
            // Update UI
            titleLabel.setText(title);
            authorLabel.setText("by " + author);
            
            // Show first page
            showPage(0);
            
            // Setup zoom slider
            zoomSlider.setMin(0.5);
            zoomSlider.setMax(3.0);
            zoomSlider.setValue(1.5);
            zoomSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                currentZoom = newVal.floatValue();
                showPage(currentPage);
            });
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load PDF: " + e.getMessage());
        }
    }
    
    /**
     * Show specific page
     */
    private void showPage(int pageIndex) {
        try {
            if (pageIndex < 0 || pageIndex >= totalPages) {
                return;
            }
            
            currentPage = pageIndex;
            
            // Render page as image with current zoom
            int dpi = (int)(72 * currentZoom);
            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(currentPage, dpi);
            
            // Convert BufferedImage to JavaFX Image
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            Image image = new Image(bais);
            
            // Display image
            pdfImageView.setImage(image);
            pdfImageView.setPreserveRatio(true);
            
            // Update page label
            pageLabel.setText("Page " + (currentPage + 1) + " of " + totalPages);
            
            // Update button states
            prevButton.setDisable(currentPage == 0);
            nextButton.setDisable(currentPage == totalPages - 1);
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to render page: " + e.getMessage());
        }
    }
    
    /**
     * Handle previous page
     */
    @FXML
    private void handlePrevPage() {
        if (currentPage > 0) {
            showPage(currentPage - 1);
        }
    }
    
    /**
     * Handle next page
     */
    @FXML
    private void handleNextPage() {
        if (currentPage < totalPages - 1) {
            showPage(currentPage + 1);
        }
    }
    
    /**
     * Handle first page
     */
    @FXML
    private void handleFirstPage() {
        showPage(0);
    }
    
    /**
     * Handle last page
     */
    @FXML
    private void handleLastPage() {
        showPage(totalPages - 1);
    }
    
    /**
     * Handle zoom in
     */
    @FXML
    private void handleZoomIn() {
        if (currentZoom < 3.0f) {
            currentZoom += 0.25f;
            zoomSlider.setValue(currentZoom);
            showPage(currentPage);
        }
    }
    
    /**
     * Handle zoom out
     */
    @FXML
    private void handleZoomOut() {
        if (currentZoom > 0.5f) {
            currentZoom -= 0.25f;
            zoomSlider.setValue(currentZoom);
            showPage(currentPage);
        }
    }
    
    /**
     * Handle close
     */
    @FXML
    private void handleClose() {
        try {
            if (document != null) {
                document.close();
            }
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Show error alert
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("PDF Viewer Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Cleanup when closing
     */
    public void cleanup() {
        try {
            if (document != null) {
                document.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
