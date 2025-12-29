package com.elibrary;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main application class for E-Library System
 * Entry point of the JavaFX application
 */
public class MainApp extends Application {
    
    private static Stage primaryStage;
    
    @Override
    public void start(Stage stage) {
        try {
            primaryStage = stage;
            
            // Load the login selection screen
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/LoginSelection.fxml"));
            
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            primaryStage.setTitle("E-Library Management System");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            primaryStage.setMaximized(true);  // Maximize window by default
            primaryStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error starting application: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Get the primary stage
     * @return Primary stage
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
    
    /**
     * Change the current scene
     * @param fxmlFile FXML file path
     * @param title Window title
     */
    public static void changeScene(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(fxmlFile));
            Parent root = loader.load();
            
            // Determine scene size based on the screen being loaded
            double width = 800;
            double height = 600;
            
            if (fxmlFile.contains("Dashboard")) {
                width = 1200;
                height = 750;
            }
            
            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(MainApp.class.getResource("/css/styles.css").toExternalForm());
            
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(width);
            primaryStage.setMinHeight(height);
            primaryStage.setMaximized(true);  // Keep window maximized
            primaryStage.centerOnScreen();
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error changing scene: " + e.getMessage());
            System.err.println("Attempted to load: " + fxmlFile);
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
